package com.nodex.allcrew.domain

import java.time.LocalDateTime

data class AdminSettlement(
    val id: Long? = null,
    val agencyId: Long,
    val settlementCode: String,
    val crewName: String,
    val crewRole: String,
    val workHours: Int,
    val hourlyRate: Int,
    val preTax: Long,
    val deduction: Long,
    val netPay: Long,
    val status: String,
    val projectCode: String,
    val createdAt: LocalDateTime? = null,
)
