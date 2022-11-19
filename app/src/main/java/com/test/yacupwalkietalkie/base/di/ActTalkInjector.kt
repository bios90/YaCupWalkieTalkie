package com.test.yacupwalkietalkie.base.di

import android.location.Location
import android.net.wifi.p2p.WifiP2pDevice
import androidx.lifecycle.viewModelScope
import com.test.yacupwalkietalkie.base.BaseActivity
import com.test.yacupwalkietalkie.screens.ActTalk.ActTalkVm
import com.test.yacupwalkietalkie.utils.CompassOrientationManager
import com.test.yacupwalkietalkie.utils.audio.AudioManager
import com.test.yacupwalkietalkie.utils.audio.AudioRecordManager
import com.test.yacupwalkietalkie.utils.location.AppLocationManager
import com.test.yacupwalkietalkie.utils.wifi.WiFiDirectBroadcastReceiver
import com.test.yacupwalkietalkie.utils.wifi.createWifiDirectReceiver

class ActTalkInjector {

    private var audioRecordManager: AudioRecordManager? = null
    private var locationManager: AppLocationManager? = null
    private var compassManager: CompassOrientationManager? = null

    fun clear() {
        audioRecordManager = null
        locationManager = null
        compassManager = null
    }

    fun provideLocationManager(
        act: BaseActivity,
        onLocationUpdated: (Location) -> Unit
    ): AppLocationManager? {
        return locationManager ?: AppLocationManager(
            activity = act,
            permissionsManager = BaseDiInjector.providePermissionsManager(act),
            onLocationUpdated = onLocationUpdated
        ).also { this.locationManager = it }
    }

    fun provideAudioRecordManager(
        vm: ActTalkVm,
    ): AudioRecordManager? {
        return if (audioRecordManager != null) {
            audioRecordManager
        } else {
            val recorder = AudioManager.getAudioRecorder() ?: return null
            AudioRecordManager(
                recorder = recorder,
                scope = vm.viewModelScope,
            ).also {
                audioRecordManager = it
            }
        }
    }

    fun provideCompassManager(
        act: BaseActivity,
        locationsProvider: (() -> Pair<Location, Location>?),
        onRotationChanged: (Float) -> Unit
    ): CompassOrientationManager {
        return compassManager ?: CompassOrientationManager(
            activity = act,
            locationsProvider = locationsProvider,
            onRotationChanged = onRotationChanged
        ).also { this.compassManager = it }
    }
}
