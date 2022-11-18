package com.test.yacupwalkietalkie.utils.sockets

import com.test.yacupwalkietalkie.data.messages.ISocketMessage
import com.test.yacupwalkietalkie.data.messages.MessageVoice
import com.test.yacupwalkietalkie.data.messages.TypeSocketMessage
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream

object SocketDataManger {
    private var dos: DataOutputStream? = null
    private var dis: DataInputStream? = null
    private var readBytes: ByteArray = ByteArray(0)
    private var typeInt: Short = 0
    private var length: Int = 0
    private var typeSocketMessage: TypeSocketMessage? = null
    private var messageReaded: ISocketMessage? = null

    fun sendMessage(msg: ISocketMessage, ops: OutputStream) {
        try {
            dos = DataOutputStream(ops)
            dos?.writeShort(msg.getType().getTypeInt())
            dos?.writeInt(msg.getLength())
            dos?.write(msg.getBytes())
            dos?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun readMessage(ips: InputStream): ISocketMessage? {
        try {
            dis = DataInputStream(ips)
            dis?.apply {
                typeInt = readShort()
                length = readInt()
                typeSocketMessage = TypeSocketMessage.fromInt(typeInt.toInt()) ?: return null
                readBytes = ByteArray(length)
                readFully(readBytes)
                messageReaded = when (typeSocketMessage) {
                    TypeSocketMessage.Voice -> MessageVoice()
                    TypeSocketMessage.Location -> TODO()
                    TypeSocketMessage.Bye -> MessageVoice()
                    null -> TODO()
                }
                messageReaded?.readFromBytes(readBytes, length)
                return messageReaded
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }
}
