package com.test.yacupwalkietalkie.screens.act_peers_list

import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.base.di.ActPeersListInjector
import com.test.yacupwalkietalkie.base.toSet
import com.test.yacupwalkietalkie.base.tryGetOps
import com.test.yacupwalkietalkie.base.view_models.BaseEffectsData
import com.test.yacupwalkietalkie.base.view_models.BaseEffectsData.*
import com.test.yacupwalkietalkie.base.view_models.BaseViewModel
import com.test.yacupwalkietalkie.base.view_models.makeOnBackground
import com.test.yacupwalkietalkie.base.view_models.makeOnUi
import com.test.yacupwalkietalkie.data.ModelConnectionData
import com.test.yacupwalkietalkie.data.ModelDevice
import com.test.yacupwalkietalkie.data.messages.MessageBye
import com.test.yacupwalkietalkie.screens.ActTalk.ActTalk
import com.test.yacupwalkietalkie.screens.act_peers_list.ActPeersListVm.Effect.*
import com.test.yacupwalkietalkie.screens.act_welcome.ActWelcomeVm
import com.test.yacupwalkietalkie.utils.sockets.SocketDataManger
import com.test.yacupwalkietalkie.utils.sockets.SocketService
import com.test.yacupwalkietalkie.utils.wifi.WifiConnectionManager
import com.test.yacupwalkietalkie.utils.wifi.createWifiDirectReceiver
import kotlinx.coroutines.flow.last
import java.net.InetAddress

class ActPeersListVm : BaseViewModel<ActPeersListVm.State, ActPeersListVm.Effect>() {

    override val initialState: State = State(
        isWifiEnabled = false,
        connectedToDevice = false,
        devices = emptyList(),
        isLoading = false
    )
    private val injector = ActPeersListInjector()

    data class State(
        val isWifiEnabled: Boolean,
        val connectedToDevice: Boolean,
        val devices: List<ModelDevice>,
        val isLoading: Boolean
    )

    sealed class Effect {
        data class BaseEffectWrapper(val data: BaseEffectsData) : Effect()
    }

    private var wifiConnectionManager: WifiConnectionManager? = null

    override fun onCreate(act: BaseActivity) {
        super.onCreate(act)
        wifiConnectionManager = injector.provideWifiConnectionManager(
            act = act,
            onPeersUpdated = ::onPeersUpdated,
            onWifiConnectionChanged = ::onWifiConnectionChanged,
            onConnectionError = ::onConnectionError,
            onP2pConnectionChanged = ::onP2pConnected,
        )
        injector.provideLocationManager(
            act = act,
            onLocationUpdated = {}
        )
    }

    /*
    * Some crutch logic here because we need
    * 1) Send bye message
    * 2) Close socket
    * 3) Close wifi connection
    * */
    override fun onResume(act: BaseActivity) {
        super.onResume(act)
        if (currentState.connectedToDevice) {
            makeOnBackground {
                SocketService.sendMessageSync(MessageBye())
                SocketService.shutDown()
                val closeAction: (Boolean) -> Unit = { disconnectionError ->
                    val effects = setOfNotNull(
                        BaseEffectWrapper(
                            Toast("Ошибка при отключении( попробуйте выключить и включить Wifi вручную")
                        ).takeIf { disconnectionError }
                    )
                    makeOnUi {
                        setStateResult(currentState.copy(connectedToDevice = false), effects)
                    }
                    wifiConnectionManager?.restartSignaling()
                }
                wifiConnectionManager?.cancelP2pConnection(
                    onDisconnected = {
                        closeAction.invoke(false)
                    },
                    onDisconnectionError = {
                        closeAction.invoke(true)
                    }
                )
            }
        }
    }

    override fun onCleared() {
        injector.clear()
        super.onCleared()
    }

    private fun onPeersUpdated(peers: List<ModelDevice>) {
        setStateResult(currentState.copy(devices = peers))
    }

    private fun onWifiConnectionChanged(isConnected: Boolean) {
        val devices = if (isConnected) {
            currentState.devices
        } else {
            emptyList()
        }
        setStateResult(
            currentState.copy(
                isWifiEnabled = isConnected,
                devices = devices
            )
        )
    }

    private fun onConnectionError() = Unit

    private fun onP2pConnected(data: ModelConnectionData) {
        if (currentState.connectedToDevice) {
            return
        }
        setStateResult(
            state = currentState.copy(
                connectedToDevice = true,
                isLoading = false
            )
        )
        setStateResult(
            state = currentState,
            effects = BaseEffectWrapper(
                NavigateTo(
                    clazz = ActTalk::class.java,
                    args = ActTalk.Args(
                        connectionData = data
                    )
                )
            ).toSet()
        )
    }

    inner class Listener {
        fun onDeviceClicked(device: ModelDevice) {
            wifiConnectionManager?.connectToDevice(
                modelDevice = device,
                onError = {
                    setStateResult(
                        state = currentState.copy(isLoading = false),
                        effects = BaseEffectWrapper(Toast("Ошибка подключения(")).toSet()
                    )
                }
            )
            setStateResult(currentState.copy(isLoading = true))
        }

        fun clickedTurnOnWifi() {
            wifiConnectionManager?.tryTurnOnWifi()
        }
    }
}
