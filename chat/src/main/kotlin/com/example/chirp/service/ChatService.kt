package com.example.chirp.service

import com.example.chirp.domain.exception.ChatParticipantNotFoundException
import com.example.chirp.domain.exception.InvalidChatSizeException
import com.example.chirp.domain.models.Chat
import com.example.chirp.domain.type.UserId
import com.example.chirp.infra.database.entities.ChatEntity
import com.example.chirp.infra.database.mappers.toChat
import com.example.chirp.infra.database.repositories.ChatParticipantRepository
import com.example.chirp.infra.database.repositories.ChatRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
) {

    @Transactional
    fun createChat(
        creatorId: UserId,
        otherUserIds: Set<UserId>
    ): Chat {
        val otherParticipants = chatParticipantRepository.findByUserIdIn(
            userIds = otherUserIds
        )

        val allParticipants = (otherParticipants + creatorId)
        if (allParticipants.size < 2) {
            throw InvalidChatSizeException()
        }

        val creator = chatParticipantRepository.findByIdOrNull(creatorId)
            ?: throw ChatParticipantNotFoundException(creatorId)

        return chatRepository.saveAndFlush(
            ChatEntity(
                creator = creator,
                participants = setOf(creator) + otherParticipants
            )
        ).toChat(lastMessage = null)

    }
}