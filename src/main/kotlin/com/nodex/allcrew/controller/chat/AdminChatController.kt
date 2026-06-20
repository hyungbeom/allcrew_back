package com.nodex.allcrew.controller.chat

import com.nodex.allcrew.dto.operations.request.CreateDirectChatRequest
import com.nodex.allcrew.dto.operations.request.CreateGroupChatRequest
import com.nodex.allcrew.dto.operations.request.SendChatMessageRequest
import com.nodex.allcrew.dto.operations.response.ChatListResponse
import com.nodex.allcrew.dto.operations.response.ChatMessageListResponse
import com.nodex.allcrew.dto.operations.response.CreateDirectChatResponse
import com.nodex.allcrew.dto.operations.response.CreateGroupChatResponse
import com.nodex.allcrew.dto.operations.response.SendChatMessageResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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

    @PostMapping("/direct")
    fun createDirectChat(
        @RequestHeader("Authorization") authorization: String?,
        @Valid @RequestBody request: CreateDirectChatRequest,
    ): CreateDirectChatResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.createDirectChat(auth, request)
    }

    @PostMapping("/group")
    fun createGroupChat(
        @RequestHeader("Authorization") authorization: String?,
        @Valid @RequestBody request: CreateGroupChatRequest,
    ): CreateGroupChatResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.createGroupChat(auth, request)
    }

    @GetMapping("/{roomCode}/messages")
    fun listChatMessages(
        @RequestHeader("Authorization") authorization: String?,
        @PathVariable roomCode: String,
    ): ChatMessageListResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.listChatMessages(auth, roomCode)
    }

    @PostMapping("/{roomCode}/messages")
    fun sendChatMessage(
        @RequestHeader("Authorization") authorization: String?,
        @PathVariable roomCode: String,
        @Valid @RequestBody request: SendChatMessageRequest,
    ): SendChatMessageResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.sendChatMessage(auth, roomCode, request)
    }
}
