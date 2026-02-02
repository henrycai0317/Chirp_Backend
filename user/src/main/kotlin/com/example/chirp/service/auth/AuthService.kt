package com.example.chirp.service.auth

import com.example.chirp.domain.exception.UserAlreadyExistsException
import com.example.chirp.domain.model.User
import com.example.chirp.infra.database.entities.UserEntity
import com.example.chirp.infra.database.repositories.UserRepository
import com.example.chirp.infra.mappers.toUser
import com.example.chirp.infra.security.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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
}