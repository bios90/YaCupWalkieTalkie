package com.test.yacupwalkietalkie.utils.sockets

import com.test.yacupwalkietalkie.utils.audio.AudioManager
import java.lang.Exception
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

object SocketConnectionManager {
    private const val PORT_USED = 9584
    private const val TIMEOUT = 3000

    fun startServerSocket(): Socket? {
        return try {
            val serverSocket = ServerSocket(PORT_USED)
            val socket = serverSocket.accept()
                .apply {
                    sendBufferSize = AudioManager.bufferRecordSize * 2
                    receiveBufferSize = AudioManager.bufferRecordSize * 2
                }
            socket
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun startAsClient(address: InetAddress): Socket? {
        return try {
            val socket = Socket()
                .apply {
                    sendBufferSize = AudioManager.bufferRecordSize * 2
                    receiveBufferSize = AudioManager.bufferRecordSize * 2
                }
            socket.connect(
                InetSocketAddress(address.hostAddress, PORT_USED),
                TIMEOUT
            )
            socket
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
