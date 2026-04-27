package com.example.chirp.domain.model

import com.example.chirp.domain.type.UserId


data class User(
    val id: UserId,
    val username: String,
    val email: String,
    val hasEmailVerified: Boolean
)
