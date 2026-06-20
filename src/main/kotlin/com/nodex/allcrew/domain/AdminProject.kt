package com.nodex.allcrew.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class AdminProject(
    val id: Long? = null,
    val agencyId: Long,
    val projectCode: String,
    val name: String,
    val eventType: String,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val workStartTime: LocalTime,
    val workEndTime: LocalTime,
    val address: String,
    val addressDetail: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val gpsRadius: Int = 100,
    val breakMinutes: Int = 60,
    val welfare: String? = null,
    val recruitmentDeadline: LocalDate,
    val preferredQualifications: String? = null,
    val startRecruitmentImmediately: Boolean = true,
    val status: String = "RECRUITING",
    val crewCurrent: Int = 0,
    val crewTotal: Int = 0,
    val budget: Long = 0,
    val progress: Int = 0,
    val createdByMemberId: Long,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
