package com.nodex.allcrew.dto.auth.response

data class AuthMemberResponse(
    val id: Long,
    val name: String,
    val email: String,
    val phone: String,
    val memberRole: String,
    val signupType: String,
    val agencyId: Long?,
    val companyName: String?,
    val companySlug: String?,
)

data class AuthResponse(
    val accessToken: String,
    val member: AuthMemberResponse,
)

data class EmailCheckResponse(
    val available: Boolean,
)

data class BusinessNumberCheckResponse(
    val available: Boolean,
)

data class CompanySlugCheckResponse(
    val available: Boolean,
)

data class SignupResponse(
    val message: String,
    val memberId: Long,
)
