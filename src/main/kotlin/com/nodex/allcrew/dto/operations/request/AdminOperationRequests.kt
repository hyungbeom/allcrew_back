package com.nodex.allcrew.dto.operations.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class UpdateAgencySettingsRequest(
    @field:NotBlank val companyName: String,
    val address: String? = null,
    val addressDetail: String? = null,
)

data class UpdateEducationSettingsRequest(
    val projectCode: String? = null,
    val ktlRequired: Boolean = false,
    val siteRequired: Boolean = false,
)

data class CreateCrewRequest(
    @field:NotBlank(message = "이름을 입력해 주세요.")
    val name: String,

    @field:NotBlank(message = "연락처를 입력해 주세요.")
    val phone: String,

    @field:NotBlank(message = "주 직무를 선택해 주세요.")
    val role: String,

    val projectIds: List<String> = emptyList(),
)

data class CreateDirectChatRequest(
    @field:NotBlank(message = "크루를 선택해 주세요.")
    val crewId: String,

    val projectCode: String? = null,
)

data class CreateGroupChatRequest(
    @field:NotBlank(message = "프로젝트를 선택해 주세요.")
    val projectCode: String,

    val title: String? = null,

    @field:NotEmpty(message = "최소 1명의 크루를 선택해 주세요.")
    val crewIds: List<String>,
)

data class SendChatMessageRequest(
    @field:NotBlank(message = "메시지를 입력해 주세요.")
    val content: String,
)
