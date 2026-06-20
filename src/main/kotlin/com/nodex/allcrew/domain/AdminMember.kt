package com.nodex.allcrew.domain

import java.time.LocalDateTime

data class AdminMember(
    val id: Long? = null,
    val agencyId: Long? = null,
    val signupType: String,
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val agreeTerms: Boolean,
    val agreePrivacy: Boolean,
    val agreeLocation: Boolean,
    val agreeMarketing: Boolean = false,
    val inviteCodeUsed: String? = null,
    val memberRole: String,
    val isActive: Boolean = true,
    val lastLoginAt: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
