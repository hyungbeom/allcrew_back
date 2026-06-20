package com.nodex.allcrew.controller.crew

import com.nodex.allcrew.dto.operations.response.CrewListResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/crew")
class AdminCrewController(
    private val adminOperationsService: AdminOperationsService,
    private val adminAuthSupport: AdminAuthSupport,
) {
    @GetMapping
    fun listCrew(
        @RequestHeader("Authorization") authorization: String?,
    ): CrewListResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.listCrew(auth)
    }
}
