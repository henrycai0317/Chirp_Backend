package com.example.chirp.api.util

import com.example.chirp.domain.exception.UnauthorizedException
import com.example.chirp.domain.model.UserId
import org.springframework.security.core.context.SecurityContextHolder

val requestUserId: UserId
    get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
        ?: throw UnauthorizedException()
