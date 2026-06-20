package com.nodex.allcrew.service.auth

import com.nodex.allcrew.exception.BusinessException
import com.nodex.allcrew.mapper.AdminAgencyMapper
import com.nodex.allcrew.mapper.AdminMemberMapper
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

data class AuthenticatedAdmin(
    val memberId: Long,
    val memberName: String,
    val agencyId: Long,
    val companySlug: String,
)

@Component
class AdminAuthSupport(
    private val jwtTokenProvider: JwtTokenProvider,
    private val adminMemberMapper: AdminMemberMapper,
    private val adminAgencyMapper: AdminAgencyMapper,
) {
    fun authenticate(authorization: String?): AuthenticatedAdmin {
        if (authorization.isNullOrBlank() || !authorization.startsWith("Bearer ")) {
            throw BusinessException(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.")
        }

        val token = authorization.removePrefix("Bearer ").trim()
        val memberId = try {
            jwtTokenProvider.getMemberId(token)
        } catch (_: Exception) {
            throw BusinessException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.")
        }

        val member = adminMemberMapper.findById(memberId)
            ?: throw BusinessException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.")

        if (!member.isActive) {
            throw BusinessException(HttpStatus.UNAUTHORIZED, "비활성화된 계정입니다.")
        }

        val agencyId = member.agencyId
            ?: throw BusinessException(HttpStatus.FORBIDDEN, "업체 정보가 없는 계정입니다.")

        val agency = adminAgencyMapper.findById(agencyId)
            ?: throw BusinessException(HttpStatus.FORBIDDEN, "업체 정보를 찾을 수 없습니다.")

        return AuthenticatedAdmin(
            memberId = member.id!!,
            memberName = member.name,
            agencyId = agency.id!!,
            companySlug = agency.companySlug,
        )
    }
}
