package com.nodex.allcrew.controller.project

import com.nodex.allcrew.dto.project.request.CreateProjectRequest
import com.nodex.allcrew.dto.project.request.UpdateProjectLocationRequest
import com.nodex.allcrew.dto.project.response.CreateProjectResponse
import com.nodex.allcrew.dto.project.response.ProjectDetailResponse
import com.nodex.allcrew.dto.project.response.ProjectListResponse
import com.nodex.allcrew.dto.operations.response.ProjectFilterResponse
import com.nodex.allcrew.service.auth.AdminAuthSupport
import com.nodex.allcrew.service.operations.AdminOperationsService
import com.nodex.allcrew.service.project.AdminProjectService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/projects")
class AdminProjectController(
    private val adminProjectService: AdminProjectService,
    private val adminOperationsService: AdminOperationsService,
    private val adminAuthSupport: AdminAuthSupport,
) {
    @GetMapping("/filter-options")
    fun filterOptions(
        @RequestHeader("Authorization") authorization: String?,
    ): ProjectFilterResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminOperationsService.getProjectFilterOptions(auth)
    }

    @GetMapping
    fun listProjects(
        @RequestHeader("Authorization") authorization: String?,
    ): ProjectListResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminProjectService.listProjects(auth)
    }

    @GetMapping("/{projectCode}")
    fun getProject(
        @RequestHeader("Authorization") authorization: String?,
        @PathVariable projectCode: String,
    ): ProjectDetailResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminProjectService.getProjectDetail(auth, projectCode)
    }

    @PostMapping
    fun createProject(
        @RequestHeader("Authorization") authorization: String?,
        @Valid @RequestBody request: CreateProjectRequest,
    ): CreateProjectResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminProjectService.createProject(auth, request)
    }

    @PatchMapping("/{projectCode}/location")
    fun updateProjectLocation(
        @RequestHeader("Authorization") authorization: String?,
        @PathVariable projectCode: String,
        @Valid @RequestBody request: UpdateProjectLocationRequest,
    ): ProjectDetailResponse {
        val auth = adminAuthSupport.authenticate(authorization)
        return adminProjectService.updateProjectLocation(auth, projectCode, request)
    }
}
