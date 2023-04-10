package com.personal.salary.kotlin.app

import androidx.annotation.CallSuper
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.action.StatusAction
import com.personal.salary.kotlin.ui.delegate.PagingUiDelegate
import com.personal.salary.kotlin.widget.StatusLayout
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/09
 * desc   : 使用 Paging3 分页带标题栏的 Fragment 基类
 */
abstract class PagingTitleBarFragment<A : AppActivity> : TitleBarFragment<A>(), StatusAction {

    private val refreshLayout by lazy {
        requireNotNull(requireView().findViewById<SmartRefreshLayout>(R.id.paging_refresh_layout)) {
            checkLayoutIdTips(
                "paging_refresh_layout"
            )
        }
    }
    private val recyclerView by lazy {
        requireNotNull(requireView().findViewById<RecyclerView>(R.id.paging_recycler_view)) {
            checkLayoutIdTips(
                "paging_recycler_view"
            )
        }
    }
    private val pagingDataAdapter by lazy { getPagingAdapter() }
    private val mPagingUiDelegate by lazy {
        PagingUiDelegate(
            viewLifecycleOwner.lifecycle,
            this,
            refreshLayout,
            recyclerView,
            pagingDataAdapter
        )
    }

    private fun checkLayoutIdTips(idName: String) = "are you ok? Do you have a control with id $idName in your xml layout? "

    override fun getStatusLayout(): StatusLayout? = requireView().findViewById(R.id.paging_status_layout)

    abstract fun getPagingAdapter(): PagingDataAdapter<*, *>

    @CallSuper
    override fun initView() {
        mPagingUiDelegate.initView()
    }

    @CallSuper
    override fun initData() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated { loadListData() }
    }

    abstract suspend fun loadListData()

    @CallSuper
    override fun initEvent() {
        mPagingUiDelegate.initEvent()
    }
}
