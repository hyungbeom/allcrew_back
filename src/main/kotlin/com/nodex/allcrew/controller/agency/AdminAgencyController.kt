package com.nodex.allcrew.controller.agency

import com.nodex.allcrew.dto.operations.request.UpdateAgencySettingsRequest
import com.nodex.allcrew.dto.operations.response.AgencySettingsResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/agency")
class AdminAgencyController(
    private val adminOperationsService: AdminOperationsService,
    private val adminAuthSupport: AdminAuthSupport,
) {
    @GetMapping
    fun getSettings(
        @RequestHeader("Authorization") authorization: String?,
    ): AgencySettingsResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.getAgencySettings(auth)
    }

    @PutMapping
    fun updateSettings(
        @RequestHeader("Authorization") authorization: String?,
        @Valid @RequestBody request: UpdateAgencySettingsRequest,
    ): AgencySettingsResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        adminOperationsService.updateAgencySettings(auth, request)
        return adminOperationsService.getAgencySettings(auth)
    }
}
