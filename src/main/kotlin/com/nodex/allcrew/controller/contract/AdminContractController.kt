package com.nodex.allcrew.controller.contract

import com.nodex.allcrew.dto.operations.response.ContractListResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/contracts")
class AdminContractController(
    private val adminOperationsService: AdminOperationsService,
    private val adminAuthSupport: AdminAuthSupport,
) {
    @GetMapping
    fun listContracts(
        @RequestHeader("Authorization") authorization: String?,
    ): ContractListResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.listContracts(auth)
    }
}
