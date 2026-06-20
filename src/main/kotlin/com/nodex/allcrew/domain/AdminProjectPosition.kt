package com.nodex.allcrew.domain

import java.time.LocalDateTime

data class AdminProjectPosition(
    val id: Long? = null,
    val projectId: Long,
    val name: String,
    val headcount: Int,
    val payType: String,
    val amount: Int,
    val sortOrder: Int = 0,
    val createdAt: LocalDateTime? = null,
)
