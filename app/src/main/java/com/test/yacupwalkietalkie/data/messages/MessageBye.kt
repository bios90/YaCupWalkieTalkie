package com.test.yacupwalkietalkie.data.messages

data class MessageBye(override var text: String = "Bye-Bye") : IMessageText {
    override fun getType() = TypeSocketMessage.Bye
}
