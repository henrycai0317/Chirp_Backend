package com.example.chirp.infra.database.repositories

import com.example.chirp.infra.database.entities.EmailVerificationTokenEntity
import com.example.chirp.infra.database.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface EmailVerificationTokenRepository : JpaRepository<EmailVerificationTokenEntity, Long> {
    fun findByToken(token: String): EmailVerificationTokenEntity?
    fun deleteByExpiresAtLessThan(now: Instant)
    fun findByUserAndUserAtIsNull(user: UserEntity): List<EmailVerificationTokenEntity>
}