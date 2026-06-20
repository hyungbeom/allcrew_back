package com.nodex.allcrew.controller.statistics

import com.nodex.allcrew.dto.operations.response.StatisticsResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/statistics")
class AdminStatisticsController(
    private val adminOperationsService: AdminOperationsService,
    private val adminAuthSupport: AdminAuthSupport,
) {
    @GetMapping
    fun getStatistics(
        @RequestHeader("Authorization") authorization: String?,
        @RequestParam(defaultValue = "quarter") period: String,
    ): StatisticsResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.getStatistics(auth, period)
    }
}
