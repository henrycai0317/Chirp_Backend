package com.example.chirp.api.dto

import com.example.chirp.domain.type.UserId

data class ChatParticipantDto(
    val userId: UserId,
    val username: String,
    val email: String,
    val profilePictureUrl: String?
)
