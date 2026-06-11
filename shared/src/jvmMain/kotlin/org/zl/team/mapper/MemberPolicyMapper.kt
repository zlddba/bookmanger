package org.zl.team.mapper

import org.zl.team.entity.MemberPolicy

interface MemberPolicyMapper {
    fun selectByLevel(level: Int): MemberPolicy?
    fun selectAll(): List<MemberPolicy>
    fun selectByMinAmount(amount: Int): MemberPolicy?
    fun insert(policy: MemberPolicy): Int
    fun update(policy: MemberPolicy): Int
    fun delete(level: Int): Int
}
