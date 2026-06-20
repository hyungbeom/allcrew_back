package com.nodex.allcrew.dto.operations.request

import jakarta.validation.constraints.NotBlank

data class UpdateAgencySettingsRequest(
    @field:NotBlank val companyName: String,
    val address: String? = null,
    val addressDetail: String? = null,
)

data class UpdateEducationSettingsRequest(
    val projectCode: String? = null,
    val ktlRequired: Boolean = false,
    val siteRequired: Boolean = false,
)
