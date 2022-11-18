package com.test.yacupwalkietalkie.utils.sockets

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.test.yacupwalkietalkie.base.closeQuietly
import com.test.yacupwalkietalkie.base.getArgs
import com.test.yacupwalkietalkie.base.makeOnBackgroundGlobal
import com.test.yacupwalkietalkie.base.safe
import com.test.yacupwalkietalkie.base.tryGetIps
import com.test.yacupwalkietalkie.base.tryGetOps
import com.test.yacupwalkietalkie.data.messages.ISocketMessage
import com.test.yacupwalkietalkie.data.messages.MessageBye
import com.test.yacupwalkietalkie.screens.ActTalk.ActTalk
import com.test.yacupwalkietalkie.screens.ActTalk.ActTalkVm
import com.test.yacupwalkietalkie.utils.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.net.Socket

class SocketService : Service() {
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var connectionJob: Job? = null
    private var messageListeningJob: Job? = null

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startNotification()
        setEvents()
    }

    private fun startNotification() = startForeground(
        NotificationHelper.SOCKET_NOTIFICATION_ID,
        NotificationHelper.showSocketListeningNotification()
    )

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val args = intent?.getArgs<ActTalk.Args>() ?: return super.onStartCommand(intent, flags, startId)
        if (isConnected().not()) {
            startSocketConnection(args)
        }
        return START_NOT_STICKY
    }

    private fun isConnected() = socket != null
            && socket?.isConnected.safe()
            && connectionJob?.isActive.safe()

    private fun startSocketConnection(args: ActTalk.Args) {
        connectionJob?.cancel()
        connectionJob = serviceScope.launch(
            context = Dispatchers.IO,
            block = {
                while (true) {
                    yield()
                    try {
                        if (socket == null || socket?.isConnected?.not() == true) {
                            if (args.connectionData.isGroupOwner) {
                                socket = SocketConnectionManager.startServerSocket()
                            } else {
                                socket =
                                    SocketConnectionManager.startAsClient(args.connectionData.groupOwnerAddress)
                            }
                            socket?.let(_flowSocket::tryEmit)
                            startMessagesListening()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    delay(1000)
                }
            }
        )
    }

    private fun startMessagesListening() {
        messageListeningJob?.cancel()
        messageListeningJob = serviceScope.launch(
            context = Dispatchers.IO,
            block = {
                val fis = socket?.tryGetIps()
                if (fis != null) {
                    while (true) {
                        yield()
                        val message = SocketDataManger.readMessage(fis)
                        message?.let(_flowMessagesReceived::tryEmit)
                    }
                }
            }
        )
    }

    private fun setEvents() {
        flowShutDown
            .onEach {
                socket?.tryGetOps()?.let { stream ->
                    SocketDataManger.sendMessage(MessageBye(), stream)
                }
                socket?.closeQuietly()
                stopSelf()
            }
            .flowOn(Dispatchers.IO)
            .launchIn(serviceScope)

        flowMessageToSend
            .onEach { message ->
                socket?.tryGetOps()?.let { stream ->
                    SocketDataManger.sendMessage(message, stream)
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(serviceScope)
    }


    override fun onDestroy() {
        socket?.close()
        connectionJob?.cancel()
        connectionJob = null
        messageListeningJob?.cancel()
        messageListeningJob = null
        serviceJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        //Socket object
        private var socket: Socket? = null
        private val _flowSocket = MutableSharedFlow<Socket>(
            replay = 1,
            extraBufferCapacity = 1,
        )
        val flowSocket = _flowSocket.asSharedFlow()

        //Messages receiving
        private val _flowMessagesReceived = MutableSharedFlow<ISocketMessage>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.SUSPEND
        )
        val flowMessagesReceived = _flowMessagesReceived.asSharedFlow()

        //Sending messages
        private val flowMessageToSend = MutableSharedFlow<ISocketMessage>()
        fun sendMessage(message: ISocketMessage) = makeOnBackgroundGlobal { flowMessageToSend.emit(message) }

        //Closing socket
        private val flowShutDown = MutableSharedFlow<Unit>()
        fun shutDown() = makeOnBackgroundGlobal {
            flowShutDown.emit(Unit)
        }
    }

}
