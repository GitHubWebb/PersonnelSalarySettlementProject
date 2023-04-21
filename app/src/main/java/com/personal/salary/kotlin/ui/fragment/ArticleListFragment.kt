package com.personal.salary.kotlin.ui.fragment

import android.annotation.SuppressLint
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.action.OnBack2TopListener
import com.personal.salary.kotlin.app.PagingTitleBarFragment
import com.personal.salary.kotlin.databinding.ArticleListFragmentBinding
import com.personal.salary.kotlin.ktx.dp
import com.personal.salary.kotlin.ktx.setFixOnClickListener
import com.personal.salary.kotlin.ktx.snapshotList
import com.personal.salary.kotlin.model.ArticleInfo
import com.personal.salary.kotlin.model.RefreshStatus
import com.personal.salary.kotlin.ui.activity.BrowserActivity
import com.personal.salary.kotlin.ui.activity.HomeActivity
import com.personal.salary.kotlin.ui.activity.ImagePreviewActivity
import com.personal.salary.kotlin.ui.activity.SearchActivity
import com.personal.salary.kotlin.ui.adapter.ArticleAdapter
import com.personal.salary.kotlin.ui.adapter.delegate.AdapterDelegate
import com.personal.salary.kotlin.ui.dialog.ShareDialog
import com.personal.salary.kotlin.util.SUNNY_BEACH_ARTICLE_URL_PRE
import com.personal.salary.kotlin.util.SimpleLinearSpaceItemDecoration
import com.personal.salary.kotlin.util.UmengReportKey
import com.personal.salary.kotlin.viewmodel.ArticleViewModel
import com.umeng.analytics.MobclickAgent
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import com.xiaomi.push.it
import kotlinx.coroutines.flow.collectLatest

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/20
 * desc   : 首页-文章 Fragment
 */
class ArticleListFragment : PagingTitleBarFragment<HomeActivity>(), OnBack2TopListener {

    private val mBinding: ArticleListFragmentBinding by viewBinding()
    private val mArticleViewModel by activityViewModels<ArticleViewModel>()
    private val mRefreshStatus = RefreshStatus()
    private val mAdapterDelegate = AdapterDelegate()
    private val mArticleAdapter = ArticleAdapter(mAdapterDelegate)

    override fun getPagingAdapter() = mArticleAdapter

    override fun getLayoutId(): Int = R.layout.article_list_fragment

    override fun initView() {
        super.initView()
        mBinding.pagingRecyclerView.addItemDecoration(SimpleLinearSpaceItemDecoration(4.dp))
    }

    override suspend fun loadListData() {
        mArticleViewModel.getArticleListByCategoryId("recommend").collectLatest {
            mArticleAdapter.submitData(it)
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
        }
        mAdapterDelegate.setOnItemClickListener { _, position ->
            mArticleAdapter.snapshotList[position]?.let {
                val url = "$SUNNY_BEACH_ARTICLE_URL_PRE${it.id}"
                BrowserActivity.start(requireContext(), url)
            }
        }
        mArticleAdapter.setOnMenuItemClickListener { view, item, _ ->
            when (view.id) {
                R.id.ll_share -> shareArticle(item)
                // R.id.ll_great -> articleLikes(item, position)
            }
        }
        mArticleAdapter.setOnNineGridClickListener { sources, index ->
            ImagePreviewActivity.start(requireContext(), sources.toMutableList(), index)
        }
    }

    private fun shareArticle(item: ArticleInfo.ArticleItem) {
        val articleId = item.id
        val content = UMWeb(SUNNY_BEACH_ARTICLE_URL_PRE + articleId)
        content.title = item.title
        content.setThumb(UMImage(requireContext(), R.mipmap.launcher_ic))
        content.description = getString(R.string.app_name)
        // 分享
        ShareDialog.Builder(requireActivity())
            .setShareLink(content)
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
            })
            .show()
    }

    override fun showLoading(id: Int) {
        takeIf { mRefreshStatus.isFirstRefresh }?.let { super.showLoading(id) }
        mRefreshStatus.isFirstRefresh = false
    }

    override fun isStatusBarEnabled(): Boolean {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled()
    }

    override fun onBack2Top() {
        mBinding.pagingRecyclerView.scrollToPosition(0)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArticleListFragment()
    }
}