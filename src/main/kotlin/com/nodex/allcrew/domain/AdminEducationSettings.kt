package com.nodex.allcrew.domain

import java.time.LocalDateTime

data class AdminEducationSettings(
    val id: Long? = null,
    val agencyId: Long,
    val projectCode: String? = null,
    val ktlRequired: Boolean = false,
    val siteRequired: Boolean = false,
    val updatedAt: LocalDateTime? = null,
)
