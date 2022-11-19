package com.test.yacupwalkietalkie.data

import android.location.Location

data class ModelLocation(
    val userName: String,
    val lat: Double,
    val lon: Double
) {
    fun toLocation() = Location("")
        .apply {
            latitude = lat
            longitude = lon
        }
}
