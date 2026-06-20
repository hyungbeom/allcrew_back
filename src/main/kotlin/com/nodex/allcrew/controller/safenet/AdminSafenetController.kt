package com.nodex.allcrew.controller.safenet

import com.nodex.allcrew.dto.operations.response.SafenetListResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/safenet")
class AdminSafenetController(
    private val adminOperationsService: AdminOperationsService,
    private val adminAuthSupport: AdminAuthSupport,
) {
    @GetMapping
    fun listIncidents(
        @RequestHeader("Authorization") authorization: String?,
    ): SafenetListResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.listSafenet(auth)
    }
}
