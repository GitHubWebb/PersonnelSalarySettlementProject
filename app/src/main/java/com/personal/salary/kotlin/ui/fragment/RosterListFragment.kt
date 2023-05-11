package com.personal.salary.kotlin.ui.fragment

import android.annotation.SuppressLint
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dylanc.longan.applicationViewModels
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.oubowu.stickyitemdecoration.OnStickyChangeListener
import com.oubowu.stickyitemdecoration.StickyHeadContainer
import com.oubowu.stickyitemdecoration.StickyItemDecoration
import com.oubowu.stickyitemdecoration.adapter.StickerItemAdapter
import com.oubowu.stickyitemdecoration.itemdecoration.SpaceItemDecoration
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.action.OnBack2TopListener
import com.personal.salary.kotlin.app.AppApplication
import com.personal.salary.kotlin.app.PagingTitleBarFragment
import com.personal.salary.kotlin.databinding.RosterListFragmentBinding
import com.personal.salary.kotlin.ktx.dp
import com.personal.salary.kotlin.ktx.loadStateListener
import com.personal.salary.kotlin.ktx.setFixOnClickListener
import com.personal.salary.kotlin.ktx.snapshotList
import com.personal.salary.kotlin.manager.EmployeeRosterManager
import com.personal.salary.kotlin.manager.EmployeeRosterStore
import com.personal.salary.kotlin.model.ArticleInfo
import com.personal.salary.kotlin.model.RefreshStatus
import com.personal.salary.kotlin.ui.activity.BrowserActivity
import com.personal.salary.kotlin.ui.activity.HomeActivity
import com.personal.salary.kotlin.ui.activity.ImagePreviewActivity
import com.personal.salary.kotlin.ui.activity.SearchActivity
import com.personal.salary.kotlin.ui.adapter.RosterAdapter
import com.personal.salary.kotlin.ui.adapter.delegate.AdapterDelegate
import com.personal.salary.kotlin.ui.dialog.ShareDialog
import com.personal.salary.kotlin.util.SUNNY_BEACH_ARTICLE_URL_PRE
import com.personal.salary.kotlin.util.SimpleLinearSpaceItemDecoration
import com.personal.salary.kotlin.util.UmengReportKey
import com.personal.salary.kotlin.viewmodel.SearchViewModel
import com.personal.salary.kotlin.viewmodel.roster.RosterViewModel
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import com.xiaomi.push.it
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * author : wangwx
 * github : https://github.com/GitHubWebb
 * time   : 2023/04/10
 * desc   : 花名册(人员列表) Fragment
 */
class RosterListFragment : PagingTitleBarFragment<HomeActivity>(), OnBack2TopListener {

    private val mBinding: RosterListFragmentBinding by viewBinding()
    private val mRosterViewModel by activityViewModels<RosterViewModel>()
    private val employeeRosterManager = EmployeeRosterManager.get()
    /** 实现Application作用域的共享ViewModel用于Activity和Fragment的相互通信 */
    private val mSearchViewModel by applicationViewModels<SearchViewModel>()

    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mRosterAdapter = RosterAdapter(mAdapterDelegate)
    private val loadStateListener =
        loadStateListener(mRosterAdapter) { mBinding.pagingRefreshLayout.finishRefresh() }

    override fun getPagingAdapter() = mRosterAdapter

