package org.zl.team.mapper

import org.zl.team.entity.Member
import org.apache.ibatis.annotations.Param

interface MemberMapper {
    fun selectById(cardNo: String): Member?
    fun selectAll(): List<Member>
    fun insert(member: Member): Int
    fun update(member: Member): Int
    fun updateLevel(@Param("cardNo") cardNo: String, @Param("level") level: Int): Int
    fun updateTotalSpent(@Param("cardNo") cardNo: String, @Param("totalSpent") totalSpent: Double): Int
    fun delete(cardNo: String): Int
}
