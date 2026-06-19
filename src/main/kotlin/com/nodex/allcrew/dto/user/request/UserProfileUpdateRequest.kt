package com.nodex.allcrew.dto.user.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 일반 사용자 프로필 수정 요청 DTO.
 *
 * `PUT /api/user/members/{id}`에서 사용한다.
 * 이름·이메일만 수정 가능하며, 역할(role) 변경은 허용하지 않는다.
 */
data class UserProfileUpdateRequest(
    @field:NotBlank(message = "이름은 필수입니다.")
    @field:Size(max = 100, message = "이름은 100자 이하여야 합니다.")
    val name: String,

    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
    val email: String,
)
