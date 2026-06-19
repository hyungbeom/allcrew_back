package com.nodex.allcrew.service.user

import com.nodex.allcrew.domain.Role
import com.nodex.allcrew.dto.response.MemberResponse
import com.nodex.allcrew.dto.user.request.UserProfileUpdateRequest
import com.nodex.allcrew.dto.user.request.UserSignupRequest
import com.nodex.allcrew.service.MemberService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 일반 사용자(User) 전용 회원 서비스.
 *
 * 회원가입·본인 프로필 조회/수정만 허용한다.
 * 목록 조회·삭제·역할 변경은 관리자 API에서만 가능하다.
 *
 * TODO: Spring Security 연동 후 요청자 본인(id) 검증 로직 추가
 */
@Service
class UserMemberService(
    private val memberService: MemberService,
) {

    /**
     * 일반 사용자 회원가입.
     *
     * 역할은 항상 [Role.USER]로 고정된다.
     *
     * @param request 가입 요청 (이름, 이메일)
     */
    @Transactional
    fun signup(request: UserSignupRequest): MemberResponse =
        memberService.createMember(
            name = request.name,
            email = request.email,
            role = Role.USER,
        )

    /**
     * 본인 프로필 조회.
     *
     * @param id 조회할 회원 PK (추후 인증 정보에서 추출)
     */
    @Transactional(readOnly = true)
    fun getProfile(id: Long): MemberResponse = memberService.getMember(id)

    /**
     * 본인 프로필 수정.
     *
     * 이름·이메일만 변경하며, 역할은 기존 값을 유지한다.
     *
     * @param id 수정 대상 회원 PK (추후 인증 정보에서 추출)
     * @param request 수정 요청
     */
    @Transactional
    fun updateProfile(id: Long, request: UserProfileUpdateRequest): MemberResponse {
        val existing = memberService.getMember(id)
        return memberService.updateMember(
            id = id,
            name = request.name,
            email = request.email,
            role = existing.role,
        )
    }
}
