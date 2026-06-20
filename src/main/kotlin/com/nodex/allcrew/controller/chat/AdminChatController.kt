package com.nodex.allcrew.controller.chat

import com.nodex.allcrew.dto.operations.response.ChatListResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/chats")
class AdminChatController(
    private val adminOperationsService: AdminOperationsService,
    private val adminAuthSupport: AdminAuthSupport,
) {
    @GetMapping
    fun listChats(
        @RequestHeader("Authorization") authorization: String?,
    ): ChatListResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.listChats(auth)
    }
}
