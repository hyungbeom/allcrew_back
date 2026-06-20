package com.nodex.allcrew.dto.project.request

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalTime

data class CreateProjectPositionRequest(
    @field:NotBlank(message = "포지션명을 입력해 주세요.")
    val name: String,

    @field:Min(1, message = "인원은 1명 이상이어야 합니다.")
    val count: Int,

    @field:NotBlank(message = "급여 유형을 선택해 주세요.")
    val payType: String,

    @field:Min(0, message = "급여는 0 이상이어야 합니다.")
    val amount: Int,
)

data class CreateProjectRequest(
    @field:NotBlank(message = "프로젝트명을 입력해 주세요.")
    val name: String,

    @field:NotBlank(message = "이벤트 타입을 선택해 주세요.")
    val eventType: String,

    val description: String? = null,
    val coverImageUrl: String? = null,

    @field:NotNull(message = "시작일을 선택해 주세요.")
    val startDate: LocalDate,

    @field:NotNull(message = "종료일을 선택해 주세요.")
    val endDate: LocalDate,

    @field:NotNull(message = "근무 시작 시간을 선택해 주세요.")
    val workStartTime: LocalTime,

    @field:NotNull(message = "근무 종료 시간을 선택해 주세요.")
    val workEndTime: LocalTime,

    @field:NotBlank(message = "행사 장소를 선택해 주세요.")
    val address: String,

    val addressDetail: String? = null,

    @field:Min(10, message = "GPS 반경은 10m 이상이어야 합니다.")
    val gpsRadius: Int = 100,

    @field:Min(0, message = "휴게시간은 0분 이상이어야 합니다.")
    val breakMinutes: Int = 60,

    val welfare: List<String> = emptyList(),

    @field:NotNull(message = "모집 마감일을 선택해 주세요.")
    val recruitmentDeadline: LocalDate,

    val preferredQualifications: String? = null,
    val startRecruitmentImmediately: Boolean = true,

    @field:NotEmpty(message = "최소 1개의 포지션을 추가해 주세요.")
    @field:Valid
    val positions: List<CreateProjectPositionRequest>,
)
