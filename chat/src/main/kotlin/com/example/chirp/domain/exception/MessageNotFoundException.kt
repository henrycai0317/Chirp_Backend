package com.example.chirp.domain.exception

import com.example.chirp.domain.type.ChatMessageId

class MessageNotFoundException(
    private val id: ChatMessageId
): RuntimeException(
    "Message with ID $id not found"
)