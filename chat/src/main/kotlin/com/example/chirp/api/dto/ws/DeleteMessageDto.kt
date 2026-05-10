package com.example.chirp.api.dto.ws

import com.example.chirp.domain.type.ChatId
import com.example.chirp.domain.type.ChatMessageId

data class DeleteMessageDto(
    val chatId: ChatId,
    val messageId: ChatMessageId
)
