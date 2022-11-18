package com.test.yacupwalkietalkie.base.di

import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.data.ModelConnectionData
import com.test.yacupwalkietalkie.data.ModelDevice
import com.test.yacupwalkietalkie.screens.act_peers_list.ActPeersList
import com.test.yacupwalkietalkie.screens.act_peers_list.ActPeersListVm
import com.test.yacupwalkietalkie.utils.wifi.WifiConnectionManager

class ActPeersListInjector {

    fun provideWifiConnectionManager(
        act: BaseActivity,
        onPeersUpdated: (List<ModelDevice>) -> Unit,
        onWifiConnectionChanged: (Boolean) -> Unit,
        onConnectionError: () -> Unit,
        onP2pConnectionChanged: (ModelConnectionData) -> Unit
    ) = WifiConnectionManager(
        act = act,
        wifiManager = BaseDiInjector.provideWifiP2pManager(act),
        connectionChannel = BaseDiInjector.provideWifiP2pConnectionChannel(act),
        permissionManager = BaseDiInjector.providePermissionsManager(act),
        onPeersUpdated = onPeersUpdated,
        onWifiConnectionChanged = onWifiConnectionChanged,
        onConnectionError = onConnectionError,
        onP2pConnectionChanged = onP2pConnectionChanged
    )
}

