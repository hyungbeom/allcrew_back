package com.nodex.allcrew.controller.ptt

import com.nodex.allcrew.dto.operations.response.PttOverviewResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/ptt")
class AdminPttController(
    private val adminOperationsService: AdminOperationsService,
    private val adminAuthSupport: AdminAuthSupport,
) {
    @GetMapping
    fun getOverview(
        @RequestHeader("Authorization") authorization: String?,
        @RequestParam(required = false) projectCode: String?,
    ): PttOverviewResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.getPttOverview(auth, projectCode)
    }
}
