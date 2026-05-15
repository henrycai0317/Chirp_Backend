package com.example.chirp.infra.mapper

import com.example.chirp.domain.model.DeviceToken
import com.example.chirp.infra.database.DeviceTokenEntity

fun DeviceTokenEntity.toDeviceToken(): DeviceToken {
    return DeviceToken(
        userId = userId,
        token = token,
        platform = platform.toPlatform(),
        createdAt = createdAt,
        id = id
    )
}