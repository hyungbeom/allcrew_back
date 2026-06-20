package com.nodex.allcrew.mapper

import com.nodex.allcrew.domain.AdminAgency
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface AdminAgencyMapper {
    fun insert(agency: AdminAgency): Int

    fun findByBusinessNumber(@Param("businessNumber") businessNumber: String): AdminAgency?

    fun findBySlug(@Param("companySlug") companySlug: String): AdminAgency?

    fun existsBySlug(@Param("companySlug") companySlug: String): Boolean

    fun findById(@Param("id") id: Long): AdminAgency?

    fun updateAgency(agency: AdminAgency): Int
}
