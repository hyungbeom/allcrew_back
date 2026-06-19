package com.nodex.allcrew.service.admin

import com.nodex.allcrew.dto.admin.request.AdminMemberCreateRequest
import com.nodex.allcrew.dto.admin.request.AdminMemberUpdateRequest
import com.nodex.allcrew.dto.response.MemberResponse
import com.nodex.allcrew.service.MemberService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 관리자(Admin) 전용 회원 서비스.
 *
 * 전체 회원 조회·생성·수정·삭제 및 역할(role) 변경을 담당한다.
 * 인증/인가(Spring Security) 연동 전까지는 API 경로(`/api/admin`)로만 구분한다.
 */
@Service
class AdminMemberService(
    private val memberService: MemberService,
) {

    /**
     * 전체 회원 목록 조회 (관리자 전용).
     */
    @Transactional(readOnly = true)
    fun getMembers(): List<MemberResponse> = memberService.getMembers()

    /**
     * 회원 단건 조회 (관리자 전용).
     *
     * @param id 조회할 회원 PK
     */
    @Transactional(readOnly = true)
    fun getMember(id: Long): MemberResponse = memberService.getMember(id)

    /**
     * 관리자가 회원을 생성한다.
     *
     * ADMIN/USER 역할을 지정해 계정을 만들 수 있다.
     *
     * @param request 생성 요청 (이름, 이메일, 역할)
     */
    @Transactional
    fun createMember(request: AdminMemberCreateRequest): MemberResponse =
        memberService.createMember(
            name = request.name,
            email = request.email,
            role = request.role,
        )

    /**
     * 관리자가 회원 정보를 수정한다.
     *
     * 이름·이메일·역할 모두 변경 가능하다.
     *
     * @param id 수정 대상 회원 PK
     * @param request 수정 요청
     */
    @Transactional
    fun updateMember(id: Long, request: AdminMemberUpdateRequest): MemberResponse =
        memberService.updateMember(
            id = id,
            name = request.name,
            email = request.email,
            role = request.role,
        )

    /**
     * 관리자가 회원을 삭제한다.
     *
     * @param id 삭제 대상 회원 PK
     */
    @Transactional
    fun deleteMember(id: Long) = memberService.deleteMember(id)
}
