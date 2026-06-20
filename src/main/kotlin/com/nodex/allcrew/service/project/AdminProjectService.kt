package com.nodex.allcrew.service.project

import com.nodex.allcrew.domain.AdminProject
import com.nodex.allcrew.domain.AdminProjectPosition
import com.nodex.allcrew.dto.project.request.CreateProjectRequest
import com.nodex.allcrew.dto.project.request.UpdateProjectLocationRequest
import com.nodex.allcrew.dto.project.response.CreateProjectResponse
import com.nodex.allcrew.dto.project.response.ProjectDetailResponse
import com.nodex.allcrew.dto.project.response.ProjectListResponse
import com.nodex.allcrew.dto.project.response.ProjectPositionResponse
import com.nodex.allcrew.dto.project.response.ProjectResponse
import com.nodex.allcrew.exception.BusinessException
import com.nodex.allcrew.mapper.AdminMemberMapper
import com.nodex.allcrew.mapper.AdminProjectMapper
import com.nodex.allcrew.mapper.AdminProjectPositionMapper
import com.nodex.allcrew.service.auth.AuthenticatedAdmin
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class AdminProjectService(
    private val adminProjectMapper: AdminProjectMapper,
    private val adminProjectPositionMapper: AdminProjectPositionMapper,
    private val adminMemberMapper: AdminMemberMapper,
) {
    fun listProjects(auth: AuthenticatedAdmin): ProjectListResponse {
        val projects = adminProjectMapper.findByAgencyId(auth.agencyId)
        val items = projects.map { toProjectResponse(it) }
        return ProjectListResponse(items = items, total = items.size)
    }

    fun getProjectDetail(auth: AuthenticatedAdmin, projectCode: String): ProjectDetailResponse {
        val project = adminProjectMapper.findByProjectCode(projectCode.trim())
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다.")

        if (project.agencyId != auth.agencyId) {
            throw BusinessException(HttpStatus.FORBIDDEN, "해당 프로젝트에 접근할 수 없습니다.")
        }

        val positions = adminProjectPositionMapper.findByProjectId(project.id!!)
        val creator = adminMemberMapper.findById(project.createdByMemberId)

        return toProjectDetailResponse(project, positions, creator?.name)
    }

    @Transactional
    fun updateProjectLocation(
        auth: AuthenticatedAdmin,
        projectCode: String,
        request: UpdateProjectLocationRequest,
    ): ProjectDetailResponse {
        val project = adminProjectMapper.findByProjectCode(projectCode.trim())
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "프로젝트를 찾을 수 없습니다.")

        if (project.agencyId != auth.agencyId) {
            throw BusinessException(HttpStatus.FORBIDDEN, "해당 프로젝트에 접근할 수 없습니다.")
        }

        val normalizedAddress = request.address?.trim()?.takeIf { it.isNotEmpty() }
        adminProjectMapper.updateLocation(
            projectCode = project.projectCode,
            latitude = request.latitude,
            longitude = request.longitude,
            address = normalizedAddress,
        )

        val updatedProject = adminProjectMapper.findByProjectCode(project.projectCode)!!
        val positions = adminProjectPositionMapper.findByProjectId(updatedProject.id!!)
        val creator = adminMemberMapper.findById(updatedProject.createdByMemberId)

        return toProjectDetailResponse(updatedProject, positions, creator?.name)
    }

    @Transactional
    fun createProject(auth: AuthenticatedAdmin, request: CreateProjectRequest): CreateProjectResponse {
        validateDates(request)

        val crewTotal = request.positions.sumOf { it.count }
        val budget = calculateDailyLaborCost(request)
        val projectCode = generateProjectCode()
        val status = if (request.startRecruitmentImmediately) "RECRUITING" else "IN_PROGRESS"
        val welfareJson = encodeWelfare(request.welfare)

        val project = AdminProject(
            agencyId = auth.agencyId,
            projectCode = projectCode,
            name = request.name.trim(),
            eventType = request.eventType.trim(),
            description = request.description?.trim()?.takeIf { it.isNotEmpty() },
            coverImageUrl = request.coverImageUrl?.trim()?.takeIf { it.isNotEmpty() },
            startDate = request.startDate,
            endDate = request.endDate,
            workStartTime = request.workStartTime,
            workEndTime = request.workEndTime,
            address = request.address.trim(),
            addressDetail = request.addressDetail?.trim()?.takeIf { it.isNotEmpty() },
            gpsRadius = request.gpsRadius,
            breakMinutes = request.breakMinutes,
            welfare = welfareJson,
            recruitmentDeadline = request.recruitmentDeadline,
            preferredQualifications = request.preferredQualifications?.trim()?.takeIf { it.isNotEmpty() },
            startRecruitmentImmediately = request.startRecruitmentImmediately,
            status = status,
            crewCurrent = 0,
            crewTotal = crewTotal,
            budget = budget,
            progress = 0,
            createdByMemberId = auth.memberId,
        )

        adminProjectMapper.insert(project)

        val positions = request.positions.mapIndexed { index, item ->
            AdminProjectPosition(
                projectId = project.id!!,
                name = item.name.trim(),
                headcount = item.count,
                payType = item.payType.trim(),
                amount = item.amount,
                sortOrder = index,
            )
        }
        adminProjectPositionMapper.insertBatch(positions)

        val response = toProjectResponse(project)
        return CreateProjectResponse(message = "프로젝트가 생성되었습니다.", project = response)
    }

    private fun validateDates(request: CreateProjectRequest) {
        if (request.endDate.isBefore(request.startDate)) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "종료일은 시작일 이후여야 합니다.")
        }

        if (request.recruitmentDeadline.isAfter(request.endDate)) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "모집 마감일은 프로젝트 종료일 이전이어야 합니다.")
        }

        val workMinutes = Duration.between(request.workStartTime, request.workEndTime).toMinutes()
        if (workMinutes <= 0) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "근무 종료 시간은 시작 시간 이후여야 합니다.")
        }

        request.positions.forEach { position ->
            if (position.payType !in setOf("hourly", "daily")) {
                throw BusinessException(HttpStatus.BAD_REQUEST, "급여 유형이 올바르지 않습니다.")
            }
        }
    }

    private fun generateProjectCode(): String {
        val sequence = adminProjectMapper.findNextProjectSequence()
        return "PRJ-${sequence.toString().padStart(4, '0')}"
    }

    private fun calculateDailyLaborCost(request: CreateProjectRequest): Long {
        val workMinutes = Duration.between(request.workStartTime, request.workEndTime).toMinutes()
        val workHours = maxOf(0.0, (workMinutes - request.breakMinutes) / 60.0)

        return request.positions.sumOf { position ->
            val count = position.count.toLong()
            val amount = position.amount.toLong()
            if (position.payType == "daily") {
                count * amount
            } else {
                (count * amount * workHours).toLong()
            }
        }
    }

    private fun encodeWelfare(welfare: List<String>): String? {
        if (welfare.isEmpty()) return null
        return welfare.distinct().joinToString(",")
    }

    private fun decodeWelfare(welfare: String?): List<String> {
        if (welfare.isNullOrBlank()) return emptyList()
        return welfare.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun calculateAvgHourlyWage(positions: List<AdminProjectPosition>): Int? {
        val hourlyPositions = positions.filter { it.payType == "hourly" && it.headcount > 0 }
        if (hourlyPositions.isEmpty()) return null

        val totalHeadcount = hourlyPositions.sumOf { it.headcount }
        val weightedSum = hourlyPositions.sumOf { it.amount * it.headcount }
        return weightedSum / totalHeadcount
    }

    private fun toProjectResponse(project: AdminProject): ProjectResponse {
        val shortDateFormatter = DateTimeFormatter.ofPattern("MM.dd")
        val fullDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        return ProjectResponse(
            id = project.projectCode,
            name = project.name,
            category = project.eventType,
            location = buildLocation(project),
            startDate = project.startDate.format(shortDateFormatter),
            endDate = project.endDate.format(shortDateFormatter),
            crewCurrent = project.crewCurrent,
            crewTotal = project.crewTotal,
            manager = null,
            budget = project.budget,
            progress = project.progress,
            status = mapStatus(project.status),
            createdAt = project.createdAt?.toLocalDate() ?: LocalDate.now(),
            workHours = formatWorkHours(project, timeFormatter),
            fullStartDate = project.startDate.format(fullDateFormatter),
            fullEndDate = project.endDate.format(fullDateFormatter),
        )
    }

    private fun toProjectDetailResponse(
        project: AdminProject,
        positions: List<AdminProjectPosition>,
        managerName: String?,
    ): ProjectDetailResponse {
        val shortDateFormatter = DateTimeFormatter.ofPattern("MM.dd")
        val fullDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        return ProjectDetailResponse(
            id = project.projectCode,
            name = project.name,
            category = project.eventType,
            location = buildLocation(project),
            address = project.address,
            addressDetail = project.addressDetail,
            latitude = project.latitude,
            longitude = project.longitude,
            startDate = project.startDate.format(shortDateFormatter),
            endDate = project.endDate.format(shortDateFormatter),
            crewCurrent = project.crewCurrent,
            crewTotal = project.crewTotal,
            manager = managerName,
            budget = project.budget,
            progress = project.progress,
            status = mapStatus(project.status),
            createdAt = project.createdAt?.toLocalDate() ?: LocalDate.now(),
            workHours = formatWorkHours(project, timeFormatter),
            fullStartDate = project.startDate.format(fullDateFormatter),
            fullEndDate = project.endDate.format(fullDateFormatter),
            description = project.description,
            gpsRadius = project.gpsRadius,
            breakMinutes = project.breakMinutes,
            welfare = decodeWelfare(project.welfare),
            recruitmentDeadline = project.recruitmentDeadline,
            preferredQualifications = project.preferredQualifications,
            startRecruitmentImmediately = project.startRecruitmentImmediately,
            positions = positions.map {
                ProjectPositionResponse(
                    name = it.name,
                    count = it.headcount,
                    payType = it.payType,
                    amount = it.amount,
                )
            },
            avgHourlyWage = calculateAvgHourlyWage(positions),
            accumulatedCost = 0,
            applicantCount = 0,
        )
    }

    private fun buildLocation(project: AdminProject): String =
        listOfNotNull(project.address, project.addressDetail)
            .joinToString(" ")
            .ifBlank { project.address }

    private fun formatWorkHours(project: AdminProject, timeFormatter: DateTimeFormatter): String =
        "${project.workStartTime.format(timeFormatter)} ~ ${project.workEndTime.format(timeFormatter)}"

    private fun mapStatus(status: String): String = when (status) {
        "RECRUITING" -> "recruiting"
        "IN_PROGRESS" -> "in_progress"
        "COMPLETED" -> "completed"
        else -> "recruiting"
    }
}
