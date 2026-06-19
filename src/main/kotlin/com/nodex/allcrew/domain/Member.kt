package com.nodex.allcrew.domain

import java.time.LocalDateTime

/**
 * 회원 도메인 엔티티.
 *
 * `members` 테이블과 1:1로 매핑되며, MyBatis를 통해 DB와 주고받는다.
 * [Role]에 따라 Admin/User API에서 접근 가능한 기능이 달라진다.
 */
data class Member(
    /** DB 자동 생성 PK (insert 전에는 0) */
    val id: Long = 0,

    /** 회원 이름 */
    val name: String = "",

    /** 로그인/식별용 이메일 (DB UNIQUE 제약) */
    val email: String = "",

    /** 권한 역할 (ADMIN / USER) */
    val role: Role = Role.USER,

    /** 레코드 최초 생성 시각 (DB DEFAULT CURRENT_TIMESTAMP) */
    val createdAt: LocalDateTime? = null,

    /** 레코드 마지막 수정 시각 (update 시 DB에서 갱신) */
    val updatedAt: LocalDateTime? = null,
)
