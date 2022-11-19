package com.test.yacupwalkietalkie.utils.wifi

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ActionListener
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener
import androidx.appcompat.app.AppCompatActivity
import com.test.yacupwalkietalkie.base.connectToAddress
import com.test.yacupwalkietalkie.base.makeActionDelayed
import com.test.yacupwalkietalkie.base.tryTurnWifiOn
import com.test.yacupwalkietalkie.data.ModelConnectionData
import com.test.yacupwalkietalkie.data.ModelDevice
import com.test.yacupwalkietalkie.utils.permissions.PermissionsManager
import kotlinx.coroutines.Job

class WifiConnectionManager(
    private val act: AppCompatActivity,
    private val wifiManager: WifiP2pManager,
    private val connectionChannel: WifiP2pManager.Channel,
    private val permissionsManager: PermissionsManager,
    private val onPeersUpdated: (List<ModelDevice>) -> Unit,
    private val onWifiConnectionChanged: (Boolean) -> Unit,
    private val onConnectionError: () -> Unit,
    private val onP2pConnectionChanged: (ModelConnectionData) -> Unit,
) {
    private var deviceToConnect: ModelDevice? = null
    private var connectionTimeoutJob: Job? = null
    private var thisWifiP2pDevice: WifiP2pDevice? = null

    init {
        createWifiDirectReceiver(
            act = act,
            onP2pStateChanged = ::handleWifiEnabledChange,
            onPeersChanged = ::reloadPeers,
            onConnectionChanged = ::handleP2pConnectionChanged,
            onThisDeviceLoaded = ::handleThisDeviceLoaded
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
                onError.invoke()
            }
        )
    }

    @SuppressLint("MissingPermission")
    fun cancelP2pConnection(
        onDisconnected: () -> Unit,
        onDisconnectionError: () -> Unit
    ) {
        deviceToConnect = null
        wifiManager.requestGroupInfo(
            connectionChannel,
            object : GroupInfoListener {
                override fun onGroupInfoAvailable(group: WifiP2pGroup?) {
                    if (group != null) {
                        wifiManager.removeGroup(
                            connectionChannel,
                            object : ActionListener {
                                override fun onSuccess() {
                                    onDisconnected.invoke()
                                }

                                override fun onFailure(p0: Int) = onDisconnectionError.invoke()
                            }
                        )
                    } else {
                        onDisconnected.invoke()
                    }
                }
            }
        )
    }

    fun restartSignaling() = startSignaling()

    @SuppressLint("MissingPermission")
    private fun startSignaling() {
        wifiManager.discoverPeers(connectionChannel,
            object : ActionListener {
                override fun onSuccess() {
                    reloadPeers()
                }

                override fun onFailure(p0: Int) = onConnectionError.invoke()
            }
        )
    }

    @SuppressLint("MissingPermission")
    private fun reloadPeers() {
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

    private fun handleWifiEnabledChange(isEnabled: Boolean) {
        onWifiConnectionChanged.invoke(isEnabled)
        if (isEnabled) {
            startSignaling()
        }

    }

    private fun handleThisDeviceLoaded(device: WifiP2pDevice) {
        thisWifiP2pDevice = device
    }

    private fun handleP2pConnectionChanged(wifiP2pInfo: WifiP2pInfo?) {
        val p2pInfo = wifiP2pInfo ?: return
        val isConnected = p2pInfo.groupOwnerAddress != null
        if (isConnected) {
            val data = ModelConnectionData(
                groupOwnerAddress = p2pInfo.groupOwnerAddress,
                isGroupOwner = p2pInfo.isGroupOwner,
                deviceToConnect = deviceToConnect,
                thisDeviceName = thisWifiP2pDevice?.deviceName ?: ""
            )
            onP2pConnectionChanged.invoke(data)
            deviceToConnect = null
            connectionTimeoutJob?.cancel()
            connectionTimeoutJob = null
        }
    }

    fun tryTurnOnWifi() = act?.tryTurnWifiOn()

    companion object {
        private const val CONNECTION_TIMEOUT = 20000L
    }
}
