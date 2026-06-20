package com.nodex.allcrew.domain

import java.time.LocalDateTime

data class AdminInviteCode(
    val id: Long? = null,
    val agencyId: Long,
    val code: String,
    val createdByMemberId: Long? = null,
    val expiresAt: LocalDateTime? = null,
    val usedAt: LocalDateTime? = null,
    val usedByMemberId: Long? = null,
    val createdAt: LocalDateTime? = null,
)
