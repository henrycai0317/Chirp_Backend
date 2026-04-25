package com.example.chirp.infra.mappers

import com.example.chirp.domain.model.EmailVerificationToken
import com.example.chirp.infra.database.entities.EmailVerificationTokenEntity

fun EmailVerificationTokenEntity.toEmailVerificationToken(): EmailVerificationToken {
    return EmailVerificationToken(
        id = this.id,
        token = this.token,
        user = this.user.toUser()
    )
}