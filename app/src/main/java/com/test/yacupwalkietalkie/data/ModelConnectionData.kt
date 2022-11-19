package com.test.yacupwalkietalkie.data

import java.io.Serializable
import java.net.InetAddress

data class ModelConnectionData(
    val isGroupOwner: Boolean,
    val groupOwnerAddress: InetAddress,
    val deviceToConnect: ModelDevice?,
    val thisDeviceName: String
) : Serializable
