package com.personal.salary.kotlin.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dylanc.longan.applicationViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.hjq.bar.TitleBar
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.app.AppActivity
import com.personal.salary.kotlin.app.AppApplication
import com.personal.salary.kotlin.databinding.SearchActivityBinding
import com.personal.salary.kotlin.ktx.clearTooltipText
import com.personal.salary.kotlin.ktx.hideKeyboard
import com.personal.salary.kotlin.ktx.reduceDragSensitivity
import com.personal.salary.kotlin.ktx.textString
import com.personal.salary.kotlin.other.SearchType
import com.personal.salary.kotlin.ui.fragment.SearchListFragment
import com.personal.salary.kotlin.viewmodel.CookiesViewModel
import com.personal.salary.kotlin.viewmodel.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索界面
 */
class SearchActivity : AppActivity() {

    private val mBinding by viewBinding<SearchActivityBinding>()

    /** 实现Application作用域的共享ViewModel用于Activity和Fragment的相互通信 */
    private val mSearchViewModel by applicationViewModels<SearchViewModel>()
    private lateinit var mTabLayoutMediator: TabLayoutMediator

    override fun getLayoutId(): Int = R.layout.search_activity

    override fun initView() {
        with(mBinding) {
            showKeyboard(mBinding.searchView)
            viewPager2.apply {
                reduceDragSensitivity()
                Timber.d("mSearchViewModel: ${mSearchViewModel}")
                Timber.d("firstDeptAndRosterCountVOS: ${mSearchViewModel.firstDeptAndRosterCountVOS} ")
                adapter = object : FragmentStateAdapter(this@SearchActivity) {

                    private val typeList = mSearchViewModel.firstDeptAndRosterCountVOS
                    /*listOf(
                        SearchType.ALL,
                        SearchType.ARTICLE,
                        SearchType.QA,
                        SearchType.SHARE,
                    )*/

                    override fun getItemCount() = typeList.size

                    override fun createFragment(position: Int): Fragment =
                        SearchListFragment.newInstance(typeList[position])
                }
            }

            mTabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                tab.text =
                    buildString {
                        var firstDeptAndRosterCountVO =
                            mSearchViewModel.firstDeptAndRosterCountVOS[position]
                        append(firstDeptAndRosterCountVO.firstOrderDept)
                        append("\t")
                        append((firstDeptAndRosterCountVO.rosterCount))
                    }

                /*when (position) {
                        0 -> "全部"
                        1 -> "文章"
                        2 -> "问答"
                        3 -> "分享"
                        else -> error("Creating this instance is not supported.")
                    }*/
            }
            mTabLayoutMediator.attach()
            tabLayout.clearTooltipText()
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        val searchView = mBinding.searchView
        searchView.doOnTextChanged { newText, _, _, _ ->
            newText?.let { mSearchViewModel.setKeywords(it.toString()) }
        }
        searchView.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    doSearch()
                    hideKeyboard()
                }
            }
            true
        }
    }

    override fun onLeftClick(titleBar: TitleBar) {
        hideKeyboard()
        super.onLeftClick(titleBar)
    }

    override fun onRightClick(titleBar: TitleBar) {
        doSearch()
    }

    private fun doSearch() {
        with(mBinding.searchView.textString) {
            takeUnless { isEmpty() }?.let { mSearchViewModel.setKeywords(this) }
                ?: toast("关键字不能为空哦~")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.searchView.clearFocus()
        mTabLayoutMediator.detach()
    }

    override fun isStatusBarDarkFont() = true

    companion object {

        private const val SHARED_ELEMENT_NAME = "searchView"

        fun start(activity: Activity, sharedElement: View) {
            Intent(activity, SearchActivity::class.java).apply {
                val activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    sharedElement,
                    SHARED_ELEMENT_NAME
                )
                ActivityCompat.startActivity(activity, this, activityOptionsCompat.toBundle())
            }
        }
    }
}