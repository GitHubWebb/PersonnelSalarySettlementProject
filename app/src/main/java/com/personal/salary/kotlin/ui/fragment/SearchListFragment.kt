package com.personal.salary.kotlin.ui.fragment

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dylanc.longan.applicationViewModels
import com.dylanc.longan.safeArguments
import com.dylanc.longan.viewLifecycleScope
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.action.OnBack2TopListener
import com.personal.salary.kotlin.action.StatusAction
import com.personal.salary.kotlin.app.AppActivity
import com.personal.salary.kotlin.app.TitleBarFragment
import com.personal.salary.kotlin.databinding.SearchListFragmentBinding
import com.personal.salary.kotlin.ktx.*
import com.personal.salary.kotlin.manager.FirstDeptAndRosterCountVO
import com.personal.salary.kotlin.ui.adapter.SearchResultListAdapter
import com.personal.salary.kotlin.ui.adapter.delegate.AdapterDelegate
import com.personal.salary.kotlin.util.SimpleLinearSpaceItemDecoration
import com.personal.salary.kotlin.viewmodel.SearchViewModel
import com.personal.salary.kotlin.widget.StatusLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * author : A Lonely Cat, wangwx
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索列表 Fragment
 */
class SearchListFragment : TitleBarFragment<AppActivity>(), StatusAction, OnBack2TopListener {

    private val mBinding by viewBinding<SearchListFragmentBinding>()
    private val mSearchViewModel by applicationViewModels<SearchViewModel>()
    private val mAdapterDelegate = AdapterDelegate()
    private val mSearchResultListAdapter = SearchResultListAdapter(mAdapterDelegate)
    private val loadStateListener =
        loadStateListener(mSearchResultListAdapter) { mBinding.refreshLayout.finishRefresh() }
    private val searchTypeJson by safeArguments<String>(SEARCH_TYPE)
    private val searchType by lazy { fromJson<FirstDeptAndRosterCountVO>(searchTypeJson) }

    override fun getLayoutId(): Int = R.layout.search_list_fragment

    override fun initView() {
        mBinding.rvSearchList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mSearchResultListAdapter
            addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
        }
    }

    override fun initData() {
        viewLifecycleScope.launchWhenCreated{
            loadSearchResultList()
        }

    }

    override fun initEvent() {
        mBinding.refreshLayout.setOnRefreshListener { mSearchResultListAdapter.refresh() }
        // 需要在 View 销毁的时候移除 listener
        mSearchResultListAdapter.addLoadStateListener(loadStateListener)
        mAdapterDelegate.setOnItemClickListener { _, position ->
            // 跳转到搜索详情界面
            mSearchResultListAdapter.snapshotList[position]?.let {
                /*
                val url = when (SearchType.valueOfType(it.empName .type)) {
                    SearchType.ARTICLE -> "$SUNNY_BEACH_ARTICLE_URL_PRE${it.id}"
                    SearchType.QA -> "$SUNNY_BEACH_QA_URL_PRE${it.id}"
                    SearchType.SHARE -> "$SUNNY_BEACH_SHARE_URL_PRE${it.id}"
                    else -> null
                }
                url?.let { BrowserActivity.start(requireContext(), url) }
                */
            }
        }

        showEmpty{
            loadSearchResultList()
        }
    }

    override fun initObserver() {
        mSearchViewModel.keywordsLiveData.observe(viewLifecycleOwner) {
            loadSearchResultList(it)
        }
    }

    private fun loadSearchResultList(keywords: String = "") {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mSearchViewModel.searchByKeywords(keyword = keywords, searchType = searchType.firstOrderDept)
                    .collectLatest {
                        mSearchResultListAdapter.submitData(it)
                    }
            }
        }
    }

    override fun getStatusLayout(): StatusLayout = mBinding.hlSearchHint

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mSearchResultListAdapter.removeLoadStateListener(loadStateListener)
    }

    override fun onBack2Top() {
        mBinding.rvSearchList.scrollToPosition(0)
    }

    companion object {

        private const val SEARCH_TYPE = "search_type"

        fun newInstance(deptAndRosterCountVO: FirstDeptAndRosterCountVO): SearchListFragment {
            val fragment = SearchListFragment()
            fragment.arguments = Bundle().apply {
                putString(SEARCH_TYPE, deptAndRosterCountVO.toJson())
            }
            return fragment
        }
    }
}