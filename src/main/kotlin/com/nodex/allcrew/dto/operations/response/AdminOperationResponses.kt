package com.nodex.allcrew.dto.operations.response

import java.time.LocalDate

data class CrewMemberResponse(
    val id: String,
    val name: String,
    val phone: String,
    val role: String,
    val projectCount: Int,
    val workDays: Int,
    val recentWorkDate: String?,
    val safetyTraining: String?,
    val rating: Double,
    val projectIds: List<String>,
)

data class CrewListResponse(val items: List<CrewMemberResponse>, val total: Int)

data class CreateCrewResponse(
    val message: String,
    val crew: CrewMemberResponse,
)

data class ContractResponse(
    val id: String,
    val crewName: String,
    val crewRole: String,
    val projectName: String,
    val projectId: String,
    val contractType: String,
    val sentDate: String?,
    val signedDate: String?,
    val status: String,
)

data class ContractListResponse(
    val items: List<ContractResponse>,
    val total: Int,
    val stats: ContractStatsResponse,
)

data class ContractStatsResponse(
    val total: Int,
    val signed: Int,
    val pending: Int,
    val unsigned: Int,
)

data class SettlementResponse(
    val id: String,
    val crewName: String,
    val crewRole: String,
    val workHours: Int,
    val hourlyRate: Int,
    val preTax: Long,
    val deduction: Long,
    val netPay: Long,
    val status: String,
    val projectId: String,
)

data class SettlementListResponse(
    val items: List<SettlementResponse>,
    val total: Int,
    val summary: SettlementSummaryResponse,
)

data class SettlementSummaryResponse(
    val count: Int,
    val preTaxTotal: Long,
    val deductionTotal: Long,
    val netPayTotal: Long,
    val pendingCount: Int,
)

data class ChatRoomResponse(
    val id: String,
    val title: String,
    val preview: String,
    val time: String,
    val type: String,
    val projectId: String,
    val avatarText: String,
    val avatarColor: String,
)

data class ChatListResponse(val items: List<ChatRoomResponse>, val total: Int)

data class CreateDirectChatResponse(
    val message: String,
    val room: ChatRoomResponse,
)

data class CreateGroupChatResponse(
    val message: String,
    val room: ChatRoomResponse,
)

data class ChatMessageResponse(
    val id: String,
    val senderType: String,
    val senderName: String,
    val content: String,
    val sentAt: String,
    val sentAtIso: String,
    val isMine: Boolean,
)

data class ChatMessageListResponse(
    val items: List<ChatMessageResponse>,
    val total: Int,
)

data class SendChatMessageResponse(
    val message: ChatMessageResponse,
    val room: ChatRoomResponse,
)

data class IncidentResponse(
    val id: String,
    val title: String,
    val projectName: String,
    val location: String,
    val reporter: String,
    val time: String,
    val status: String,
    val projectId: String,
)

data class SafenetListResponse(
    val items: List<IncidentResponse>,
    val total: Int,
    val workflowStats: SafenetWorkflowResponse,
)

data class SafenetWorkflowResponse(
    val received: Int,
    val redAlert: Int,
    val pttResponse: Int,
    val closed: Int,
)

data class ActivityResponse(val text: String, val date: String)

data class DashboardTaskResponse(
    val unverifiedApplicants: Int,
    val verifiedApplicants: Int,
    val recruitingProjects: Int,
    val pendingSettlements: Int,
    val educationExpiring: Int,
)

data class DashboardAttendanceResponse(
    val total: Int,
    val checkedIn: Int,
    val normal: Int,
    val late: Int,
    val absent: Int,
)

data class DashboardSiteStatsResponse(
    val activeSites: Int,
    val fieldCrew: Int,
    val zoneViolations: Int,
)

data class DashboardProjectSummaryResponse(
    val id: String,
    val name: String,
    val location: String,
    val status: String,
    val crewCurrent: Int,
    val crewTotal: Int,
)

data class ChartPointResponse(val month: String, val value: Int)

data class CategoryChartResponse(val category: String, val value: Int)

data class DashboardResponse(
    val attendance: DashboardAttendanceResponse,
    val siteStats: DashboardSiteStatsResponse,
    val tasks: DashboardTaskResponse,
    val todayProjects: List<DashboardProjectSummaryResponse>,
    val activities: List<ActivityResponse>,
    val monthlySales: List<ChartPointResponse>,
    val categoryDistribution: List<CategoryChartResponse>,
)

data class StatisticsSummaryResponse(
    val totalProjects: Int,
    val totalCrew: Int,
    val totalWorkHours: Int,
    val safetyIncidents: Int,
)

data class TopCrewResponse(
    val id: String,
    val name: String,
    val role: String,
    val projectCount: Int,
    val workHours: Int?,
    val rating: Double,
)

data class StatisticsResponse(
    val period: String,
    val range: String,
    val summary: StatisticsSummaryResponse,
    val monthlyProjects: List<ChartPointResponse>,
    val monthlyCrew: List<ChartPointResponse>,
    val topCrew: List<TopCrewResponse>,
)

data class EducationOverviewResponse(
    val completionRate: Int,
    val completedCount: Int,
    val totalCount: Int,
    val incompleteCount: Int,
    val ktlRequired: Boolean,
    val siteRequired: Boolean,
    val statusCounts: Map<String, Int>,
)

data class PttChannelResponse(
    val id: String,
    val name: String,
    val description: String,
    val type: String,
)

data class PttOverviewResponse(
    val channels: List<PttChannelResponse>,
    val logs: List<PttLogResponse>,
)

data class PttLogResponse(
    val id: String,
    val speaker: String,
    val message: String,
    val time: String,
)

data class AgencySettingsResponse(
    val companyName: String,
    val companySlug: String,
    val businessNumber: String,
    val address: String?,
    val addressDetail: String?,
)

data class ProjectFilterOption(val value: String, val label: String)

data class ProjectFilterResponse(val items: List<ProjectFilterOption>)
