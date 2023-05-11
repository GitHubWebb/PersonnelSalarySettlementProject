package com.personal.salary.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.oubowu.stickyitemdecoration.adapter.StickerItemAdapter
import com.personal.salary.kotlin.app.AppApplication
import com.personal.salary.kotlin.db.dao.EmployeeRosterDao
import com.personal.salary.kotlin.ktx.toJson
import com.personal.salary.kotlin.manager.EmployeeRosterStore
import com.personal.salary.kotlin.manager.FirstDeptAndRosterCountVO
import com.personal.salary.kotlin.other.SearchType
import timber.log.Timber


/**
 * author : wangwx
 * github : https://github.com/GitHubWebb
 * time   : 2023/04/11
 * desc   : 员工 花名册的 ViewModel
 */
class EmployeeRosterViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val PAGE_SIZE = Int.MAX_VALUE  // 30
        private const val ENABLE_PLACEHOLDER = true
    }

    private val empDao: EmployeeRosterDao = AppApplication.getEmpDatabase().empDao()

    /** 保存 花名册 */
    fun saveRoster(employeeRosterStore: EmployeeRosterStore) {
        // 把 Cookies 保存到存储 Cookie 的 Room 数据库中
        empDao.insert(employeeRosterStore)
    }

    /** 保存 花名册 */
    fun saveRoster(employeeRosterStores: List<EmployeeRosterStore>) {
        // 把 Cookies 保存到存储 Cookie 的 Room 数据库中
        empDao.insert(employeeRosterStores)
    }

    /** 更新 花名册 */
    suspend fun updateRoster(
        employeeRosterStores: List<EmployeeRosterStore>,
        action: suspend () -> Unit
    ) {
        employeeRosterStores.isNotEmpty() ?: return

        for (employeeRosterStore in employeeRosterStores) {
            employeeRosterStore?.empIdCard?.let {
                var doQueryByIdCard = empDao.doQueryByIdCard(it)
                doQueryByIdCard?.let {
                    employeeRosterStore.uId = it.uId
                    empDao.update(employeeRosterStore)
                } ?: empDao.insert(employeeRosterStore)
            } ?: continue
        }

        action()
    }

    suspend fun saveOrUpdateRoster(
        employeeRosterStores: List<EmployeeRosterStore>,
        action: suspend () -> Unit
    ) {
        updateRoster(employeeRosterStores, action)
    }

    fun getRosterByName(empName: String) = empDao.doQueryByName(empName)

    /** 根据搜索条件获取员工信息 */
    fun getRosterByKeywords(keyword: String, searchType: String) = empDao.doQueryByKeyWord(keyword, searchType)

    fun getRosterList() = empDao.findAll()

    fun getRosterListGroupByDept(): List<EmployeeRosterStore>? {
        var queryGroupByDept = empDao.doQueryGroupByDept()
        var rosterStoreStickList = mutableListOf<EmployeeRosterStore>()

        // ?: 左侧为空 则返回右侧
        queryGroupByDept ?: return null

        /** 当前部门类型头 所在位置 */
        var curItemTypePos = 0;
        /** 人员数 */
        var peopleCount = 0;

        for (empRoster: EmployeeRosterStore in queryGroupByDept) {
            // 根据存储的部门类型头 获取粘性item 实体
            rosterStoreStickList.getOrNull(curItemTypePos)?.also {
                // 修改对应实体中 人员数量
                it.itemCount = peopleCount
            }

            // 根据条件查找 最后一条item数据 如果最后一条不是部门类型头 则添加 部门类型头
            rosterStoreStickList.findLast {
                it.itemType == StickerItemAdapter.TYPE_STICKY_HEAD && it.firstOrderDept.equals(
                    empRoster.firstOrderDept
                )
            } ?: rosterStoreStickList.takeIf {
                rosterStoreStickList.add(
                    EmployeeRosterStore(
                        itemType = StickerItemAdapter.TYPE_STICKY_HEAD,
                        firstOrderDept = empRoster.firstOrderDept,
                        itemCount = peopleCount
                    )
                )
            }.also {
                // 当 部门类型头添加成功 后 将人员数还原 当前下标缓存
                peopleCount = 0
                curItemTypePos = rosterStoreStickList.lastIndex
            }

            // 添加 正常 item 数据
            rosterStoreStickList.add(empRoster.apply {
                itemType = StickerItemAdapter.TYPE_DATA
                // 计算 人员数量
                peopleCount++
            })

            // 避免最后一条header头 人员数据值错误 刷新下
            // 根据存储的部门类型头 获取粘性item 实体
            rosterStoreStickList.getOrNull(curItemTypePos)?.also {
                // 修改对应实体中 人员数量
                it.itemCount = peopleCount
            }
        }

        Timber.d("rosterStoreStickList: ${rosterStoreStickList.toJson()}")
        return rosterStoreStickList
    }

    /** 获取部门名称及对应人员总数 */
    fun getDeptNameAndRosterCount(): MutableList<FirstDeptAndRosterCountVO>? {
        var deptNameRosterCount = empDao.doQueryDeptNameAndRosterCount()
        Timber.d("getDeptNameAndRosterCount: ${deptNameRosterCount.toJson()}")
        return deptNameRosterCount as MutableList<FirstDeptAndRosterCountVO>
    }
}
