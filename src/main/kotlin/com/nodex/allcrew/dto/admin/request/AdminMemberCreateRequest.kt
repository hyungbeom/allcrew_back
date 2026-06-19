package com.nodex.allcrew.dto.admin.request

import com.nodex.allcrew.domain.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 관리자용 회원 생성 요청 DTO.
 *
 * `POST /api/admin/members`에서 사용한다.
 * 관리자는 [role]을 지정해 ADMIN/USER 계정을 모두 생성할 수 있다.
 */
data class AdminMemberCreateRequest(
    @field:NotBlank(message = "이름은 필수입니다.")
    @field:Size(max = 100, message = "이름은 100자 이하여야 합니다.")
    val name: String,

    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
    val email: String,

    @field:NotNull(message = "역할은 필수입니다.")
    val role: Role,
)
