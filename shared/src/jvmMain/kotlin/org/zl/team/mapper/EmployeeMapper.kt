package org.zl.team.mapper

import org.zl.team.entity.Employee

interface EmployeeMapper {
    fun selectById(account: String): Employee?
    fun selectAll(): List<Employee>
    fun insert(employee: Employee): Int
    fun update(employee: Employee): Int
    fun delete(account: String): Int
}
