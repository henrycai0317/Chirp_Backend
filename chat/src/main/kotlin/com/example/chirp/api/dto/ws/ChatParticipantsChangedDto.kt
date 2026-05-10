package com.example.chirp.api.dto.ws

import com.example.chirp.domain.type.ChatId

data class ChatParticipantsChangedDto(
    val chatId: ChatId
)
