package com.nodex.allcrew.dto.response

import com.nodex.allcrew.domain.Member
import com.nodex.allcrew.domain.Role
import java.time.LocalDateTime

/**
 * 회원 API 응답 DTO.
 *
 * Admin/User API 모두에서 공통으로 사용한다.
 * Admin 응답에는 [role]이 포함되어 권한 확인이 가능하다.
 */
data class MemberResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: Role,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
) {
    companion object {
        /**
         * 도메인 엔티티를 API 응답 객체로 변환한다.
         *
         * @param member DB에서 조회한 회원 엔티티
         * @return 클라이언트에 반환할 응답 DTO
         */
        fun from(member: Member): MemberResponse =
            MemberResponse(
                id = member.id,
                name = member.name,
                email = member.email,
                role = member.role,
                createdAt = member.createdAt,
                updatedAt = member.updatedAt,
            )
    }
}
