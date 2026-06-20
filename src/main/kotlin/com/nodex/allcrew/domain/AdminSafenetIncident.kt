package com.nodex.allcrew.domain

import java.time.LocalDateTime

data class AdminSafenetIncident(
    val id: Long? = null,
    val agencyId: Long,
    val incidentCode: String,
    val title: String,
    val projectName: String,
    val location: String,
    val reporter: String,
    val incidentTime: String,
    val status: String,
    val projectCode: String,
    val createdAt: LocalDateTime? = null,
)
