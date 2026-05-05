package com.example.chirp.domain.event

import com.example.chirp.domain.type.ChatId
import com.example.chirp.domain.type.UserId

data class ChatParticipantLeftEvent(
    val chatId: ChatId,
    val userId: UserId
)