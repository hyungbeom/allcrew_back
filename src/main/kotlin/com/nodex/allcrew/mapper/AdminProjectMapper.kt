package com.nodex.allcrew.mapper

import com.nodex.allcrew.domain.AdminProject
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface AdminProjectMapper {
    fun insert(project: AdminProject): Int

    fun findNextProjectSequence(): Int

    fun findByAgencyId(@Param("agencyId") agencyId: Long): List<AdminProject>

    fun findByProjectCode(@Param("projectCode") projectCode: String): AdminProject?

    fun findById(@Param("id") id: Long): AdminProject?

    fun countByAgencyIdAndStatus(
        @Param("agencyId") agencyId: Long,
        @Param("status") status: String,
    ): Int

    fun updateLocation(
        @Param("projectCode") projectCode: String,
        @Param("latitude") latitude: Double,
        @Param("longitude") longitude: Double,
        @Param("address") address: String?,
    ): Int
}
