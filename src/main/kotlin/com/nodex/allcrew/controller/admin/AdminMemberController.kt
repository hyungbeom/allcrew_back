package com.nodex.allcrew.controller.admin

import com.nodex.allcrew.dto.admin.request.AdminMemberCreateRequest
import com.nodex.allcrew.dto.admin.request.AdminMemberUpdateRequest
import com.nodex.allcrew.dto.response.MemberResponse
import com.nodex.allcrew.service.admin.AdminMemberService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 관리자(Admin) 전용 회원 API.
 *
 * `/api/admin/members` 하위에서 전체 회원 CRUD 및 역할 관리를 제공한다.
 * 추후 Spring Security로 ADMIN 역할만 접근 허용 예정.
 */
@RestController
@RequestMapping("/api/admin/members")
class AdminMemberController(
    private val adminMemberService: AdminMemberService,
) {

    /** 전체 회원 목록 조회 */
    @GetMapping
    fun getMembers(): List<MemberResponse> = adminMemberService.getMembers()

    /** 회원 단건 조회 */
    @GetMapping("/{id}")
    fun getMember(@PathVariable id: Long): MemberResponse = adminMemberService.getMember(id)

    /** 회원 생성 (역할 지정 가능) */
    @PostMapping
    fun createMember(
        @Valid @RequestBody request: AdminMemberCreateRequest,
    ): ResponseEntity<MemberResponse> {
        val member = adminMemberService.createMember(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(member)
    }

    /** 회원 수정 (역할 변경 포함) */
    @PutMapping("/{id}")
    fun updateMember(
        @PathVariable id: Long,
        @Valid @RequestBody request: AdminMemberUpdateRequest,
    ): MemberResponse = adminMemberService.updateMember(id, request)

    /** 회원 삭제 */
    @DeleteMapping("/{id}")
    fun deleteMember(@PathVariable id: Long): ResponseEntity<Void> {
        adminMemberService.deleteMember(id)
        return ResponseEntity.noContent().build()
    }
}
