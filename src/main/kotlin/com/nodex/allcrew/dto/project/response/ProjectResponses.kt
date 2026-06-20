package com.nodex.allcrew.dto.project.response

import java.time.LocalDate

data class ProjectPositionResponse(
    val name: String,
    val count: Int,
    val payType: String,
    val amount: Int,
)

data class ProjectResponse(
    val id: String,
    val name: String,
    val category: String,
    val location: String,
    val startDate: String,
    val endDate: String,
    val crewCurrent: Int,
    val crewTotal: Int,
    val manager: String? = null,
    val budget: Long,
    val progress: Int,
    val status: String,
    val createdAt: LocalDate,
    val workHours: String? = null,
    val fullStartDate: String? = null,
    val fullEndDate: String? = null,
)

data class CreateProjectResponse(
    val message: String,
    val project: ProjectResponse,
)

data class ProjectListResponse(
    val items: List<ProjectResponse>,
    val total: Int,
)

data class ProjectDetailResponse(
    val id: String,
    val name: String,
    val category: String,
    val location: String,
    val address: String,
    val addressDetail: String?,
    val startDate: String,
    val endDate: String,
    val crewCurrent: Int,
    val crewTotal: Int,
    val manager: String? = null,
    val budget: Long,
    val progress: Int,
    val status: String,
    val createdAt: LocalDate,
    val workHours: String? = null,
    val fullStartDate: String? = null,
    val fullEndDate: String? = null,
    val description: String? = null,
    val gpsRadius: Int,
    val breakMinutes: Int,
    val welfare: List<String> = emptyList(),
    val recruitmentDeadline: LocalDate,
    val preferredQualifications: String? = null,
    val startRecruitmentImmediately: Boolean,
    val positions: List<ProjectPositionResponse> = emptyList(),
    val avgHourlyWage: Int? = null,
    val accumulatedCost: Long = 0,
    val applicantCount: Int = 0,
)
