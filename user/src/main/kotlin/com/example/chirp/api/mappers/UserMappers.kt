package com.example.chirp.api.mappers

import com.example.chirp.api.dto.AuthenticatedUserDto
import com.example.chirp.api.dto.UserDto
import com.example.chirp.domain.model.AuthenticatedUser
import com.example.chirp.domain.model.User


fun AuthenticatedUser.toAuthenticatedUserDto(): AuthenticatedUserDto {
    return AuthenticatedUserDto(
        user = user.toUserDto(),
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}

fun User.toUserDto(): UserDto {
    return UserDto(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasEmailVerified,
    )
}