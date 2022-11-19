package com.test.yacupwalkietalkie.data.messages

import com.test.yacupwalkietalkie.base.safeLet
import com.test.yacupwalkietalkie.data.ModelLocation

data class MessageLocation(override var text: String = "") : ITextMessage {
    override fun getType(): TypeSocketMessage = TypeSocketMessage.Location

    fun toModelLocation(): ModelLocation? {
        val words = text.split("##")
        val name = words.getOrNull(0)
        val lat = words.getOrNull(1)?.toDoubleOrNull()
        val lon = words.getOrNull(2)?.toDoubleOrNull()
        return safeLet(name, lat, lon) { name, lat, lon ->
            ModelLocation(
                userName = name,
                lat = lat,
                lon = lon
            )
        }
    }

    companion object {
        fun fromModelLocation(location: ModelLocation): MessageLocation {
            val text = "${location.userName}##${location.lat}##${location.lon}"
            return MessageLocation(text)
        }
    }
}
