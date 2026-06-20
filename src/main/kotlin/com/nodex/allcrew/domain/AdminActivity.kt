package com.nodex.allcrew.domain

import java.time.LocalDate
import java.time.LocalDateTime

data class AdminActivity(
    val id: Long? = null,
    val agencyId: Long,
    val content: String,
    val activityDate: LocalDate,
    val createdAt: LocalDateTime? = null,
)
