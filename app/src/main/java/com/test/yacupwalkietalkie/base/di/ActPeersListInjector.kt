package com.test.yacupwalkietalkie.base.di

import android.location.Location
import android.net.wifi.p2p.WifiP2pDevice
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.data.ModelConnectionData
import com.test.yacupwalkietalkie.data.ModelDevice
import com.test.yacupwalkietalkie.screens.act_peers_list.ActPeersList
import com.test.yacupwalkietalkie.screens.act_peers_list.ActPeersListVm
import com.test.yacupwalkietalkie.utils.location.AppLocationManager
import com.test.yacupwalkietalkie.utils.wifi.WifiConnectionManager

class ActPeersListInjector {

    private var locationManager: AppLocationManager? = null

    fun clear() {
        locationManager = null
    }


    fun provideWifiConnectionManager(
        act: BaseActivity,
        onPeersUpdated: (List<ModelDevice>) -> Unit,
        onWifiConnectionChanged: (Boolean) -> Unit,
        onConnectionError: () -> Unit,
        onP2pConnectionChanged: (ModelConnectionData) -> Unit,
    ) = WifiConnectionManager(
        act = act,
        wifiManager = BaseDiInjector.provideWifiP2pManager(),
        connectionChannel = BaseDiInjector.provideWifiP2pConnectionChannel(act),
        permissionsManager = BaseDiInjector.providePermissionsManager(act),
        onPeersUpdated = onPeersUpdated,
        onWifiConnectionChanged = onWifiConnectionChanged,
        onConnectionError = onConnectionError,
        onP2pConnectionChanged = onP2pConnectionChanged,
    )

    fun provideLocationManager(
        act: BaseActivity,
        onLocationUpdated: (Location) -> Unit
    ): AppLocationManager {
        return locationManager ?: AppLocationManager(
            activity = act,
            permissionsManager = BaseDiInjector.providePermissionsManager(act),
            onLocationUpdated = onLocationUpdated
        ).also { this.locationManager = it }
    }
}

