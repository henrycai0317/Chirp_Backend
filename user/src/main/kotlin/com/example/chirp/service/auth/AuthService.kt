package com.example.chirp.service.auth

import com.example.chirp.domain.exception.InvalidCredentialsException
import com.example.chirp.domain.exception.InvalidTokenException
import com.example.chirp.domain.exception.UserAlreadyExistsException
import com.example.chirp.domain.exception.UserNotFoundException
import com.example.chirp.domain.model.AuthenticatedUser
import com.example.chirp.domain.model.User
import com.example.chirp.domain.model.UserId
import com.example.chirp.infra.database.entities.RefreshTokenEntity
import com.example.chirp.infra.database.entities.UserEntity
import com.example.chirp.infra.database.repositories.RefreshTokenRepository
import com.example.chirp.infra.database.repositories.UserRepository
import com.example.chirp.infra.mappers.toUser
import com.example.chirp.infra.security.PasswordEncoder
import com.example.chirp.service.JwtService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Base64

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    fun register(email: String, username: String, password: String): User {
        val user = userRepository.findByEmailOrUsername(
            email = email.trim(),
            username = username.trim()
        )

        if (user != null) {
            throw UserAlreadyExistsException()
        }

        val saveUser = userRepository.save(
            UserEntity(
                email = email.trim(),
                username = username.trim(),
                hashedPassword = passwordEncoder.encode(password.trim())
                    ?: throw IllegalStateException("Password encoding failed")
            )
        ).toUser()

        return saveUser
    }

    fun login(
        email: String,
        password: String
    ): AuthenticatedUser {
        val user = userRepository.findByEmail(email.trim()) ?: throw InvalidCredentialsException()

        if (passwordEncoder.matches(password, user.hashedPassword).not()) {
            throw InvalidCredentialsException()
        }

        //TODO: Check for verified email


        return user.id?.let { userId ->
            val accessToken = jwtService.generateAccessToken(userId)
            val refreshToken = jwtService.generateRefreshToken(userId)
            storeRefreshToken(userId, refreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } ?: throw UserNotFoundException()

    }

    @Transactional
    fun refresh(refreshToken: String): AuthenticatedUser {
        if(jwtService.validateRefreshToken(refreshToken).not()) {
            throw InvalidTokenException(
                message = "Invalid Refresh token"
            )
        }

        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        val hashed = hashedToken(refreshToken)

        return user.id?.let { userId ->
            refreshTokenRepository.findByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashed
            ) ?: throw InvalidTokenException("Invalid refresh token")

            refreshTokenRepository.deleteByUserIdAndHashedToken(
                userId = userId,
                hashedToken = hashed
            )

            val newAccessToken = jwtService.generateAccessToken(userId)
            val newRefreshToken = jwtService.generateRefreshToken(userId)

            storeRefreshToken(userId, newRefreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = newAccessToken,
                refreshToken = newRefreshToken
            )
        } ?: throw UserNotFoundException()
    }

    private fun storeRefreshToken(userId: UserId, token: String) {
        val hashed = hashedToken(token)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    private fun hashedToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }


}