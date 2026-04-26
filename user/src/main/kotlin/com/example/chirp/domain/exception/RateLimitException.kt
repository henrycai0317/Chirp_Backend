package com.example.chirp.domain.exception

class RateLimitException(
    val resultsInSeconds: Long
): RuntimeException(
    "Rate limit exceeded. Please try again in $resultsInSeconds seconds."
) {
}