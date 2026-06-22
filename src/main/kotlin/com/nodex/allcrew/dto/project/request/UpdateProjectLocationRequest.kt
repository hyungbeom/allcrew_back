package com.nodex.allcrew.dto.project.request

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull

data class UpdateProjectLocationRequest(
    @field:NotNull(message = "위도를 입력해 주세요.")
    @field:DecimalMin("-90.0", message = "위도 값이 올바르지 않습니다.")
    @field:DecimalMax("90.0", message = "위도 값이 올바르지 않습니다.")
    val latitude: Double,

    @field:NotNull(message = "경도를 입력해 주세요.")
    @field:DecimalMin("-180.0", message = "경도 값이 올바르지 않습니다.")
    @field:DecimalMax("180.0", message = "경도 값이 올바르지 않습니다.")
    val longitude: Double,

    val address: String? = null,
)
