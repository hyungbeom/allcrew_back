package com.nodex.allcrew.domain

import java.time.LocalDateTime

data class AdminAgency(
    val id: Long? = null,
    val companyName: String,
    val companySlug: String,
    val businessNumber: String,
    val address: String? = null,
    val addressDetail: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
