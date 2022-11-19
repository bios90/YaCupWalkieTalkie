package com.test.yacupwalkietalkie.base.di

import android.content.Context.WIFI_P2P_SERVICE
import android.location.Location
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import com.test.yacupwalkietalkie.base.App
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.utils.location.AppLocationManager
import com.test.yacupwalkietalkie.utils.permissions.PermissionsManager
import com.test.yacupwalkietalkie.utils.resources.StringsProvider

object BaseDiInjector {

    fun providePermissionsManager(act: BaseActivity): PermissionsManager = PermissionsManager(act)
    fun provideStringsProvider(act: BaseActivity): StringsProvider = StringsProvider(act)
    fun provideWifiP2pManager(): WifiP2pManager = App.app.getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
    fun provideWifiP2pConnectionChannel(act: BaseActivity) =
        provideWifiP2pManager().initialize(act, Looper.getMainLooper(), null)
}
