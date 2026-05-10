package com.example.chirp.api.dto.ws

import com.example.chirp.domain.type.ChatId
import com.example.chirp.domain.type.ChatMessageId

data class SendMessageDto(
    val chatId: ChatId,
    val content: String,
    val messageId: ChatMessageId? = null,
)
