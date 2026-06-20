package com.nodex.allcrew.service.operations

import com.nodex.allcrew.domain.AdminActivity
import com.nodex.allcrew.domain.AdminChatMessage
import com.nodex.allcrew.domain.AdminChatRoom
import com.nodex.allcrew.domain.AdminContract
import com.nodex.allcrew.domain.AdminCrewMember
import com.nodex.allcrew.domain.AdminSafenetIncident
import com.nodex.allcrew.domain.AdminSettlement
import com.nodex.allcrew.dto.operations.response.ActivityResponse
import com.nodex.allcrew.dto.operations.response.ChatMessageResponse
import com.nodex.allcrew.dto.operations.response.ChatRoomResponse
import com.nodex.allcrew.dto.operations.response.ContractResponse
import com.nodex.allcrew.dto.operations.response.CrewMemberResponse
import com.nodex.allcrew.dto.operations.response.IncidentResponse
import com.nodex.allcrew.dto.operations.response.SettlementResponse
import java.time.format.DateTimeFormatter

object OperationResponseMapper {
    private val dotDateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")
    private val isoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val chatTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val chatDateFormatter = DateTimeFormatter.ofPattern("MM.dd")
    private val chatIsoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun formatCrewPhone(phone: String): String {
        val digits = phone.replace(Regex("\\D"), "")
        if (digits.length != 11) {
            return phone
        }

        return "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7)}"
    }

    fun toCrewResponse(member: AdminCrewMember, projectIds: List<String>): CrewMemberResponse =
        CrewMemberResponse(
            id = member.crewCode,
            name = member.name,
            phone = formatCrewPhone(member.phone),
            role = member.role,
            projectCount = member.projectCount,
            workDays = member.workDays,
            recentWorkDate = member.recentWorkDate?.format(isoDateFormatter),
            safetyTraining = member.safetyTraining,
            rating = member.rating.toDouble(),
            projectIds = projectIds,
        )

    fun toContractResponse(contract: AdminContract): ContractResponse =
        ContractResponse(
            id = contract.contractCode,
            crewName = contract.crewName,
            crewRole = contract.crewRole,
            projectName = contract.projectName,
            projectId = contract.projectCode,
            contractType = contract.contractType,
            sentDate = contract.sentDate?.format(dotDateFormatter),
            signedDate = contract.signedDate?.format(dotDateFormatter),
            status = mapContractStatus(contract.status),
        )

    fun toSettlementResponse(settlement: AdminSettlement): SettlementResponse =
        SettlementResponse(
            id = settlement.settlementCode,
            crewName = settlement.crewName,
            crewRole = settlement.crewRole,
            workHours = settlement.workHours,
            hourlyRate = settlement.hourlyRate,
            preTax = settlement.preTax,
            deduction = settlement.deduction,
            netPay = settlement.netPay,
            status = mapSettlementStatus(settlement.status),
            projectId = settlement.projectCode,
        )

    fun toChatRoomResponse(room: AdminChatRoom): ChatRoomResponse =
        ChatRoomResponse(
            id = room.roomCode,
            title = room.title,
            preview = room.preview,
            time = room.roomTime,
            type = mapChatType(room.roomType),
            projectId = room.projectCode,
            avatarText = room.avatarText,
            avatarColor = room.avatarColor,
        )

    fun toChatMessageResponse(message: AdminChatMessage, currentAdminName: String): ChatMessageResponse {
        val createdAt = message.createdAt ?: java.time.LocalDateTime.now()
        val sentAt = if (createdAt.toLocalDate() == java.time.LocalDate.now()) {
            createdAt.format(chatTimeFormatter)
        } else {
            createdAt.format(chatDateFormatter)
        }

        return ChatMessageResponse(
            id = message.id!!.toString(),
            senderType = mapMessageSenderType(message.senderType),
            senderName = message.senderName,
            content = message.content,
            sentAt = sentAt,
            sentAtIso = createdAt.format(chatIsoFormatter),
            isMine = message.senderType == "ADMIN" && message.senderName == currentAdminName,
        )
    }

    private fun mapMessageSenderType(senderType: String): String =
        when (senderType) {
            "ADMIN" -> "admin"
            else -> "crew"
        }

    fun toIncidentResponse(incident: AdminSafenetIncident): IncidentResponse =
        IncidentResponse(
            id = incident.incidentCode,
            title = incident.title,
            projectName = incident.projectName,
            location = incident.location,
            reporter = incident.reporter,
            time = incident.incidentTime,
            status = mapIncidentStatus(incident.status),
            projectId = incident.projectCode,
        )

    fun toActivityResponse(activity: AdminActivity): ActivityResponse =
        ActivityResponse(
            text = activity.content,
            date = activity.activityDate.format(isoDateFormatter),
        )

    fun mapContractStatus(status: String): String = when (status) {
        "SIGNED" -> "signed"
        "PENDING" -> "pending"
        "UNSIGNED" -> "unsigned"
        else -> "pending"
    }

    fun mapSettlementStatus(status: String): String = when (status) {
        "PENDING" -> "pending"
        "APPROVED" -> "approved"
        "PAID" -> "paid"
        else -> "pending"
    }

    fun mapChatType(type: String): String = when (type) {
        "PROJECT" -> "project"
        "DIRECT" -> "direct"
        else -> "project"
    }

    fun mapIncidentStatus(status: String): String = when (status) {
        "RESPONDING" -> "responding"
        "CLOSED" -> "closed"
        else -> "responding"
    }
}
