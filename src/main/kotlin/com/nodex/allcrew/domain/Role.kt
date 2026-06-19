package com.nodex.allcrew.domain

/**
 * 회원 권한 역할.
 *
 * - [ADMIN]: 전체 회원 관리, 역할 변경 등 관리자 전용 기능
 * - [USER]: 일반 사용자 (가입·본인 프로필 조회/수정)
 */
enum class Role {
    ADMIN,
    USER,
}
