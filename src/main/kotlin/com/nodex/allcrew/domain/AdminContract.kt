package com.nodex.allcrew.domain

import java.time.LocalDate
import java.time.LocalDateTime

data class AdminContract(
    val id: Long? = null,
    val agencyId: Long,
    val contractCode: String,
    val crewName: String,
    val crewRole: String,
    val projectName: String,
    val projectCode: String,
    val contractType: String,
    val sentDate: LocalDate? = null,
    val signedDate: LocalDate? = null,
    val status: String,
    val createdAt: LocalDateTime? = null,
)
