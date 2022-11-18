package com.test.yacupwalkietalkie.data.messages

enum class TypeSocketMessage {
    Voice,
    Location,
    Bye;

    fun getTypeInt() = values().indexOf(this)

    companion object {
        fun fromInt(index: Int) = values().getOrNull(index)
    }
}
