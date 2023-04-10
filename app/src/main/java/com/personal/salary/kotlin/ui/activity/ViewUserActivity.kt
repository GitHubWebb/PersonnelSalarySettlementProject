package com.personal.salary.kotlin.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.dylanc.longan.lifecycleOwner
import com.hjq.bar.TitleBar
import com.hjq.umeng.Platform
import com.hjq.umeng.UmengShare
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.aop.Log
import com.personal.salary.kotlin.app.AppActivity
import com.personal.salary.kotlin.databinding.ViewUserActivityBinding
import com.personal.salary.kotlin.ktx.*
import com.personal.salary.kotlin.manager.UserManager
import com.personal.salary.kotlin.model.UserInfo
import com.personal.salary.kotlin.other.FriendsStatus
import com.personal.salary.kotlin.other.IntentKey
import com.personal.salary.kotlin.ui.dialog.ShareDialog
import com.personal.salary.kotlin.ui.fragment.UserMediaFragment
import com.personal.salary.kotlin.util.SUNNY_BEACH_VIEW_USER_URL_PRE
import com.personal.salary.kotlin.viewmodel.UserViewModel
import com.umeng.socialize.media.UMWeb
import com.xiaomi.push.it
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.text.orEmpty

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/29
 * desc   : 查看用户信息界面
 */
@AndroidEntryPoint
class ViewUserActivity : AppActivity() {

    private val mBinding by viewBinding<ViewUserActivityBinding>()
    private val mUserViewModel by viewModels<UserViewModel>()
    private var mFriendsStatus = FriendsStatus.FOLLOW
    private var mUserInfo: UserInfo? = null

    override fun getLayoutId(): Int = R.layout.view_user_activity

    override fun initView() {
        val userId = getUserId()
        setUpUserInfo(userId)
        val fragment = supportFragmentManager.findFragmentById(R.id.user_media_fragment_container)
        // 避免 Activity 重建后，重复 add Fragment 到容器里
        takeIf { fragment == null }?.let {
            val ft = supportFragmentManager.beginTransaction()
            ft.add(R.id.user_media_fragment_container, UserMediaFragment.newInstance(userId))
            ft.commitAllowingStateLoss()
        }
    }

    private fun getUserId(): String {
        val uri = intent?.data
        val scheme = uri?.scheme.orEmpty()
        val authority = uri?.authority.orEmpty()
        val lastPathSegment = uri?.lastPathSegment.orEmpty()
        val userId = intent.getStringExtra(IntentKey.ID).orEmpty()

        Timber.d("showResult：===> scheme is $scheme authority is $authority userId is $userId lastPathSegment is $lastPathSegment")

        return when {
            mUserViewModel.checkScheme(scheme).not() -> userId
            mUserViewModel.checkAuthority(authority).not() -> userId
            mUserViewModel.checkUserId(lastPathSegment).not() -> userId
            else -> lastPathSegment
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUserInfo(userId: String) {
        with(mBinding) {
            mUserViewModel.getUserInfo(userId).observe(lifecycleOwner) {
                val userInfo = it.getOrNull() ?: return@observe
                titleBar.rightTitle = "分享"
                mUserInfo = userInfo
                ivAvatar.setFixOnClickListener { ImagePreviewActivity.start(context, userInfo.avatar) }
                ivAvatar.loadAvatar(userInfo.vip, userInfo.avatar)
                tvNickName.text = userInfo.nickname
                tvNickName.setTextColor(UserManager.getNickNameColor(userInfo.vip))
                val job = userInfo.position.ifNullOrEmpty { "滩友" }
                val company = userInfo.company.ifNullOrEmpty { "无业" }
                tvDesc.text = "${job}@${company}"
            }
            checkFollowState(userId)

        }
    }

    private fun checkFollowState(userId: String) {
        with(mBinding) {
            tvFollow.setRoundRectBg(mFriendsStatus.color, 3.dp)
            val currUserId = UserManager.loadUserBasicInfo()?.id.orEmpty()
            if (userId == currUserId) {
                tvFollow.text = "编辑"
                tvFollow.setTextColor(Color.parseColor("#1D7DFA"))
                tvFollow.background = ContextCompat.getDrawable(context, R.drawable.edit_ic)
                return
            }
            mUserViewModel.followState(userId).observe(lifecycleOwner) {
                val state = it.getOrNull() ?: return@observe
                mFriendsStatus = FriendsStatus.valueOfCode(state)
                tvFollow.text = mFriendsStatus.desc
                tvFollow.setRoundRectBg(mFriendsStatus.color, 3.dp)
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        val userId = getUserId()
        with(mBinding) {
        }
    }

    override fun onRightClick(titleBar: TitleBar) {
        val userId = mUserInfo?.userId ?: return
        val content = UMWeb(SUNNY_BEACH_VIEW_USER_URL_PRE + userId)
        // 分享
        ShareDialog.Builder(this)
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

    companion object {

        @JvmStatic
        @Log
        fun start(context: Context, userId: String) {
            val intent = Intent(context, ViewUserActivity::class.java)
            intent.putExtra(IntentKey.ID, userId)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}