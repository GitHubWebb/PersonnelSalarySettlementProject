package com.personal.salary.kotlin.ui.fragment

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.blankj.utilcode.util.FileUtils
import com.hjq.base.BaseAdapter
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.action.OnBack2TopListener
import com.personal.salary.kotlin.app.TitleBarFragment
import com.personal.salary.kotlin.databinding.StatusFragmentBinding
import com.personal.salary.kotlin.ktx.dp
import com.personal.salary.kotlin.ktx.toJson
import com.personal.salary.kotlin.manager.EmployeeRosterManager
import com.personal.salary.kotlin.manager.EmployeeRosterStore
import com.personal.salary.kotlin.model.ImportRule
import com.personal.salary.kotlin.other.PermissionCallback
import com.personal.salary.kotlin.ui.activity.HomeActivity
import com.personal.salary.kotlin.ui.adapter.ImportAdapter
import com.personal.salary.kotlin.util.SimpleLinearSpaceItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.rosuh.filepicker.config.FilePickerManager
import net.fenghaitao.AutoExcel
import net.fenghaitao.parameters.FieldSetting
import net.fenghaitao.parameters.ImportPara
import timber.log.Timber
import kotlin.concurrent.thread

/**
 *    author : wangwx
 *    github : https://github.com/GitHubWebb
 *    time   : 2023/04/12
 *    desc   : 导入 Fragment
 */
class ImportFragment : TitleBarFragment<HomeActivity>(), OnBack2TopListener,
    BaseAdapter.OnItemClickListener {

    private val mBinding: StatusFragmentBinding by viewBinding()
    private var adapter: ImportAdapter? = null

    override fun getLayoutId(): Int {
        return R.layout.status_fragment
    }

    override fun initView() {
        mBinding.titleBar.rightIcon = null
        mBinding.titleBar.title = "导入"

        adapter = ImportAdapter(getAttachActivity()!!)
        adapter?.setOnItemClickListener(this)

        mBinding.rvStatusList.adapter = adapter
        mBinding.rvStatusList.addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))

        mBinding.rlStatusRefresh.setEnableRefresh(false)
        (mBinding.rlStatusRefresh.layoutParams as ConstraintLayout.LayoutParams).topMargin = 5.dp
    }

    override fun initData() {
        adapter?.apply {
            setData(analogData())
        }

    }

    /**
     * 模拟数据
     */
    private fun analogData(): MutableList<ImportRule.ImportType?> {
        val dataRule: MutableList<ImportRule.ImportType?> = ArrayList()

        dataRule.add(ImportRule.ImportType.IMPORT_ROSTER)
        dataRule.add(ImportRule.ImportType.IMPORT_SOP)

        /*val data: MutableList<String?> = ArrayList()
        adapter?.let {
            for (i in it.getCount() until it.getCount() + 20) {
                data.add("我是第" + i + "条目")
            }
            return data
        }
        return data
        */
        return dataRule
    }

    override fun onBack2Top() {
        mBinding.rvStatusList.scrollToPosition(0)
    }

    /**
     * [BaseAdapter.OnItemClickListener]
     *
     * @param recyclerView      RecyclerView对象
     * @param itemView          被点击的条目对象
     * @param position          被点击的条目位置
     */
    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        var importType = adapter?.getItem(position)
        when (importType) {
            ImportRule.ImportType.IMPORT_ROSTER -> {
                XXPermissions.with(this@ImportFragment)
                    // .permission(*Permission.Group.STORAGE)
                    .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                    .request(object : PermissionCallback() {
                        override fun onGranted(
                            permissions: MutableList<String>, allGranted: Boolean
                        ) {
                            if (!allGranted) {
                                toast("获取部分权限成功，但部分权限未正常授予")
                                return
                            }

                            FilePickerManager.from(this@ImportFragment)
                                .forResult(FilePickerManager.REQUEST_CODE)

                        }

                        override fun onDenied(
                            permissions: MutableList<String>, doNotAskAgain: Boolean
                        ) {
                            if (doNotAskAgain) {
                                toast("被永久拒绝授权，请手动授予权限");
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                context?.let {
                                    XXPermissions.startPermissionActivity(
                                        it, permissions
                                    )
                                }
                            }
                        }
                    })
            }
            ImportRule.ImportType.IMPORT_SOP -> {
                toast(importType.itemName)
            }
            else -> {}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            FilePickerManager.REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val list = FilePickerManager.obtainData()
                    toast("$list")

                    thread {
                        for (dir in list) {
                            var fileByPath = FileUtils.getFileByPath(dir)

                            // if (!ExcelUtil.checkIfExcelFile(fileByPath)) continue
                            // ExcelUtil.readExcel(fileByPath)

                            var importParas = mutableListOf<ImportPara>(
                                ImportPara(
                                    0,
                                    AutoExcel.genProjectFieldSettings(EmployeeRosterStore::class.java)
                                )
                            )

                            var dataSet = AutoExcel.read(dir, importParas);
                            // 方式一、获取原始数据，没有类型转换，可通过这种方式检验数据是否符合要求
                            // var products = dataSet.get("Product");
                            // 方式二、通过sheet索引获取指定类的数据，类型自动转换，转换失败将抛出异常
                            var products = dataSet.get("花名册-ok", EmployeeRosterStore::class.java);
                            Timber.d("dataSet: ${dataSet}")
                            Timber.d("products: ${products.toJson()}")
                            // List<Project> projects= dataSet.get(1, Project.class);
                            // 方式三、通过sheet名称获取指定类的数据，类型自动转换，转换失败将抛出异常
                            // List<Product> products = dataSet.get("Product", Product.class);
                            // List<Project> projects = dataSet.get("Project", Project.class);
                            lifecycleScope.launch(Dispatchers.IO) {
                                val employeeRosterManager = EmployeeRosterManager.get()
                                employeeRosterManager.saveEmployeeRosterList(products)

                            }

                        }

                    }
                    // do your work
                } else {
                    toast("没有选择任何东西~")
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = ImportFragment()
    }

}