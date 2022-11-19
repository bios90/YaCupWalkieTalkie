package com.test.yacupwalkietalkie.data.messages

data class MessageBye(override var text: String = "Bye-Bye") : ITextMessage {
    override fun getType() = TypeSocketMessage.Bye
}
