package com.nodex.allcrew.domain

import java.time.LocalDateTime

data class AdminChatMessage(
    val id: Long? = null,
    val roomId: Long,
    val senderType: String,
    val senderName: String,
    val content: String,
    val createdAt: LocalDateTime? = null,
)
