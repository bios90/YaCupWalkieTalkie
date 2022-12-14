package com.test.yacupwalkietalkie.utils.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import androidx.appcompat.app.AppCompatActivity
import com.test.yacupwalkietalkie.base.addLifeCycleObserver

fun createWifiDirectReceiver(
    act: AppCompatActivity,
    onP2pStateChanged: ((Boolean) -> Unit)? = null,
    onPeersChanged: (() -> Unit)? = null,
    onConnectionChanged: ((WifiP2pInfo?) -> Unit)? = null,
    onThisDeviceLoaded: ((WifiP2pDevice) -> Unit)? = null
): WiFiDirectBroadcastReceiver {
    val intentFilter = IntentFilter()
        .apply {
            onP2pStateChanged?.let { WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.let(::addAction) }
            onPeersChanged?.let { WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.let(::addAction) }
            onConnectionChanged?.let { WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.let(::addAction) }
            onThisDeviceLoaded?.let { WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.let(::addAction) }
        }
    val receiver = WiFiDirectBroadcastReceiver(
        onP2pStateChanged = onP2pStateChanged ?: {},
        onPeersChanged = onPeersChanged ?: {},
        onConnectionChanged = onConnectionChanged ?: {},
        onThisDeviceChanged = onThisDeviceLoaded ?: {}
    )
    act.addLifeCycleObserver(
        onResume = {
            act.registerReceiver(receiver, intentFilter)
        },
        onPause = {
            act.unregisterReceiver(receiver)
        },
        onDestroy = {
            receiver.abortBroadcast()
        }
    )
    return receiver
}

class WiFiDirectBroadcastReceiver(
    private val onP2pStateChanged: (Boolean) -> Unit,
    private val onPeersChanged: () -> Unit,
    private val onConnectionChanged: (WifiP2pInfo?) -> Unit,
    private val onThisDeviceChanged: (WifiP2pDevice) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val p2pState = intent.getIntExtra(
                    WifiP2pManager.EXTRA_WIFI_STATE,
                    WifiP2pManager.WIFI_P2P_STATE_DISABLED
                )
                val isEnabled = p2pState == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                onP2pStateChanged.invoke(isEnabled)
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> onPeersChanged.invoke()
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                val wifiP2pInfo =
                    intent.getParcelableExtra<WifiP2pInfo>(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
                onConnectionChanged.invoke(wifiP2pInfo)
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                val device = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                device?.let(onThisDeviceChanged)
            }
        }
    }
}
