package com.test.yacupwalkietalkie.data.messages

interface ISocketMessage {
    fun getType(): TypeSocketMessage
    fun readFromBytes(bytes: ByteArray, length: Int)
    fun clear()
    fun writeToBytes(bytes: ByteArray, offset: Int)
    fun getBytes(): ByteArray
    fun getLength(): Int
}
