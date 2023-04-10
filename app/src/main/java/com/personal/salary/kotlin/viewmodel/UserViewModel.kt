package com.personal.salary.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.blankj.utilcode.util.RegexUtils
import com.personal.salary.kotlin.http.network.Repository
import com.personal.salary.kotlin.model.*
import com.personal.salary.kotlin.util.I_LOVE_ANDROID_SITE_BASE_URL
import com.personal.salary.kotlin.util.SUNNY_BEACH_SITE_BASE_URL
import com.personal.salary.kotlin.util.StringUtil
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.io.File

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 用户 ViewModel
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val phoneLiveData = MutableLiveData<String>()

    val userAvatarLiveData = Transformations.switchMap(phoneLiveData) { account ->
        Repository.queryUserAvatar(account)
    }

    /**
     * We only support http and https protocols.
     */
    fun checkScheme(scheme: String) = scheme == "http" || scheme == "https"

    fun checkAuthority(authority: String): Boolean {
        val sobSiteTopDomain = StringUtil.getTopDomain(SUNNY_BEACH_SITE_BASE_URL)
        val loveSiteTopDomain = StringUtil.getTopDomain(I_LOVE_ANDROID_SITE_BASE_URL)

        Timber.d("checkAuthority：===> authority is $authority")
        Timber.d("checkAuthority：===> sobSiteTopDomain is $sobSiteTopDomain")
        Timber.d("checkAuthority：===> loveSiteTopDomain is $loveSiteTopDomain")

        fun String.delete3W() = replace("www.", "")
        val sobAuthority = authority.delete3W() == sobSiteTopDomain
        val loveAuthority = authority.delete3W() == loveSiteTopDomain
        return sobAuthority || loveAuthority
    }

    /**
     * Sob site userId is long type, we need check.
     */
    fun checkUserId(userId: String) = userId.isNotBlank() && userId.toLongOrNull() != null

    /**
     * 修改用户头像
     */
    fun modifyAvatar(avatarUrl: String) = Repository.modifyAvatar(avatarUrl)

    /**
     * 根据分类 id 上传用户中心图片
     */
    suspend fun uploadUserCenterImageByCategoryId(imageFile: File, categoryId: String) =
        Repository.uploadUserCenterImageByCategoryId(imageFile, categoryId)

    /**
     * 发送邮件
     */
    fun sendEmail(email: String) = Repository.sendEmail(email)

    /**
     * 找回密码（通过短信找回）
     */
    fun modifyPasswordBySms(smsCode: String, user: User) = Repository.modifyPasswordBySms(smsCode, user)

    /**
     * 修改密码（通过旧密码修改）
     */
    fun modifyPasswordByOldPwd(modifyPwd: ModifyPwd) = Repository.modifyPasswordByOldPwd(modifyPwd)

    /**
     * 检查手机验证码是否正确
     */
    fun checkSmsCode(phoneNumber: String, smsCode: String) = Repository.checkSmsCode(phoneNumber, smsCode)

    /**
     * 获取找回密码的手机验证码（找回密码）
     */
    fun sendForgetSmsVerifyCode(smsInfo: SmsInfo) = Repository.sendForgetSmsVerifyCode(smsInfo)

    /**
     * 注册账号
     */
    fun registerAccount(smsCode: String, user: User) = Repository.registerAccount(smsCode, user)

    /**
     * 获取注册的手机验证码（注册）
     */
    fun sendRegisterSmsVerifyCode(smsInfo: SmsInfo) = Repository.sendRegisterSmsVerifyCode(smsInfo)

    /**
     * 自己与目标用户的关注状态
     *
     * 0：表示没有关注对方，可以显示为：关注
     * 1：表示对方关注自己，可以显示为：回粉
     * 2：表示已经关注对方，可以显示为：已关注
     * 3：表示相互关注，可以显示为：相互关注
     */
    fun followState(userId: String) = Repository.followState(userId)

    /**
     * 获取用户信息
     */
    fun getUserInfo(userId: String) = Repository.getUserInfo(userId)

    /**
     * 通过账号（手机号）获取用户头像
     */
    fun queryUserAvatar(account: String) {
        phoneLiveData.value = account
    }

    /**
     * 退出登录
     */
    fun logout() = Repository.logout()

    /**
     * 用户账号登录
     */
    fun login(userAccount: String, password: String, captcha: String) = when {
        userAccount.isUserAccountValid().not() -> checkErrorResult("手机号码格式错误")
        password.isPasswordValid().not() -> checkErrorResult("密码长度不能低于5位")
        captcha.isVerifyCodeValid().not() -> checkErrorResult("验证码不能为空")
        else -> Repository.login(userAccount, password, captcha)
    }

    /**
     * 检查错误结果
     */
    private fun checkErrorResult(msg: String) = MutableLiveData<Result<UserBasicInfo>>(Result.failure(IllegalArgumentException(msg)))

    /**
     * 手机号码格式检查
     */
    private fun String.isUserAccountValid(): Boolean = RegexUtils.isMobileExact(this)

    /**
     * 账号密码长度检查
     */
    private fun String.isPasswordValid(): Boolean = length > 5

    /**
     * 验证码检查
     */
    private fun String.isVerifyCodeValid(): Boolean = isNotEmpty()
}