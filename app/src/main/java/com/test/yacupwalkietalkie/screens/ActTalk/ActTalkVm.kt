package com.test.yacupwalkietalkie.screens.ActTalk

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.lifecycle.viewModelScope
import com.test.yacupwalkietalkie.base.App
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.base.di.ActTalkInjector
import com.test.yacupwalkietalkie.base.putArgs
import com.test.yacupwalkietalkie.base.safeLet
import com.test.yacupwalkietalkie.base.toSet
import com.test.yacupwalkietalkie.base.view_models.BaseEffectsData
import com.test.yacupwalkietalkie.base.view_models.BaseEffectsData.*
import com.test.yacupwalkietalkie.base.view_models.BaseViewModel
import com.test.yacupwalkietalkie.base.view_models.makeOnBackground
import com.test.yacupwalkietalkie.base.view_models.makeOnUi
import com.test.yacupwalkietalkie.data.ModelLocation
import com.test.yacupwalkietalkie.data.messages.ISocketMessage
import com.test.yacupwalkietalkie.data.messages.MessageBye
import com.test.yacupwalkietalkie.data.messages.MessageIsSpeaking
import com.test.yacupwalkietalkie.data.messages.MessageLocation
import com.test.yacupwalkietalkie.data.messages.MessageVoice
import com.test.yacupwalkietalkie.screens.ActTalk.ActTalkVm.Effect.BaseEffectWrapper
import com.test.yacupwalkietalkie.utils.CoroutineDebouncer
import com.test.yacupwalkietalkie.utils.CoroutineThrottler
import com.test.yacupwalkietalkie.utils.audio.AudioManager
import com.test.yacupwalkietalkie.utils.sockets.SocketService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ActTalkVm(private val args: ActTalk.Args) : BaseViewModel<ActTalkVm.State, ActTalkVm.Effect>() {

    override val initialState: State = State(
        isCompanionSpeaking = false,
        isRecording = false,
        distance = 0f,
        userLocation = null,
        companionLocationData = null,
        compassRotation = 0f,
        isRationMode = false,
        showDismissDialog = false
    )

    data class State(
        val isRecording: Boolean,
        val isCompanionSpeaking: Boolean,
        val distance: Float?,
        val userLocation: Location?,
        val companionLocationData: ModelLocation?,
        val compassRotation: Float,
        val isRationMode: Boolean,
        val showDismissDialog: Boolean,
    )

    sealed class Effect {
        data class BaseEffectWrapper(val data: BaseEffectsData) : Effect()
    }

    private val injector by lazy { ActTalkInjector() }
    private val audioPlayer by lazy { AudioManager.getAudioPlayer() }

    //Debouncer to turn off companion speaking after 10 seconds if MessageIsSpeaking lost
    private var debouncerCompanionSpeaking = CoroutineDebouncer(COMPANION_IS_SPEAKING_DELAY, viewModelScope)

    //Debouncer to to finish talk if no messages received
    private val debouncerFinishOnNoMessages = CoroutineDebouncer(CONNECTION_TIMEOUT_DELAY, viewModelScope)

    //Throttler to make ui compass updates nut so ofter
    private val throttlerCompassChange = CoroutineThrottler(COMPASS_CHANGE_DELAY, viewModelScope, true)

    init {
        initAudioPlayer()
        setEvents()
        startSocketService(App.app)
    }

    override fun onCreate(act: BaseActivity) {
        super.onCreate(act)
        injector.provideLocationManager(
            act = act,
            onLocationUpdated = ::handleLocationUpdated
        )
        injector.provideCompassManager(
            act = act,
            locationsProvider = { provideLocationPair() },
            onRotationChanged = ::handleRotationChanged
        )
        debounceFinishOnNoMessage()
    }


    override fun onDestroy(act: BaseActivity) {
        injector.clear()
        stopRecording()
        super.onDestroy(act)
    }

    private fun initAudioPlayer() {
        makeOnBackground {
            audioPlayer.play()
        }
    }

    private fun setEvents() {
        SocketService
            .flowMessagesReceived
            .onEach(::handleMessage)
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private fun debounceFinishOnNoMessage() = debouncerFinishOnNoMessages.makeAction {
        makeOnUi {
            setStateResult(currentState, BaseEffectWrapper(Finish).toSet())
        }
    }

    private fun debounceCompanionSpeaking() = debouncerCompanionSpeaking.makeAction {
        makeOnUi {
            setStateResult(currentState.copy(isCompanionSpeaking = false))
        }
    }


    private fun provideLocationPair(): Pair<Location, Location>? {
        return safeLet(
            currentState.userLocation,
            currentState.companionLocationData?.toLocation()
        ) { userLocation, companionLocation ->
            Pair(userLocation, companionLocation)
        }
    }

    private fun handleLocationUpdated(location: Location) {
        setStateResult(
            state = currentState.copy(
                userLocation = location
            )
        )
        val deviceName = args.connectionData.thisDeviceName
        val modelLocation = ModelLocation(
            userName = deviceName,
            lat = location.latitude,
            lon = location.longitude
        )
        SocketService.sendMessage(MessageLocation.fromModelLocation(modelLocation))
    }

    private fun handleRotationChanged(rotation: Float) {
        makeOnUi {
            setStateResult(
                state = currentState.copy(compassRotation = rotation)
            )
        }
    }

    private fun startSocketService(context: Context) {
        context.startService(
            Intent(context, SocketService::class.java)
                .apply { putArgs(args) }
        )
    }

    private fun handleMessage(message: ISocketMessage) {
        when (message) {
            is MessageBye -> handleByeMessage(message)
            is MessageVoice -> handleVoiceMessage(message)
            is MessageLocation -> handleLocationMessage(message)
            is MessageIsSpeaking -> handleCompanionIsSpeakingMessage(message)
        }
        debounceFinishOnNoMessage()
        debounceCompanionSpeaking()
    }

    private fun startRecording() {
        if (currentState.isRecording || currentState.isCompanionSpeaking) {
            return
        }
        setStateResult(currentState.copy(isRecording = true))
        SocketService.sendMessage(MessageIsSpeaking(text = true.toString()))
        var message: MessageVoice?
        injector.provideAudioRecordManager(vm = this)
            ?.startRecording(
                onBytesReaded = { bytes ->
                    message = MessageVoice(bytes)
                    message?.let(SocketService::sendMessageSync)
                }
            )
    }

    private fun stopRecording() {
        SocketService.sendMessage(MessageIsSpeaking(text = false.toString()))
        setStateResult(currentState.copy(isRecording = false))
        injector.provideAudioRecordManager(this)?.stopRecording()
    }

    private fun recountLocations() {
        throttlerCompassChange.makeAction {
            makeOnUi {
                val userLocation = currentState.userLocation ?: return@makeOnUi
                val companionLocation = currentState.companionLocationData?.toLocation() ?: return@makeOnUi
                val distance = userLocation.distanceTo(companionLocation)
                setStateResult(
                    state = currentState.copy(
                        distance = distance
                    )
                )
            }
        }
    }

    private fun handleVoiceMessage(voice: MessageVoice) {
        try {
            audioPlayer.write(voice.getBytes(), 0, voice.getLength())
            voice.clear()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun handleByeMessage(messageBye: MessageBye) {
        makeOnUi {
            setStateResult(currentState, BaseEffectWrapper(Finish).toSet())
        }
    }

    private fun handleLocationMessage(messageLocation: MessageLocation) {
        val locationData = messageLocation.toModelLocation() ?: return
        makeOnUi {
            setStateResult(
                state = currentState.copy(
                    companionLocationData = locationData
                )
            )
        }
        recountLocations()
    }

    private fun handleCompanionIsSpeakingMessage(message: MessageIsSpeaking) {
        val isSpeaking = message.text.toBoolean()
        makeOnUi {
            setStateResult(
                state = currentState.copy(
                    isCompanionSpeaking = isSpeaking
                )
            )
        }
        debounceCompanionSpeaking()
    }

    inner class Listener {
        fun recordButtonDown() {
            startRecording()
        }

        fun recordButtonUp() {
            stopRecording()
        }

        fun onRationModeToggled(isEnabled: Boolean) {
            setStateResult(
                state = currentState.copy(
                    isRationMode = isEnabled
                )
            )
        }

        fun onBackPressed() {
            setStateResult(
                state = currentState.copy(
                    showDismissDialog = true
                )
            )
        }

        fun onStayMoreClicked() {
            setStateResult(
                state = currentState.copy(
                    showDismissDialog = false
                )
            )
        }

        fun onDismissClicked() {
            setStateResult(
                currentState,
                BaseEffectWrapper(Finish).toSet()
            )
        }
    }

    companion object {
        private const val COMPANION_IS_SPEAKING_DELAY = 5000L
        private const val CONNECTION_TIMEOUT_DELAY = 90000L
        private const val COMPASS_CHANGE_DELAY = 1000L
    }
}
