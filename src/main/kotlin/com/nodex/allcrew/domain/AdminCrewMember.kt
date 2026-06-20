package com.nodex.allcrew.domain

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class AdminCrewMember(
    val id: Long? = null,
    val agencyId: Long,
    val crewCode: String,
    val name: String,
    val phone: String,
    val role: String = "크루",
    val projectCount: Int = 0,
    val workDays: Int = 0,
    val recentWorkDate: LocalDate? = null,
    val safetyTraining: String? = null,
    val rating: BigDecimal = BigDecimal("5.0"),
    val createdAt: LocalDateTime? = null,
)
