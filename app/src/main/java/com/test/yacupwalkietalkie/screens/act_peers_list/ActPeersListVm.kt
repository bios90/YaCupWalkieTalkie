package com.test.yacupwalkietalkie.screens.act_peers_list

import android.net.wifi.p2p.WifiP2pInfo
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.base.di.ActPeersListInjector
import com.test.yacupwalkietalkie.base.toSet
import com.test.yacupwalkietalkie.base.view_models.BaseEffectsData
import com.test.yacupwalkietalkie.base.view_models.BaseEffectsData.*
import com.test.yacupwalkietalkie.base.view_models.BaseViewModel
import com.test.yacupwalkietalkie.data.ModelConnectionData
import com.test.yacupwalkietalkie.data.ModelDevice
import com.test.yacupwalkietalkie.screens.ActTalk.ActTalk
import com.test.yacupwalkietalkie.screens.act_peers_list.ActPeersListVm.Effect.*
import com.test.yacupwalkietalkie.screens.act_welcome.ActWelcomeVm
import com.test.yacupwalkietalkie.utils.wifi.WifiConnectionManager
import com.test.yacupwalkietalkie.utils.wifi.createWifiDirectReceiver
import java.net.InetAddress

class ActPeersListVm : BaseViewModel<ActPeersListVm.State, ActPeersListVm.Effect>() {

    override val initialState: State = State(
        isWifiEnabled = false,
        connectedToDevice = false,
        devices = emptyList()
    )
    private val injector: ActPeersListInjector by lazy { ActPeersListInjector() }

    data class State(
        val isWifiEnabled: Boolean,
        val connectedToDevice: Boolean,
        val devices: List<ModelDevice>
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
            onP2pConnectionChanged = ::onP2pConnected
        )
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

    private fun onConnectionError() {

    }

    private fun onP2pConnected(data: ModelConnectionData) {
        if (currentState.connectedToDevice) {
            return
        }
        setStateResult(state = currentState.copy(connectedToDevice = true))
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
                        state = currentState,
                        effects = BaseEffectWrapper(Toast("Ошибка подключения(")).toSet()
                    )
                }
            )
        }

        fun clickedTurnOnWifi() {
            wifiConnectionManager?.tryTurnOnWifi()
        }
    }
}
