package com.example.chirp.domain.event

import com.example.chirp.domain.type.ChatId
import com.example.chirp.domain.type.UserId

data class ChatParticipantsJoinedEvent(
    val chatId: ChatId,
    val userIds: Set<UserId>
)