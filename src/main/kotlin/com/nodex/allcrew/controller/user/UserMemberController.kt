package com.nodex.allcrew.controller.user

import com.nodex.allcrew.dto.response.MemberResponse
import com.nodex.allcrew.dto.user.request.UserProfileUpdateRequest
import com.nodex.allcrew.dto.user.request.UserSignupRequest
import com.nodex.allcrew.service.user.UserMemberService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 일반 사용자(User) 전용 회원 API.
 *
 * `/api/user/members` 하위에서 회원가입·프로필 조회/수정만 제공한다.
 * 추후 Spring Security로 본인 데이터만 접근 가능하도록 제한 예정.
 */
@RestController
@RequestMapping("/api/user/members")
class UserMemberController(
    private val userMemberService: UserMemberService,
) {

    /** 회원가입 (역할 USER 고정) */
    @PostMapping
    fun signup(
        @Valid @RequestBody request: UserSignupRequest,
    ): ResponseEntity<MemberResponse> {
        val member = userMemberService.signup(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(member)
    }

    /** 본인 프로필 조회 */
    @GetMapping("/{id}")
    fun getProfile(@PathVariable id: Long): MemberResponse = userMemberService.getProfile(id)

    /** 본인 프로필 수정 (이름·이메일만) */
    @PutMapping("/{id}")
    fun updateProfile(
        @PathVariable id: Long,
        @Valid @RequestBody request: UserProfileUpdateRequest,
    ): MemberResponse = userMemberService.updateProfile(id, request)
}
