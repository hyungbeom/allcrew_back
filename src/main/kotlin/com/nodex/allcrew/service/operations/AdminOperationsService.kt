package com.nodex.allcrew.service.operations

import com.nodex.allcrew.domain.AdminChatMessage
import com.nodex.allcrew.domain.AdminChatRoom
import com.nodex.allcrew.domain.AdminEducationSettings
import com.nodex.allcrew.domain.AdminCrewMember
import com.nodex.allcrew.dto.operations.request.CreateCrewRequest
import com.nodex.allcrew.dto.operations.request.CreateDirectChatRequest
import com.nodex.allcrew.dto.operations.request.CreateGroupChatRequest
import com.nodex.allcrew.dto.operations.request.UpdateAgencySettingsRequest
import com.nodex.allcrew.dto.operations.request.SendChatMessageRequest
import com.nodex.allcrew.dto.operations.request.UpdateEducationSettingsRequest
import com.nodex.allcrew.dto.operations.response.ActivityResponse
import com.nodex.allcrew.dto.operations.response.AgencySettingsResponse
import com.nodex.allcrew.dto.operations.response.CategoryChartResponse
import com.nodex.allcrew.dto.operations.response.ChartPointResponse
import com.nodex.allcrew.dto.operations.response.ChatListResponse
import com.nodex.allcrew.dto.operations.response.ChatMessageListResponse
import com.nodex.allcrew.dto.operations.response.ChatMessageResponse
import com.nodex.allcrew.dto.operations.response.ContractListResponse
import com.nodex.allcrew.dto.operations.response.ContractStatsResponse
import com.nodex.allcrew.dto.operations.response.CreateGroupChatResponse
import com.nodex.allcrew.dto.operations.response.CreateDirectChatResponse
import com.nodex.allcrew.dto.operations.response.CreateCrewResponse
import com.nodex.allcrew.dto.operations.response.CrewListResponse
import com.nodex.allcrew.dto.operations.response.DashboardAttendanceResponse
import com.nodex.allcrew.dto.operations.response.DashboardProjectSummaryResponse
import com.nodex.allcrew.dto.operations.response.DashboardResponse
import com.nodex.allcrew.dto.operations.response.DashboardSiteStatsResponse
import com.nodex.allcrew.dto.operations.response.DashboardTaskResponse
import com.nodex.allcrew.dto.operations.response.EducationOverviewResponse
import com.nodex.allcrew.dto.operations.response.ProjectFilterOption
import com.nodex.allcrew.dto.operations.response.ProjectFilterResponse
import com.nodex.allcrew.dto.operations.response.PttChannelResponse
import com.nodex.allcrew.dto.operations.response.PttOverviewResponse
import com.nodex.allcrew.dto.operations.response.SafenetListResponse
import com.nodex.allcrew.dto.operations.response.SafenetWorkflowResponse
import com.nodex.allcrew.dto.operations.response.SendChatMessageResponse
import com.nodex.allcrew.dto.operations.response.SettlementListResponse
import com.nodex.allcrew.dto.operations.response.SettlementSummaryResponse
import com.nodex.allcrew.dto.operations.response.StatisticsResponse
import com.nodex.allcrew.dto.operations.response.StatisticsSummaryResponse
import com.nodex.allcrew.dto.operations.response.TopCrewResponse
import com.nodex.allcrew.exception.BusinessException
import com.nodex.allcrew.mapper.AdminAgencyMapper
import com.nodex.allcrew.mapper.AdminOperationsMapper
import com.nodex.allcrew.mapper.AdminProjectMapper
import com.nodex.allcrew.service.auth.AuthenticatedAdmin
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Service
class AdminOperationsService(
    private val adminOperationsMapper: AdminOperationsMapper,
    private val adminProjectMapper: AdminProjectMapper,
    private val adminAgencyMapper: AdminAgencyMapper,
) {
    fun listCrew(auth: AuthenticatedAdmin): CrewListResponse {
        val members = adminOperationsMapper.findCrewByAgencyId(auth.agencyId)
        val items = members.map { member ->
            val projectIds = adminOperationsMapper.findCrewProjectCodes(member.id!!)
            OperationResponseMapper.toCrewResponse(member, projectIds)
        }
        return CrewListResponse(items = items, total = items.size)
    }

    @Transactional
    fun createCrew(auth: AuthenticatedAdmin, request: CreateCrewRequest): CreateCrewResponse {
        val name = request.name.trim()
        val phone = normalizePhone(request.phone.trim())
        val role = request.role.trim()

        val projectCodes = request.projectIds
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()

        projectCodes.forEach { projectCode ->
            val project = adminProjectMapper.findByProjectCode(projectCode)
                ?: throw BusinessException(HttpStatus.BAD_REQUEST, "존재하지 않는 프로젝트입니다.")
            if (project.agencyId != auth.agencyId) {
                throw BusinessException(HttpStatus.FORBIDDEN, "해당 프로젝트에 접근할 수 없습니다.")
            }
        }

        val member = AdminCrewMember(
            agencyId = auth.agencyId,
            crewCode = generateCrewCode(),
            name = name,
            phone = phone,
            role = role,
            projectCount = projectCodes.size,
            workDays = 0,
            recentWorkDate = null,
            safetyTraining = null,
            rating = BigDecimal("5.0"),
        )

        adminOperationsMapper.insertCrewMember(member)

        projectCodes.forEach { projectCode ->
            adminOperationsMapper.insertCrewProjectLink(member.id!!, projectCode)
        }

        val crew = OperationResponseMapper.toCrewResponse(member, projectCodes)
        return CreateCrewResponse(message = "크루가 추가되었습니다.", crew = crew)
    }

    fun listContracts(auth: AuthenticatedAdmin): ContractListResponse {
        val contracts = adminOperationsMapper.findContractsByAgencyId(auth.agencyId)
        val items = contracts.map { OperationResponseMapper.toContractResponse(it) }
        val stats = ContractStatsResponse(
            total = items.size,
            signed = items.count { it.status == "signed" },
            pending = items.count { it.status == "pending" },
            unsigned = items.count { it.status == "unsigned" },
        )
        return ContractListResponse(items = items, total = items.size, stats = stats)
    }

    fun listSettlements(auth: AuthenticatedAdmin): SettlementListResponse {
        val settlements = adminOperationsMapper.findSettlementsByAgencyId(auth.agencyId)
        val items = settlements.map { OperationResponseMapper.toSettlementResponse(it) }
        val summary = SettlementSummaryResponse(
            count = items.size,
            preTaxTotal = items.sumOf { it.preTax },
            deductionTotal = items.sumOf { it.deduction },
            netPayTotal = items.sumOf { it.netPay },
            pendingCount = items.count { it.status == "pending" },
        )
        return SettlementListResponse(items = items, total = items.size, summary = summary)
    }

    fun listChats(auth: AuthenticatedAdmin): ChatListResponse {
        val rooms = adminOperationsMapper.findChatRoomsByAgencyId(auth.agencyId)
        val items = rooms.map { OperationResponseMapper.toChatRoomResponse(it) }
        return ChatListResponse(items = items, total = items.size)
    }

    @Transactional
    fun createDirectChat(auth: AuthenticatedAdmin, request: CreateDirectChatRequest): CreateDirectChatResponse {
        val crewCode = request.crewId.trim()
        val crew = adminOperationsMapper.findCrewByCode(auth.agencyId, crewCode)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "크루를 찾을 수 없습니다.")

        val crewProjects = adminOperationsMapper.findCrewProjectCodes(crew.id!!)
        val preferredProject = request.projectCode?.trim()?.ifBlank { null }

        val projectCode = when {
            preferredProject != null -> {
                if (preferredProject !in crewProjects) {
                    throw BusinessException(HttpStatus.BAD_REQUEST, "선택한 프로젝트에 참여하지 않은 크루입니다.")
                }
                preferredProject
            }
            crewProjects.isNotEmpty() -> crewProjects.first()
            else -> throw BusinessException(HttpStatus.BAD_REQUEST, "참여 프로젝트가 없는 크루입니다.")
        }

        val existing = adminOperationsMapper.findDirectChatRoom(auth.agencyId, crewCode, projectCode)
        if (existing != null) {
            return CreateDirectChatResponse(
                message = "기존 1:1 채팅방으로 이동합니다.",
                room = OperationResponseMapper.toChatRoomResponse(existing),
            )
        }

        val project = adminProjectMapper.findByProjectCode(projectCode)
            ?: throw BusinessException(HttpStatus.BAD_REQUEST, "존재하지 않는 프로젝트입니다.")

        val room = AdminChatRoom(
            agencyId = auth.agencyId,
            roomCode = generateChatRoomCode(),
            title = "${crew.name} · ${project.name}",
            preview = "1:1 · 메시지가 없어요",
            roomTime = LocalDateTime.now().format(chatTimeFormatter),
            roomType = "DIRECT",
            projectCode = projectCode,
            avatarText = crew.name.first().toString(),
            avatarColor = pickAvatarColor(crew.name),
            crewCode = crewCode,
        )

        adminOperationsMapper.insertChatRoom(room)

        return CreateDirectChatResponse(
            message = "1:1 채팅방이 생성되었습니다.",
            room = OperationResponseMapper.toChatRoomResponse(room),
        )
    }

    @Transactional
    fun createGroupChat(auth: AuthenticatedAdmin, request: CreateGroupChatRequest): CreateGroupChatResponse {
        val projectCode = request.projectCode.trim()
        val project = adminProjectMapper.findByProjectCode(projectCode)
            ?: throw BusinessException(HttpStatus.BAD_REQUEST, "존재하지 않는 프로젝트입니다.")

        if (project.agencyId != auth.agencyId) {
            throw BusinessException(HttpStatus.FORBIDDEN, "해당 프로젝트에 접근할 수 없습니다.")
        }

        val crewIds = request.crewIds.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        if (crewIds.isEmpty()) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "최소 1명의 크루를 선택해 주세요.")
        }

        val crews = crewIds.map { crewCode ->
            adminOperationsMapper.findCrewByCode(auth.agencyId, crewCode)
                ?: throw BusinessException(HttpStatus.BAD_REQUEST, "크루를 찾을 수 없습니다.")
        }

        crews.forEach { crew ->
            val crewProjects = adminOperationsMapper.findCrewProjectCodes(crew.id!!)
            if (projectCode !in crewProjects) {
                throw BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "${crew.name} 크루는 해당 프로젝트에 참여하지 않습니다.",
                )
            }
        }

        val title = request.title?.trim()?.ifBlank { null } ?: "${project.name} 전체"

        val existing = adminOperationsMapper.findGroupChatRoom(auth.agencyId, projectCode, title)
        if (existing != null) {
            return CreateGroupChatResponse(
                message = "기존 그룹 채팅방으로 이동합니다.",
                room = OperationResponseMapper.toChatRoomResponse(existing),
            )
        }

        val room = AdminChatRoom(
            agencyId = auth.agencyId,
            roomCode = generateChatRoomCode(),
            title = title,
            preview = buildGroupChatPreview(crews),
            roomTime = LocalDateTime.now().format(chatTimeFormatter),
            roomType = "PROJECT",
            projectCode = projectCode,
            avatarText = title.first().toString(),
            avatarColor = pickAvatarColor(title),
            crewCode = null,
        )

        adminOperationsMapper.insertChatRoom(room)

        return CreateGroupChatResponse(
            message = "그룹 채팅방이 생성되었습니다.",
            room = OperationResponseMapper.toChatRoomResponse(room),
        )
    }

    fun listChatMessages(auth: AuthenticatedAdmin, roomCode: String): ChatMessageListResponse {
        val room = findChatRoomForAgency(auth, roomCode)
        val messages = adminOperationsMapper.findChatMessagesByRoomId(room.id!!)
        val items = messages.map {
            OperationResponseMapper.toChatMessageResponse(it, auth.memberName)
        }
        return ChatMessageListResponse(items = items, total = items.size)
    }

    @Transactional
    fun sendChatMessage(
        auth: AuthenticatedAdmin,
        roomCode: String,
        request: SendChatMessageRequest,
    ): SendChatMessageResponse {
        val room = findChatRoomForAgency(auth, roomCode)
        val content = request.content.trim()
        if (content.isEmpty()) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "메시지를 입력해 주세요.")
        }

        val message = AdminChatMessage(
            roomId = room.id!!,
            senderType = "ADMIN",
            senderName = auth.memberName,
            content = content,
        )
        adminOperationsMapper.insertChatMessage(message)

        val preview = buildChatPreview(room.roomType, auth.memberName, content)
        val roomTime = LocalDateTime.now().format(chatTimeFormatter)
        adminOperationsMapper.updateChatRoomPreview(room.id!!, preview, roomTime)

        val updatedRoom = room.copy(preview = preview, roomTime = roomTime)
        val messageResponse = OperationResponseMapper.toChatMessageResponse(message, auth.memberName)

        return SendChatMessageResponse(
            message = messageResponse,
            room = OperationResponseMapper.toChatRoomResponse(updatedRoom),
        )
    }

    private fun findChatRoomForAgency(auth: AuthenticatedAdmin, roomCode: String): AdminChatRoom {
        val normalizedCode = roomCode.trim()
        if (normalizedCode.isEmpty()) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "채팅방 코드가 필요합니다.")
        }

        return adminOperationsMapper.findChatRoomByCode(auth.agencyId, normalizedCode)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다.")
    }

    private fun buildChatPreview(roomType: String, senderName: String, content: String): String {
        val truncated = content.take(120)
        return if (roomType == "DIRECT") {
            truncated
        } else {
            "$senderName: $truncated"
        }
    }

    fun listSafenet(auth: AuthenticatedAdmin): SafenetListResponse {
        val incidents = adminOperationsMapper.findIncidentsByAgencyId(auth.agencyId)
        val items = incidents.map { OperationResponseMapper.toIncidentResponse(it) }
        val closed = adminOperationsMapper.countIncidentsByStatus(auth.agencyId, "CLOSED")
        val responding = adminOperationsMapper.countIncidentsByStatus(auth.agencyId, "RESPONDING")
        val workflowStats = SafenetWorkflowResponse(
            received = items.size,
            redAlert = responding,
            pttResponse = 0,
            closed = closed,
        )
        return SafenetListResponse(items = items, total = items.size, workflowStats = workflowStats)
    }

    fun getDashboard(auth: AuthenticatedAdmin): DashboardResponse {
        val crewMembers = adminOperationsMapper.findCrewByAgencyId(auth.agencyId)
        val totalCrew = crewMembers.size.coerceAtLeast(1)
        val checkedIn = (totalCrew * 0.875).roundToInt().coerceAtMost(totalCrew)
        val normal = (checkedIn * 0.857).roundToInt()
        val late = checkedIn - normal
        val absent = totalCrew - checkedIn

        val projects = adminProjectMapper.findByAgencyId(auth.agencyId)
        val today = LocalDate.now()
        val todayProjects = projects
            .filter { !today.isBefore(it.startDate) && !today.isAfter(it.endDate) && it.status == "IN_PROGRESS" }
            .map {
                DashboardProjectSummaryResponse(
                    id = it.projectCode,
                    name = it.name,
                    location = listOfNotNull(it.address, it.addressDetail).joinToString(" ").ifBlank { it.address },
                    status = mapProjectStatus(it.status),
                    crewCurrent = it.crewCurrent,
                    crewTotal = it.crewTotal,
                )
            }

        val recruitingCount = adminProjectMapper.countByAgencyIdAndStatus(auth.agencyId, "RECRUITING")
        val pendingSettlements = adminOperationsMapper.countSettlementsByStatus(auth.agencyId, "PENDING")
        val educationExpiring = crewMembers.count { it.safetyTraining.isNullOrBlank() }

        val activeSites = adminProjectMapper.countByAgencyIdAndStatus(auth.agencyId, "IN_PROGRESS")
        val fieldCrew = projects.sumOf { it.crewCurrent }
        val zoneViolations = adminOperationsMapper.countIncidentsByStatus(auth.agencyId, "RESPONDING")

        val activities = adminOperationsMapper.findActivitiesByAgencyId(auth.agencyId)
            .map { OperationResponseMapper.toActivityResponse(it) }

        val settlements = adminOperationsMapper.findSettlementsByAgencyId(auth.agencyId)
        val monthlySales = buildMonthlySales(
            settlements.map { settlement ->
                (settlement.createdAt?.toLocalDate()?.monthValue ?: 6) to settlement.netPay
            },
        )
        val categoryDistribution = projects
            .groupBy { it.eventType }
            .map { (category, group) -> CategoryChartResponse(category = category, value = group.size) }

        return DashboardResponse(
            attendance = DashboardAttendanceResponse(
                total = totalCrew,
                checkedIn = checkedIn,
                normal = normal,
                late = late,
                absent = absent,
            ),
            siteStats = DashboardSiteStatsResponse(
                activeSites = activeSites,
                fieldCrew = fieldCrew,
                zoneViolations = zoneViolations,
            ),
            tasks = DashboardTaskResponse(
                unverifiedApplicants = 0,
                verifiedApplicants = 0,
                recruitingProjects = recruitingCount,
                pendingSettlements = pendingSettlements,
                educationExpiring = educationExpiring,
            ),
            todayProjects = todayProjects,
            activities = activities,
            monthlySales = monthlySales,
            categoryDistribution = categoryDistribution,
        )
    }

    fun getStatistics(auth: AuthenticatedAdmin, period: String): StatisticsResponse {
        val periodKey = period.ifBlank { "quarter" }
        val range = when (periodKey) {
            "month" -> "${LocalDate.now().withDayOfMonth(1).format(dotFormatter)} - ${LocalDate.now().format(dotFormatter)}"
            "year" -> "${LocalDate.now().withDayOfYear(1).format(dotFormatter)} - ${LocalDate.now().format(dotFormatter)}"
            else -> {
                val now = LocalDate.now()
                val quarterStartMonth = ((now.monthValue - 1) / 3) * 3 + 1
                val start = LocalDate.of(now.year, quarterStartMonth, 1)
                "${start.format(dotFormatter)} - ${now.format(dotFormatter)}"
            }
        }

        val crewMembers = adminOperationsMapper.findCrewByAgencyId(auth.agencyId)
        val projects = adminProjectMapper.findByAgencyId(auth.agencyId)
        val settlements = adminOperationsMapper.findSettlementsByAgencyId(auth.agencyId)
        val incidents = adminOperationsMapper.findIncidentsByAgencyId(auth.agencyId)

        val monthlyProjects = buildMonthlyCounts(projects.map { it.createdAt?.monthValue ?: LocalDate.now().monthValue })
        val monthlyCrew = buildMonthlyCounts(crewMembers.map { it.createdAt?.monthValue ?: LocalDate.now().monthValue })

        val topCrew = crewMembers
            .sortedByDescending { it.projectCount }
            .take(5)
            .map {
                TopCrewResponse(
                    id = it.crewCode,
                    name = it.name,
                    role = it.role,
                    projectCount = it.projectCount,
                    workHours = null,
                    rating = it.rating.toDouble(),
                )
            }

        return StatisticsResponse(
            period = periodKey,
            range = range,
            summary = StatisticsSummaryResponse(
                totalProjects = projects.size,
                totalCrew = crewMembers.size,
                totalWorkHours = settlements.sumOf { it.workHours },
                safetyIncidents = incidents.size,
            ),
            monthlyProjects = monthlyProjects,
            monthlyCrew = monthlyCrew,
            topCrew = topCrew,
        )
    }

    fun getEducationOverview(auth: AuthenticatedAdmin, projectCode: String?): EducationOverviewResponse {
        val crewMembers = adminOperationsMapper.findCrewByAgencyId(auth.agencyId)
        val filtered = if (projectCode.isNullOrBlank()) {
            crewMembers
        } else {
            crewMembers.filter { member ->
                adminOperationsMapper.findCrewProjectCodes(member.id!!).contains(projectCode)
            }
        }

        val completed = filtered.count { !it.safetyTraining.isNullOrBlank() }
        val total = filtered.size
        val incomplete = total - completed
        val settings = adminOperationsMapper.findEducationSettings(auth.agencyId, projectCode?.trim()?.ifBlank { null })

        return EducationOverviewResponse(
            completionRate = if (total == 0) 0 else (completed * 100 / total),
            completedCount = completed,
            totalCount = total,
            incompleteCount = incomplete,
            ktlRequired = settings?.ktlRequired ?: false,
            siteRequired = settings?.siteRequired ?: false,
            statusCounts = mapOf(
                "all" to total,
                "completed" to completed,
                "expiring" to 0,
                "incomplete" to incomplete,
            ),
        )
    }

    @Transactional
    fun updateEducationSettings(auth: AuthenticatedAdmin, request: UpdateEducationSettingsRequest) {
        val projectCode = request.projectCode?.trim()?.ifBlank { null }
        val existing = adminOperationsMapper.findEducationSettings(auth.agencyId, projectCode)
        val settings = AdminEducationSettings(
            id = existing?.id,
            agencyId = auth.agencyId,
            projectCode = projectCode,
            ktlRequired = request.ktlRequired,
            siteRequired = request.siteRequired,
        )

        if (existing == null) {
            adminOperationsMapper.insertEducationSettings(settings)
        } else {
            adminOperationsMapper.updateEducationSettings(settings.copy(id = existing.id))
        }
    }

    fun getPttOverview(auth: AuthenticatedAdmin, projectCode: String?): PttOverviewResponse {
        val rooms = adminOperationsMapper.findChatRoomsByAgencyId(auth.agencyId)
        val filtered = if (projectCode.isNullOrBlank()) rooms else rooms.filter { it.projectCode == projectCode }

        val channels = mutableListOf(
            PttChannelResponse(
                id = "all",
                name = "전체",
                description = "전체 크루",
                type = "ptt",
            ),
        )
        channels += filtered
            .filter { it.roomType == "PROJECT" }
            .map {
                PttChannelResponse(
                    id = it.roomCode,
                    name = it.title,
                    description = it.preview,
                    type = "ptt",
                )
            }

        return PttOverviewResponse(channels = channels, logs = emptyList())
    }

    fun getAgencySettings(auth: AuthenticatedAdmin): AgencySettingsResponse {
        val agency = adminAgencyMapper.findById(auth.agencyId)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "업체 정보를 찾을 수 없습니다.")

        return AgencySettingsResponse(
            companyName = agency.companyName,
            companySlug = agency.companySlug,
            businessNumber = agency.businessNumber,
            address = agency.address,
            addressDetail = agency.addressDetail,
        )
    }

    @Transactional
    fun updateAgencySettings(auth: AuthenticatedAdmin, request: UpdateAgencySettingsRequest) {
        val agency = adminAgencyMapper.findById(auth.agencyId)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "업체 정보를 찾을 수 없습니다.")

        adminAgencyMapper.updateAgency(
            agency.copy(
                companyName = request.companyName.trim(),
                address = request.address?.trim()?.ifBlank { null },
                addressDetail = request.addressDetail?.trim()?.ifBlank { null },
            ),
        )
    }

    fun getProjectFilterOptions(auth: AuthenticatedAdmin): ProjectFilterResponse {
        val projects = adminProjectMapper.findByAgencyId(auth.agencyId)
        val items = projects.map { ProjectFilterOption(value = it.projectCode, label = it.name) }
        return ProjectFilterResponse(items = items)
    }

    private fun mapProjectStatus(status: String): String = when (status) {
        "RECRUITING" -> "recruiting"
        "IN_PROGRESS" -> "in_progress"
        "COMPLETED" -> "completed"
        else -> "recruiting"
    }

    private fun buildMonthlySales(entries: List<Pair<Int, Long>>): List<ChartPointResponse> {
        val monthLabels = listOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
        val sums = IntArray(12)
        entries.forEach { (month, amount) ->
            if (month in 1..12) sums[month - 1] += amount.toInt()
        }
        return monthLabels.mapIndexed { index, label -> ChartPointResponse(month = label, value = sums[index]) }
    }

    private fun buildMonthlyCounts(monthValues: List<Int>): List<ChartPointResponse> {
        val monthLabels = listOf("1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월")
        val counts = IntArray(12)
        monthValues.forEach { month ->
            if (month in 1..12) counts[month - 1]++
        }
        return monthLabels.mapIndexed { index, label -> ChartPointResponse(month = label, value = counts[index]) }
    }

    private fun generateCrewCode(): String {
        val sequence = adminOperationsMapper.findNextCrewSequence()
        return "CRW-${sequence.toString().padStart(3, '0')}"
    }

    private fun generateChatRoomCode(): String {
        val sequence = adminOperationsMapper.findNextChatRoomSequence()
        return "CHT-${sequence.toString().padStart(4, '0')}"
    }

    private fun pickAvatarColor(name: String): String {
        val colors = listOf("#1677ff", "#722ed1", "#13c2c2", "#fa8c16", "#eb2f96", "#52c41a")
        val index = name.sumOf { it.code } % colors.size
        return colors[index]
    }

    private fun buildGroupChatPreview(crews: List<AdminCrewMember>): String {
        if (crews.size == 1) {
            return "${crews[0].name} · 메시지가 없어요"
        }

        return "${crews[0].name} 외 ${crews.size - 1}명 · 메시지가 없어요"
    }

    private fun normalizePhone(input: String): String {
        val digits = input.replace(Regex("\\D"), "")
        if (digits.length != 11 || !digits.startsWith("01")) {
            throw BusinessException(HttpStatus.BAD_REQUEST, "올바른 휴대폰 번호 형식이 아닙니다.")
        }

        return digits
    }

    companion object {
        private val dotFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
        private val chatTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