    override fun getLayoutId(): Int = R.layout.roster_list_fragment

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
    }

    override suspend fun loadListData() {
        showLoading()
        // lifecycleScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.IO) {
            var rosterListGroupByDept = employeeRosterManager.getRosterListGroupByDept()
            rosterListGroupByDept?.let {
                mRosterAdapter.submitData(
                    PagingData.from(
                        rosterListGroupByDept
                    )
                )
            }

            // 在首页获取花名册数据时, 同步获取搜索页面的部门信息
            withContext(Dispatchers.IO) {
                Timber.d("mSearchViewModel: ${mSearchViewModel}")
                mSearchViewModel.getDeptNameAndRosterCount()
            }
        }

    }

    @SuppressLint("InflateParams")
    override fun initEvent() {
        super.initEvent()
        mBinding.apply {
            topLayout.setOnClickListener { }
            searchContainer.setFixOnClickListener {
                MobclickAgent.onEvent(context, UmengReportKey.HOME_SEARCH)
                SearchActivity.start(requireActivity(), it)
            }

            pagingRefreshLayout.setOnRefreshListener {
                lifecycleScope.launch(Dispatchers.Main) {
                    loadListData()
                    mRosterAdapter.refresh()
                    pagingRefreshLayout.finishRefresh()
                }
            }

            stickHeadContainer.setDataCallback(StickyHeadContainer.DataCallback { pos ->
                // mStickyPosition = pos
                val item: EmployeeRosterStore? = mRosterAdapter.snapshotList[pos]
                item?.let {
                    mBinding.itemRosterStickyHead.tvRosterStickyHeadName.setText(item.firstOrderDept)
                    mBinding.itemRosterStickyHead.tvCount.setText("${item.itemCount}人")
                    mBinding.itemRosterStickyHead.checkbox.setChecked(pos % 2 == 0)

                }
            })

            stickHeadContainer.setOnClickListener(View.OnClickListener {
                toast(
                    "点击了粘性头部：" + mBinding.itemRosterStickyHead.tvRosterStickyHeadName.getText(),
                )
            })

            val stickyItemDecoration =
                StickyItemDecoration(stickHeadContainer, StickerItemAdapter.TYPE_STICKY_HEAD)
            stickyItemDecoration.setOnStickyChangeListener(object : OnStickyChangeListener {
                override fun onScrollable(offset: Int) {
                    stickHeadContainer.scrollChild(offset)
                    stickHeadContainer.setVisibility(View.VISIBLE)
                }

                override fun onInVisible() {
                    stickHeadContainer.reset()
                    stickHeadContainer.setVisibility(View.INVISIBLE)
                }
            })

            pagingRecyclerView.addItemDecoration(stickyItemDecoration)
            pagingRecyclerView.addItemDecoration(
                SpaceItemDecoration(
                    pagingRecyclerView.getContext()
                )
            )

            // 需要在 View 销毁的时候移除 listener
            mRosterAdapter.addLoadStateListener(loadStateListener)
            mAdapterDelegate.setOnItemClickListener { _, position ->
                mRosterAdapter.snapshotList[position]?.let {
                    val url = "$SUNNY_BEACH_ARTICLE_URL_PRE${it.uId}"
                    BrowserActivity.start(requireContext(), url)
                }
            }
            mRosterAdapter.setOnMenuItemClickListener { view, item, _ ->
                when (view.id) {
                    R.id.ll_share -> shareArticle(item)
                    // R.id.ll_great -> articleLikes(item, position)
                }
            }
            mRosterAdapter.setOnNineGridClickListener { sources, index ->
                ImagePreviewActivity.start(requireContext(), sources.toMutableList(), index)
            }
        }
    }

    private fun shareArticle(item: EmployeeRosterStore) {
        val articleId = item.uId
        val content = UMWeb(SUNNY_BEACH_ARTICLE_URL_PRE + articleId)
        content.title = item.empName
        content.setThumb(UMImage(requireContext(), R.mipmap.launcher_ic))
        content.description = getString(R.string.app_name)
        // 分享
        ShareDialog.Builder(requireActivity()).setShareLink(content)
            .setListener(object : UmengShare.OnShareListener {
                override fun onSucceed(platform: Platform?) {
                    toast("分享成功")
                }

                override fun onError(platform: Platform?, t: Throwable) {
                    toast(t.message)
                }

                override fun onCancel(platform: Platform?) {
                    toast("分享取消")
                }
            }).show()
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRosterAdapter.removeLoadStateListener(loadStateListener)
    }

    override fun onBack2Top() {
        mBinding.pagingRecyclerView.scrollToPosition(0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = RosterListFragment()
    }
}