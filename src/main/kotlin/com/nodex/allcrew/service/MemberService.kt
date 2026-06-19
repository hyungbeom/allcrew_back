package com.nodex.allcrew.service

import com.nodex.allcrew.domain.Member
import com.nodex.allcrew.domain.Role
import com.nodex.allcrew.dto.response.MemberResponse
import com.nodex.allcrew.exception.BusinessException
import com.nodex.allcrew.mapper.MemberMapper
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 회원 공통 데이터 서비스.
 *
 * Admin/User 서비스가 공통으로 사용하는 DB 접근·검증 로직을 담는다.
 * HTTP 계층과 분리되어 있으며, 권한 분기는 [AdminMemberService], [UserMemberService]에서 처리한다.
 */
@Service
class MemberService(
    private val memberMapper: MemberMapper,
) {

    /**
     * 전체 회원 목록을 조회한다.
     *
     * @return 회원 응답 목록
     */
    @Transactional(readOnly = true)
    fun getMembers(): List<MemberResponse> =
        memberMapper.findAll().map(MemberResponse::from)

    /**
     * PK로 회원 단건을 조회한다.
     *
     * @param id 조회할 회원 PK
     * @return 회원 응답 DTO
     * @throws BusinessException 회원이 없으면 404 NOT_FOUND
     */
    @Transactional(readOnly = true)
    fun getMember(id: Long): MemberResponse =
        memberMapper.findById(id)
            ?.let(MemberResponse::from)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다. id=$id")

    /**
     * 신규 회원을 등록한다.
     *
     * @param name 회원 이름
     * @param email 이메일 (UNIQUE)
     * @param role 권한 역할
     * @return 생성된 회원 정보
     * @throws BusinessException 동일 이메일이 이미 존재하면 409 CONFLICT
     */
    @Transactional
    fun createMember(name: String, email: String, role: Role): MemberResponse {
        validateEmailAvailable(email)

        val member = Member(name = name, email = email, role = role)
        memberMapper.insert(member)
        return getMember(member.id)
    }

    /**
     * 회원 정보를 수정한다.
     *
     * @param id 수정 대상 회원 PK
     * @param name 변경할 이름
     * @param email 변경할 이메일
     * @param role 변경할 역할
     * @return 수정된 회원 정보
     * @throws BusinessException 회원이 없으면 404, 이메일 중복이면 409
     */
    @Transactional
    fun updateMember(id: Long, name: String, email: String, role: Role): MemberResponse {
        val existing = memberMapper.findById(id)
            ?: throw BusinessException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다. id=$id")

        validateEmailAvailable(email, excludeId = id)

        memberMapper.update(
            existing.copy(
                name = name,
                email = email,
                role = role,
            ),
        )
        return getMember(id)
    }

    /**
     * 회원을 삭제한다.
     *
     * @param id 삭제할 회원 PK
     * @throws BusinessException 삭제 대상이 없으면 404 NOT_FOUND
     */
    @Transactional
    fun deleteMember(id: Long) {
        if (memberMapper.deleteById(id) == 0) {
            throw BusinessException(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다. id=$id")
        }
    }

    /**
     * 이메일 사용 가능 여부를 검증한다.
     *
     * @param email 검사할 이메일
     * @param excludeId 수정 시 본인 PK (본인 이메일은 허용)
     * @throws BusinessException 다른 회원이 사용 중이면 409 CONFLICT
     */
    private fun validateEmailAvailable(email: String, excludeId: Long? = null) {
        val duplicated = memberMapper.findByEmail(email)
        if (duplicated != null && duplicated.id != excludeId) {
            throw BusinessException(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다.")
        }
    }
}
