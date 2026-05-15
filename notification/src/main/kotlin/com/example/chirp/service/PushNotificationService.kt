package com.example.chirp.service

import com.example.chirp.domain.exception.InvalidDeviceTokenException
import com.example.chirp.domain.model.DeviceToken
import com.example.chirp.domain.model.PushNotification
import com.example.chirp.domain.type.ChatId
import com.example.chirp.domain.type.UserId
import com.example.chirp.infra.database.DeviceTokenEntity
import com.example.chirp.infra.database.DeviceTokenRepository
import com.example.chirp.infra.mapper.toDeviceToken
import com.example.chirp.infra.mapper.toPlatformEntity
import com.example.chirp.infra.push_notification.FirebasePushNotificationService
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service

@Service
class PushNotificationService(
    private val deviceTokenRepository: DeviceTokenRepository,
    private val firebasePushNotificationService: FirebasePushNotificationService,
) {

    private val logger = LoggerFactory.getLogger(javaClass)


    @Transactional
    fun registerDevice(
        userId: UserId,
        token: String,
        platform: DeviceToken.Platform,
    ): DeviceToken {
        val existing = deviceTokenRepository.findByToken(token)

        val trimmedToken = token.trim()
        if (existing == null && firebasePushNotificationService.isValidToken(trimmedToken).not()) {
            throw InvalidDeviceTokenException()
        }

        val entity = if (existing != null) {
            deviceTokenRepository.save(
                existing.apply {
                    this.userId = userId
                }
            )
        } else {
            deviceTokenRepository.save(
                DeviceTokenEntity(
                    userId = userId,
                    token = trimmedToken,
                    platform = platform.toPlatformEntity()
                )
            )
        }

        return entity.toDeviceToken()
    }

    @Transactional
    fun unregisterDevice(token: String) {
        deviceTokenRepository.deleteByToken(token.trim())
    }

    fun sendNewMessageNotifications(
        recipientUserIds: List<UserId>,
        senderUserId: UserId,
        senderUsername: String,
        message: String,
        chatId: ChatId
    ) {
        val deviceTokens = deviceTokenRepository.findByUserIdIn(recipientUserIds)
        if (deviceTokens.isEmpty()) {
            logger.info("No device tokens found for $recipientUserIds")
            return
        }

        val recipients = deviceTokens
            .filter { it.userId != senderUserId }
            .map { it.toDeviceToken() }

        val notification = PushNotification(
            title = "New message from $senderUsername",
            recipients = recipients,
            message = message,
            chatId = chatId,
            data = mapOf(
                "chatId" to chatId.toString(),
                "type" to "new_message"
            )
        )

        firebasePushNotificationService.sendNotification(notification)
    }


}