package com.nodex.allcrew.dto.user.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 일반 사용자 회원가입 요청 DTO.
 *
 * `POST /api/user/members`에서 사용한다.
 * 역할은 서버에서 항상 [com.nodex.allcrew.domain.Role.USER]로 고정한다.
 */
data class UserSignupRequest(
    @field:NotBlank(message = "이름은 필수입니다.")
    @field:Size(max = 100, message = "이름은 100자 이하여야 합니다.")
    val name: String,

    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    @field:Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
    val email: String,
)
