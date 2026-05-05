package com.example.chirp.domain.event

import com.example.chirp.domain.type.ChatId
import com.example.chirp.domain.type.ChatMessageId

data class MessageDeletedEvent(
    val chatId: ChatId,
    val messageId: ChatMessageId,
)