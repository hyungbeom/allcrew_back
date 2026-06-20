package com.nodex.allcrew.mapper

import com.nodex.allcrew.domain.AdminActivity
import com.nodex.allcrew.domain.AdminChatRoom
import com.nodex.allcrew.domain.AdminContract
import com.nodex.allcrew.domain.AdminCrewMember
import com.nodex.allcrew.domain.AdminEducationSettings
import com.nodex.allcrew.domain.AdminSafenetIncident
import com.nodex.allcrew.domain.AdminSettlement
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface AdminOperationsMapper {
    fun findCrewByAgencyId(@Param("agencyId") agencyId: Long): List<AdminCrewMember>

    fun findCrewProjectCodes(@Param("crewId") crewId: Long): List<String>

    fun findContractsByAgencyId(@Param("agencyId") agencyId: Long): List<AdminContract>

    fun findSettlementsByAgencyId(@Param("agencyId") agencyId: Long): List<AdminSettlement>

    fun findChatRoomsByAgencyId(@Param("agencyId") agencyId: Long): List<AdminChatRoom>

    fun findIncidentsByAgencyId(@Param("agencyId") agencyId: Long): List<AdminSafenetIncident>

    fun findActivitiesByAgencyId(@Param("agencyId") agencyId: Long): List<AdminActivity>

    fun findEducationSettings(
        @Param("agencyId") agencyId: Long,
        @Param("projectCode") projectCode: String?,
    ): AdminEducationSettings?

    fun insertEducationSettings(settings: AdminEducationSettings): Int

    fun updateEducationSettings(settings: AdminEducationSettings): Int

    fun countSettlementsByStatus(@Param("agencyId") agencyId: Long, @Param("status") status: String): Int

    fun countCrewByAgencyId(@Param("agencyId") agencyId: Long): Int

    fun countIncidentsByStatus(@Param("agencyId") agencyId: Long, @Param("status") status: String): Int
}
