package com.test.yacupwalkietalkie.utils.wifi

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import com.test.yacupwalkietalkie.base.connectToAddress
import com.test.yacupwalkietalkie.base.makeActionDelayed
import com.test.yacupwalkietalkie.base.tryTurnWifiOn
import com.test.yacupwalkietalkie.data.ModelConnectionData
import com.test.yacupwalkietalkie.data.ModelDevice
import com.test.yacupwalkietalkie.utils.permissions.PermissionManager
import kotlinx.coroutines.Job

class WifiConnectionManager(
    private val act: AppCompatActivity,
    private val wifiManager: WifiP2pManager,
    private val connectionChannel: WifiP2pManager.Channel,
    private val permissionManager: PermissionManager,
    private val onPeersUpdated: (List<ModelDevice>) -> Unit,
    private val onWifiConnectionChanged: (Boolean) -> Unit,
    private val onConnectionError: () -> Unit,
    private val onP2pConnectionChanged: (ModelConnectionData) -> Unit
) {
    private var deviceToConnect: ModelDevice? = null
    private var connectionTimeoutJob: Job? = null

    init {
        createWifiDirectReceiver(
            act = act,
            onP2pStateChanged = ::handleWifiEnabledChange,
            onPeersChanged = ::reloadPeers,
            onConnectionChanged = ::handleP2pConnectionChanged
        )
        startSignaling()
    }

    fun connectToDevice(
        modelDevice: ModelDevice,
        onError: () -> Unit,
    ) {
        wifiManager.connectToAddress(
            channel = connectionChannel,
            address = modelDevice.macAddress,
            onError = {
                deviceToConnect = null
                onError.invoke()
            },
            onSuccess = {
                deviceToConnect = modelDevice
            }
        )
        connectionTimeoutJob = act.makeActionDelayed(
            delayTime = CONNECTION_TIMEOUT,
            action = {
                deviceToConnect = null
                cancelP2pConnection()
                onError.invoke()
            }
        )
    }

    private fun cancelP2pConnection() = wifiManager.cancelConnect(
        connectionChannel,
        object : WifiP2pManager.ActionListener {
            override fun onSuccess() = Unit
            override fun onFailure(p0: Int) = Unit
        }
    )

    @SuppressLint("MissingPermission")
    private fun startSignaling() {
        permissionManager.checkPermissions {
            if (permissionManager.areAllPermissionsGranted()) {
                wifiManager.discoverPeers(connectionChannel,
                    object : WifiP2pManager.ActionListener {
                        override fun onSuccess() = Unit

                        override fun onFailure(p0: Int) = onConnectionError.invoke()
                    }
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun reloadPeers() {
        permissionManager.checkPermissions {
            wifiManager.requestPeers(
                connectionChannel,
                {
                    it.deviceList.map {
                        ModelDevice(
                            macAddress = it.deviceAddress,
                            name = it.deviceName
                        )
                    }.let(onPeersUpdated)
                }
            )
        }
    }

    private fun handleWifiEnabledChange(isEnabled: Boolean) {
        onWifiConnectionChanged.invoke(isEnabled)
        if (isEnabled) {
            startSignaling()
        }
    }

    private fun handleP2pConnectionChanged(wifiP2pInfo: WifiP2pInfo?) {
        val p2pInfo = wifiP2pInfo ?: return
        val isConnected = p2pInfo.groupOwnerAddress != null
        if (isConnected) {
            val data = ModelConnectionData(
                groupOwnerAddress = p2pInfo.groupOwnerAddress,
                isGroupOwner = p2pInfo.isGroupOwner,
                deviceToConnect = deviceToConnect
            )
            onP2pConnectionChanged.invoke(data)
            deviceToConnect = null
            connectionTimeoutJob?.cancel()
            connectionTimeoutJob = null
        }
    }

    fun tryTurnOnWifi() = act?.tryTurnWifiOn()

    companion object {
        private const val CONNECTION_TIMEOUT = 5000L
    }
}
