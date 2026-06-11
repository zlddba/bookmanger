package org.zl.team.mapper

import org.zl.team.entity.Admin

interface AdminMapper {
    fun selectById(userId: String): Admin?
    fun selectAll(): List<Admin>
    fun insert(admin: Admin): Int
    fun update(admin: Admin): Int
    fun delete(userId: String): Int
}
