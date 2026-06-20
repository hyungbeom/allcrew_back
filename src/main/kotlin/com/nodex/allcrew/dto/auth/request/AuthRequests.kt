package com.nodex.allcrew.dto.auth.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    @field:Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @field:NotBlank(message = "비밀번호를 입력해 주세요.")
    val password: String,
)

data class OAuthLoginRequest(
    @field:NotBlank(message = "인증 코드가 필요합니다.")
    val code: String,

    @field:NotBlank(message = "redirectUri가 필요합니다.")
    val redirectUri: String,
)

data class SignupRepresentativeRequest(
    @field:NotBlank val name: String,
    @field:NotBlank @field:Pattern(regexp = "^\\d{10,11}$") val phone: String,
    @field:NotBlank @field:Email val email: String,
    @field:NotBlank @field:Size(min = 8) val password: String,
    val agreeTerms: Boolean,
    val agreePrivacy: Boolean,
    val agreeLocation: Boolean,
    val agreeMarketing: Boolean = false,
    @field:NotBlank val companyName: String,
    @field:NotBlank
    @field:Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "영문 소문자, 숫자, 하이픈(-)만 사용할 수 있습니다.")
    @field:Size(min = 2, max = 50)
    val companySlug: String,
    @field:NotBlank @field:Pattern(regexp = "^\\d{3}-\\d{2}-\\d{5}$") val businessNumber: String,
    val address: String? = null,
    val addressDetail: String? = null,
)

data class SignupEmployeeRequest(
    @field:NotBlank val name: String,
    @field:NotBlank @field:Pattern(regexp = "^\\d{10,11}$") val phone: String,
    @field:NotBlank @field:Email val email: String,
    @field:NotBlank @field:Size(min = 8) val password: String,
    val agreeTerms: Boolean,
    val agreePrivacy: Boolean,
    val agreeLocation: Boolean,
    val agreeMarketing: Boolean = false,
    @field:NotBlank val inviteCode: String,
)
