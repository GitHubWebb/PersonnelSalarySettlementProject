package com.personal.salary.kotlin.ui.fragment

import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.hjq.base.FragmentPagerAdapter
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.app.AppActivity
import com.personal.salary.kotlin.app.AppFragment
import com.personal.salary.kotlin.databinding.UserMediaFragmentBinding
import com.personal.salary.kotlin.ktx.clearTooltipText
import com.personal.salary.kotlin.other.FollowType
import com.personal.salary.kotlin.other.IntentKey

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 用户信息 Fragment
 */
class UserMediaFragment : AppFragment<AppActivity>() {

    private val mBinding by viewBinding<UserMediaFragmentBinding>()
    private val mPagerAdapter by lazy { FragmentPagerAdapter<AppFragment<AppActivity>>(this) }

    var mUserId = ""

    override fun getLayoutId(): Int = R.layout.user_media_fragment

    override fun initView() {
        mBinding.apply {
            viewPager.apply {
                adapter = mPagerAdapter
                mPagerAdapter.startUpdate(this)
            }
            tabLayout.setupWithViewPager(viewPager)
        }
    }

    override fun initData() {
        mUserId = arguments?.getString(IntentKey.ID, "").orEmpty()
        mPagerAdapter.apply {
            /*addFragment(UserFishListFragment.newInstance(mUserId), "动态")
            addFragment(UserArticleListFragment.newInstance(mUserId), "文章")
            addFragment(UserQaListFragment.newInstance(mUserId), "回答")
            addFragment(UserFollowOrFansListFragment.newInstance(mUserId, FollowType.FOLLOW), "关注")
            addFragment(UserFollowOrFansListFragment.newInstance(mUserId, FollowType.FANS), "粉丝")
            addFragment(UserShareListFragment.newInstance(mUserId), "分享")*/
        }
        val tabLayout = mBinding.tabLayout
        tabLayout.clearTooltipText()
    }

    companion object {

        @JvmStatic
        fun newInstance(userId: String): UserMediaFragment {
            val fragment = UserMediaFragment()
            val args = Bundle().apply {
                putString(IntentKey.ID, userId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}