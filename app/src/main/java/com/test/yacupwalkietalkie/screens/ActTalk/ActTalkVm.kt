package com.test.yacupwalkietalkie.screens.ActTalk

import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.lifecycle.viewModelScope
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.base.di.ActPeersListInjector
import com.test.yacupwalkietalkie.base.di.ActTalkInjector
import com.test.yacupwalkietalkie.base.putArgs
import com.test.yacupwalkietalkie.base.toSet
import com.test.yacupwalkietalkie.base.view_models.BaseEffectsData
import com.test.yacupwalkietalkie.base.view_models.BaseEffectsData.*
import com.test.yacupwalkietalkie.base.view_models.BaseViewModel
import com.test.yacupwalkietalkie.base.view_models.makeOnBackground
import com.test.yacupwalkietalkie.data.messages.ISocketMessage
import com.test.yacupwalkietalkie.data.messages.MessageBye
import com.test.yacupwalkietalkie.data.messages.MessageVoice
import com.test.yacupwalkietalkie.screens.ActTalk.ActTalkVm.Effect.BaseEffectWrapper
import com.test.yacupwalkietalkie.utils.audio.AudioManager
import com.test.yacupwalkietalkie.utils.sockets.SocketService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ActTalkVm(private val args: ActTalk.Args) : BaseViewModel<ActTalkVm.State, ActTalkVm.Effect>() {

    override val initialState: State = State(
        canRecord = true,
        isRecording = false
    )

    data class State(
        val isRecording: Boolean,
        val canRecord: Boolean
    )

    sealed class Effect {
        data class BaseEffectWrapper(val data: BaseEffectsData) : Effect()
    }

    private val injector by lazy { ActTalkInjector() }
    private val audioPlayer by lazy { AudioManager.getAudioPlayer() }

    init {
        initAudioPlayer()
        setEvents()
    }

    override fun onCreate(act: BaseActivity) {
        super.onCreate(act)
        startSocketService(act)
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

    private fun startSocketService(context: Context) {
        context.startService(
            Intent(context, SocketService::class.java)
                .apply { putArgs(args) }
        )
    }

    private fun handleMessage(message: ISocketMessage) {
        when (message) {
            is MessageBye -> handleByeMessage(message)
            is MessageVoice -> playVoiceMessage(message)
        }
    }

    private fun playVoiceMessage(voice: MessageVoice) {
        try {
            audioPlayer.write(voice.getBytes(), 0, voice.getLength())
            voice.clear()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun startRecording() {
        if (currentState.isRecording || currentState.canRecord.not()) {
            return
        }
        setStateResult(currentState.copy(isRecording = true))
        var message: MessageVoice?
        injector.provideAudioRecordManager(vm = this)
            ?.startRecording(
                onBytesReaded = { bytes ->
                    message = MessageVoice(bytes)
                    message?.let(SocketService::sendMessage)
                }
            )
    }

    private fun stopRecording() {
        if (currentState.isRecording.not()) {
            return
        }
        setStateResult(currentState.copy(isRecording = false))
        injector.provideAudioRecordManager(this)?.stopRecording()
    }

    private fun handleByeMessage(messageBye: MessageBye) {
        setStateResult(currentState, BaseEffectWrapper(Finish).toSet())
    }


    private fun handleLocationChanged(location: Location) {

    }

    inner class Listener {
        fun recordButtonDown() {
            startRecording()
        }

        fun recordButtonUp() {
            stopRecording()
        }
    }

}
