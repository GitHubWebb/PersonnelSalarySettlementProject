package com.personal.salary.kotlin.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.personal.salary.kotlin.manager.EmployeeRosterStore

@Dao
abstract class EmployeeRosterDao : BaseDao<EmployeeRosterStore>() {

    @Transaction
    @Query("SELECT * FROM EmployeeRosterStore WHERE empName = :empName")
    abstract fun doQueryByName(empName: String): EmployeeRosterStore?

    @Transaction
    @Query("SELECT * FROM EmployeeRosterStore WHERE empIdCard = :empIdCard")
    abstract fun doQueryByIdCard(empIdCard: String): EmployeeRosterStore?

    /** 根据部门分组排序 */
    @Transaction
    @Query("SELECT * \n" +
            "FROM EmployeeRosterStore\n" +
            " GROUP BY firstOrderDept, empIdCard\n" +
            "ORDER BY firstOrderDept, jobTitle ASC")
    abstract fun doQueryGroupByDept(): List<EmployeeRosterStore>?
}