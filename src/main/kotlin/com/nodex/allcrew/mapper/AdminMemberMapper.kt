package com.nodex.allcrew.mapper

import com.nodex.allcrew.domain.AdminMember
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface AdminMemberMapper {
    fun insert(member: AdminMember): Int

    fun findByEmail(@Param("email") email: String): AdminMember?

    fun existsByEmail(@Param("email") email: String): Boolean

    fun updateLastLoginAt(@Param("id") id: Long): Int

    fun findById(@Param("id") id: Long): AdminMember?
}
