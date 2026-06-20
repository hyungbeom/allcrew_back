package com.nodex.allcrew.controller.settlement

import com.nodex.allcrew.dto.operations.response.SettlementListResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/settlements")
class AdminSettlementController(
    private val adminOperationsService: AdminOperationsService,
    private val adminAuthSupport: AdminAuthSupport,
) {
    @GetMapping
    fun listSettlements(
        @RequestHeader("Authorization") authorization: String?,
    ): SettlementListResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.listSettlements(auth)
    }
}
