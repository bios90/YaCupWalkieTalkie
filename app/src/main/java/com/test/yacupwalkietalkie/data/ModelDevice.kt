package com.test.yacupwalkietalkie.data

import java.io.Serializable

data class ModelDevice(
    val macAddress: String,
    val name: String,
) : Serializable
