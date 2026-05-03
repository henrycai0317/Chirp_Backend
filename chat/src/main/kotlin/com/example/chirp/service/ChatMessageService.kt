package com.example.chirp.service

import com.example.chirp.domain.exception.ChatNotFoundException
import com.example.chirp.domain.exception.ChatParticipantNotFoundException
import com.example.chirp.domain.exception.ForbiddenException
import com.example.chirp.domain.exception.MessageNotFoundException
import com.example.chirp.domain.models.ChatMessage
import com.example.chirp.domain.type.ChatId
import com.example.chirp.domain.type.ChatMessageId
import com.example.chirp.domain.type.UserId
import com.example.chirp.infra.database.entities.ChatMessageEntity
import com.example.chirp.infra.database.mappers.toChatMessage
import com.example.chirp.infra.database.repositories.ChatMessageRepository
import com.example.chirp.infra.database.repositories.ChatParticipantRepository
import com.example.chirp.infra.database.repositories.ChatRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ChatMessageService(
    private val chatRepository: ChatRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val messageCacheManager: MessageCacheManager
) {

    @Transactional
    @CacheEvict(
        value = ["messages"],
        key = "#chatId",
    )
    fun sendMessage(
        chatId: ChatId,
        senderId: UserId,
        content: String,
        messageId: ChatMessageId? = null
    ): ChatMessage {
        val chat = chatRepository.findChatById(chatId, senderId)
            ?: throw ChatNotFoundException()
        val sender = chatParticipantRepository.findByIdOrNull(senderId)
            ?: throw ChatParticipantNotFoundException(senderId)

        val savedMessage = chatMessageRepository.saveAndFlush(
            ChatMessageEntity(
                id = messageId ?: UUID.randomUUID(),
                content = content.trim(),
                chatId = chatId,
                chat = chat,
                sender = sender
            )
        )

        return savedMessage.toChatMessage()
    }


    @Transactional
    fun deleteMessage(
        messageId: ChatMessageId,
        requestUserId: UserId
    ) {
        val message = chatMessageRepository.findByIdOrNull(messageId)
            ?: throw MessageNotFoundException(messageId)

        if(message.sender.userId != requestUserId) {
            throw ForbiddenException()
        }

        chatMessageRepository.delete(message)


        messageCacheManager.evictMessagesCache(message.chatId)
    }

}

@Component
class MessageCacheManager {
    @CacheEvict(
        value = ["messages"],
        key = "#chatId",
    )
    fun evictMessagesCache(chatId: ChatId) {
        // NO-OP: Let Spring handle the cache evict
    }
}