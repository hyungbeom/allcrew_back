package com.nodex.allcrew.controller.education

import com.nodex.allcrew.dto.operations.request.UpdateEducationSettingsRequest
import com.nodex.allcrew.dto.operations.response.EducationOverviewResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/education")
class AdminEducationController(
    private val adminOperationsService: AdminOperationsService,
    private val adminAuthSupport: AdminAuthSupport,
) {
    @GetMapping
    fun getOverview(
        @RequestHeader("Authorization") authorization: String?,
        @RequestParam(required = false) projectCode: String?,
    ): EducationOverviewResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.getEducationOverview(auth, projectCode)
    }

    @PutMapping
    fun updateSettings(
        @RequestHeader("Authorization") authorization: String?,
        @RequestBody request: UpdateEducationSettingsRequest,
    ): EducationOverviewResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        adminOperationsService.updateEducationSettings(auth, request)
        return adminOperationsService.getEducationOverview(auth, request.projectCode)
    }
}
