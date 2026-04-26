package com.example.chirp.api.dto

import jakarta.validation.constraints.Email

data class EmailRequest(
    @field:Email
    val email: String,
)
