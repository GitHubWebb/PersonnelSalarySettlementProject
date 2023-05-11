package com.personal.salary.kotlin.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.personal.salary.kotlin.manager.EmployeeRosterStore
import com.personal.salary.kotlin.manager.FirstDeptAndRosterCountVO

@Dao
abstract class EmployeeRosterDao : BaseDao<EmployeeRosterStore>() {

    @Transaction
    @Query("SELECT * FROM EmployeeRosterStore WHERE empName = :empName")
    abstract fun doQueryByName(empName: String): List<EmployeeRosterStore>?

    @Transaction
    @Query("SELECT * FROM EmployeeRosterStore WHERE empIdCard = :empIdCard")
    abstract fun doQueryByIdCard(empIdCard: String): EmployeeRosterStore?

    /** 根据关键字搜索人员 */
    @Transaction
    @Query("SELECT * FROM EmployeeRosterStore WHERE empName LIKE '%' || :empName || '%' OR firstOrderDept LIKE '%' || :firstOrderDept || '%' ")
    abstract fun doQueryByLikeKeyWord(
        empName: String,
        firstOrderDept: String
    ): PagingSource<Int, EmployeeRosterStore>

    /** 根据关键字搜索人员 */
    @Transaction
    @Query("SELECT * FROM EmployeeRosterStore WHERE instr(empName, :empName) > 0  AND  (firstOrderDept = :firstOrderDept OR :firstOrderDept = '全部')  ")
    abstract fun doQueryByKeyWord(
        empName: String,
        firstOrderDept: String
    ): PagingSource<Int, EmployeeRosterStore>

    /** 根据部门分组排序 */
    @Transaction
    @Query(
        "SELECT * \n" +
                "FROM EmployeeRosterStore\n" +
                " GROUP BY firstOrderDept, empIdCard\n" +
                "ORDER BY firstOrderDept, jobTitle ASC"
    )
    abstract fun doQueryGroupByDept(): List<EmployeeRosterStore>?

    /** 获取部门名称及对应人员总数 */
    @Transaction
    @Query(
        "SELECT firstOrderDept, count(*) as rosterCount FROM EmployeeRosterStore\n" +
                "GROUP BY firstOrderDept ORDER BY rosterCount DESC"
    )
    abstract fun doQueryDeptNameAndRosterCount(): List<FirstDeptAndRosterCountVO>?

}