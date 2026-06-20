package com.nodex.allcrew.mapper

import com.nodex.allcrew.domain.AdminProjectPosition
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface AdminProjectPositionMapper {
    fun insert(position: AdminProjectPosition): Int

    fun insertBatch(@Param("positions") positions: List<AdminProjectPosition>): Int

    fun findByProjectId(@Param("projectId") projectId: Long): List<AdminProjectPosition>
}
