package com.test.yacupwalkietalkie.data.messages

data class MessageIsSpeaking(override var text: String = true.toString()) : ITextMessage {
    override fun getType() = TypeSocketMessage.IsTalking
}
