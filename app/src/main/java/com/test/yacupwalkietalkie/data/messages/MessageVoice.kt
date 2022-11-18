package com.test.yacupwalkietalkie.data.messages

data class MessageVoice(
    var data: ByteArray? = null
) : ISocketMessage {
    override fun getType(): TypeSocketMessage = TypeSocketMessage.Voice
    override fun writeToBytes(bytes: ByteArray, offset: Int) {
        data?.let {
            System.arraycopy(it, 0, bytes, offset, it.size)
        }
    }

    override fun getBytes(): ByteArray = data ?: ByteArray(0)

    override fun getLength(): Int = data?.size ?: 0

    override fun readFromBytes(bytes: ByteArray, length: Int) {
        this.data = bytes
    }

    override fun clear() {
        this.data = null
    }
}
