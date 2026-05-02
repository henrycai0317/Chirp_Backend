package com.example.chirp.api.dto

import com.example.chirp.domain.type.ChatId
import com.example.chirp.domain.type.ChatMessageId
import com.example.chirp.domain.type.UserId
import java.time.Instant

data class ChatMessageDto(
    val id: ChatMessageId,
    val chatId: ChatId,
    val content: String,
    val createdAt: Instant,
    val senderId: UserId
)

