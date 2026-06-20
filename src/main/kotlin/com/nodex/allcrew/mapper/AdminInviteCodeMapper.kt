package com.nodex.allcrew.mapper

import com.nodex.allcrew.domain.AdminInviteCode
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface AdminInviteCodeMapper {
    fun findByCode(@Param("code") code: String): AdminInviteCode?

    fun markUsed(
        @Param("id") id: Long,
        @Param("usedByMemberId") usedByMemberId: Long,
    ): Int
}
