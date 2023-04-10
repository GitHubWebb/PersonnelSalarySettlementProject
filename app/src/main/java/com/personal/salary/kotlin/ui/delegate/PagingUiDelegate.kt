package com.personal.salary.kotlin.ui.delegate

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.personal.salary.kotlin.action.Init
import com.personal.salary.kotlin.action.StatusAction
import com.personal.salary.kotlin.ktx.loadStateListener
import com.scwang.smart.refresh.layout.api.RefreshLayout

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/09
 * desc   : Paging3 分页列表的代理类
 */
class PagingUiDelegate(
    private val lifecycle: Lifecycle,
    statusAction: StatusAction,
    private val refreshLayout: RefreshLayout,
    private val recyclerView: RecyclerView,
    private val pagingDataAdapter: PagingDataAdapter<*, *>
) : Init, LifecycleEventObserver {

    private val loadStateListener = statusAction.loadStateListener(pagingDataAdapter) { refreshLayout.finishRefresh() }

    init {
        lifecycle.addObserver(this)
    }

    override fun initView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pagingDataAdapter
        }
    }

    override fun initEvent() {
        refreshLayout.setOnRefreshListener { pagingDataAdapter.refresh() }
        // 需要在 View 销毁的时候移除 listener
        pagingDataAdapter.addLoadStateListener(loadStateListener)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event.targetState) {
            Lifecycle.State.DESTROYED -> {
                lifecycle.removeObserver(this)
                pagingDataAdapter.removeLoadStateListener(loadStateListener)
            }
            else -> {}
        }
    }
}