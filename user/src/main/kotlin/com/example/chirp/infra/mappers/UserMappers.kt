package com.example.chirp.infra.mappers

import com.example.chirp.domain.model.User
import com.example.chirp.infra.database.entities.UserEntity

fun UserEntity.toUser(): User {
    return User(
        id = id!!,
        username = username,
        email = email,
        hasEmailVerified = hasVerifiedEmail
    )

}