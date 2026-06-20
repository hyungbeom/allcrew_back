package com.nodex.allcrew.controller.dashboard

import com.nodex.allcrew.dto.operations.response.DashboardResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/dashboard")
class AdminDashboardController(
    private val adminOperationsService: AdminOperationsService,
    private val adminAuthSupport: AdminAuthSupport,
) {
    @GetMapping
    fun getDashboard(
        @RequestHeader("Authorization") authorization: String?,
    ): DashboardResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.getDashboard(auth)
    }
}
