package com.personal.salary.kotlin.manager

import androidx.lifecycle.ViewModelProvider
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.personal.salary.kotlin.app.AppApplication
import com.personal.salary.kotlin.viewmodel.EmployeeRosterViewModel
import net.fenghaitao.annotation.ExcelProperty

/**
 * author : wangwx
 * github : https://github.com/GitHubWebb
 * time   : 2023/04/11
 * desc   : 花名册(人员列表) 管理器
 */
class EmployeeRosterManager {

    private val empRosterViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(AppApplication.getInstance())
            .create(EmployeeRosterViewModel::class.java)
    }

    fun saveEmployeeRoster(employeeRosterStore: EmployeeRosterStore) =
        empRosterViewModel.saveRoster(employeeRosterStore)

    fun saveEmployeeRosterList(employeeRosterStores: List<EmployeeRosterStore>) =
        empRosterViewModel.saveOrUpdateRoster(employeeRosterStores)

    fun getRosterByName(empName: String) = empRosterViewModel.getRosterByName(empName)

    fun getRosterList() = empRosterViewModel.getRosterList()

    fun getRosterListGroupByDept() = empRosterViewModel.getRosterListGroupByDept()

    companion object {
        private var sINSTANCE: EmployeeRosterManager? = null

        @JvmStatic
        fun get(): EmployeeRosterManager {
            return sINSTANCE ?: synchronized(this) {
                val instance = EmployeeRosterManager()
                sINSTANCE = instance
                instance
            }
        }
    }
}

// @Entity(tableName = "emp_roster_store")
@Entity
data class EmployeeRosterStore @JvmOverloads constructor(

    /** item类型 @see [ StickerItemAdapter.TYPE_STICKY_HEAD, StickerItemAdapter.TYPE_DATA ] 忽略room字段 */
    @Ignore var itemType: Int = -1,

    /** item总数量 此处为部门人数 */
    @Ignore var itemCount: Int = 0,

    @PrimaryKey(autoGenerate = true) //可设置主键自增长
    var uId: Long = 0,

    @ExcelProperty("NO") var sortNo: Int = 0,

    /** 员工编号 */
    @ExcelProperty("员工编号") var empNo: String? = "",

    /** 员工姓名 */
    @ExcelProperty("姓名") var empName: String? = "",

    /** 手机号 */
    @ExcelProperty("手机号") var mobile: String? = "",

    /** 员工性别 */
    @ExcelProperty("性别") var empGender: String? = "",

    /** 员工身份证 */
    @ExcelProperty("身份证") var empIdCard: String? = "",

    /** 入职日期 */
    @ExcelProperty("入职日期") var entryDate: String? = "",

    /** 司龄 */
    @ExcelProperty("司龄") var divisionAge: String? = "",

    /** 所属单位 */
    @ExcelProperty("所属单位") var affiliatedUnit: String? = "",

    /** 员工工作地点 */
    @ExcelProperty("工作地点") var empWorkPlace: String? = "",

    /** 一级部门 */
    @ExcelProperty("总部") var firstOrderDept: String? = "",

    /** 团队名称 */
    @ExcelProperty("团队") var teamName: String? = "",

    /** 职位名称 */
    @ExcelProperty("职位名称") var jobTitle: String? = "",

    /** 职级名称 */
    @ExcelProperty("职级") var rankName: String? = "",

    )
