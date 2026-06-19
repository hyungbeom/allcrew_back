package com.nodex.allcrew.service.user

import com.nodex.allcrew.domain.Role
import com.nodex.allcrew.dto.user.request.UserProfileUpdateRequest
import com.nodex.allcrew.dto.user.request.UserSignupRequest
import com.nodex.allcrew.exception.BusinessException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

/**
 * [UserMemberService] 통합 테스트.
 */
@SpringBootTest
@Transactional
class UserMemberServiceTest {

    @Autowired
    private lateinit var userMemberService: UserMemberService

    /**
     * 회원가입 시 역할이 항상 USER로 설정되어야 한다.
     */
    @Test
    fun signupCreatesUserRole() {
        val created = userMemberService.signup(
            UserSignupRequest(name = "홍길동", email = "hong@example.com"),
        )

        assertEquals(Role.USER, created.role)
        assertEquals("홍길동", created.name)
    }

    /**
     * 프로필 수정 시 역할은 변경되지 않아야 한다.
     */
    @Test
    fun updateProfileKeepsRole() {
        val created = userMemberService.signup(
            UserSignupRequest(name = "홍길동", email = "hong@example.com"),
        )

        val updated = userMemberService.updateProfile(
            created.id,
            UserProfileUpdateRequest(name = "김철수", email = "kim@example.com"),
        )

        assertEquals(Role.USER, updated.role)
        assertEquals("김철수", updated.name)
    }

    /**
     * 이미 등록된 이메일로 가입 시도하면 409 CONFLICT가 발생해야 한다.
     */
    @Test
    fun duplicateEmailThrowsConflict() {
        userMemberService.signup(
            UserSignupRequest(name = "홍길동", email = "hong@example.com"),
        )

        val exception = assertThrows(BusinessException::class.java) {
            userMemberService.signup(
                UserSignupRequest(name = "김철수", email = "hong@example.com"),
            )
        }

        assertEquals(409, exception.status.value())
    }
}
