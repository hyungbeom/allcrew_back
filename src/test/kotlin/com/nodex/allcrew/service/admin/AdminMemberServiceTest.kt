package com.nodex.allcrew.service.admin

import com.nodex.allcrew.domain.Role
import com.nodex.allcrew.dto.admin.request.AdminMemberCreateRequest
import com.nodex.allcrew.dto.admin.request.AdminMemberUpdateRequest
import com.nodex.allcrew.exception.BusinessException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

/**
 * [AdminMemberService] 통합 테스트.
 */
@SpringBootTest
@Transactional
class AdminMemberServiceTest {

    @Autowired
    private lateinit var adminMemberService: AdminMemberService

    /**
     * 관리자가 ADMIN 역할 회원을 생성할 수 있어야 한다.
     */
    @Test
    fun createAdminMember() {
        val created = adminMemberService.createMember(
            AdminMemberCreateRequest(
                name = "관리자2",
                email = "admin2@example.com",
                role = Role.ADMIN,
            ),
        )

        assertEquals(Role.ADMIN, created.role)
    }

    /**
     * 관리자가 회원 역할을 USER → ADMIN으로 변경할 수 있어야 한다.
     */
    @Test
    fun updateMemberRole() {
        val created = adminMemberService.createMember(
            AdminMemberCreateRequest(
                name = "홍길동",
                email = "hong@example.com",
                role = Role.USER,
            ),
        )

        val updated = adminMemberService.updateMember(
            created.id,
            AdminMemberUpdateRequest(
                name = "홍길동",
                email = "hong@example.com",
                role = Role.ADMIN,
            ),
        )

        assertEquals(Role.ADMIN, updated.role)
    }

    /**
     * 전체 목록 조회 시 data.sql의 기본 관리자 계정이 포함되어야 한다.
     */
    @Test
    fun getMembersIncludesSeedAdmin() {
        val members = adminMemberService.getMembers()
        assertTrue(members.any { it.email == "admin@example.com" && it.role == Role.ADMIN })
    }

    /**
     * 관리자가 회원을 삭제할 수 있어야 한다.
     */
    @Test
    fun deleteMember() {
        val created = adminMemberService.createMember(
            AdminMemberCreateRequest(
                name = "삭제대상",
                email = "delete@example.com",
                role = Role.USER,
            ),
        )

        adminMemberService.deleteMember(created.id)

        val exception = assertThrows(BusinessException::class.java) {
            adminMemberService.getMember(created.id)
        }
        assertEquals(404, exception.status.value())
    }
}
