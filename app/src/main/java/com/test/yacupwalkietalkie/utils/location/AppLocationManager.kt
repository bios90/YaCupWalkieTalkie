package com.test.yacupwalkietalkie.utils.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.test.yacupwalkietalkie.base.addLifeCycleObserver
import com.test.yacupwalkietalkie.base.safeLet
import com.test.yacupwalkietalkie.utils.permissions.PermissionsManager

class AppLocationManager(
    private val activity: AppCompatActivity,
    private val permissionsManager: PermissionsManager,
    private val onLocationUpdated: (Location) -> Unit
) {
    private var locationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    init {
        activity.addLifeCycleObserver(
            onResume = { startGettingLocation() },
            onPause = { stopGettingLocation() }
        )
    }

    @SuppressLint("MissingPermission")
    private fun startGettingLocation() {
        permissionsManager.checkPermissions {
            if (permissionsManager.areAllPermissionsGranted()) {
                locationClient = LocationServices.getFusedLocationProviderClient(activity)
                locationClient?.getLastLocation()?.addOnSuccessListener(onLocationUpdated)
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        locationResult.lastLocation?.let(onLocationUpdated)
                    }
                }

                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, MIN_TIME_INTERVAL)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(MIN_TIME_INTERVAL)
                    .setMinUpdateDistanceMeters(MIN_DISPLACEMENT)
                    .setMaxUpdateDelayMillis(MIN_TIME_INTERVAL * 2)
                    .build()

                safeLet(locationClient, locationCallback) { client, callback ->
                    client.requestLocationUpdates(
                        request,
                        callback,
                        Looper.getMainLooper()
                    )
                }
            }
        }
    }

    private fun stopGettingLocation() {
        safeLet(locationClient, locationCallback) { client, callback ->
            client.removeLocationUpdates(callback)
        }
    }

    companion object {
        private const val MIN_TIME_INTERVAL = 5000L
        private const val MIN_DISPLACEMENT = 3f

    }
}
