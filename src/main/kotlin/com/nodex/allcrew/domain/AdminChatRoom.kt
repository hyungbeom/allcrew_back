package com.nodex.allcrew.domain

import java.time.LocalDateTime

data class AdminChatRoom(
    val id: Long? = null,
    val agencyId: Long,
    val roomCode: String,
    val title: String,
    val preview: String,
    val roomTime: String,
    val roomType: String,
    val projectCode: String,
    val avatarText: String,
    val avatarColor: String,
    val createdAt: LocalDateTime? = null,
)
