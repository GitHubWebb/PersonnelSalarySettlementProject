package com.personal.salary.kotlin.http.network

import com.personal.salary.kotlin.http.api.sob.UserApi
import com.personal.salary.kotlin.model.ModifyPwd
import com.personal.salary.kotlin.model.SmsInfo
import com.personal.salary.kotlin.model.User
import okhttp3.MultipartBody

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/23
 * desc   : 用户信息获取
 */
object UserNetwork {

    suspend fun queryIntegralRule(item: String) = UserApi.queryIntegralRule(item)

    suspend fun modifyAvatar(avatarUrl: String) = UserApi.modifyAvatar(avatarUrl)

    suspend fun uploadUserCenterImageByCategoryId(part: MultipartBody.Part, categoryId: String) =
        UserApi.uploadUserCenterImageByCategoryId(part, categoryId)

    suspend fun sendEmail(email: String) = UserApi.sendEmail(email)

    suspend fun modifyPasswordBySms(smsCode: String, user: User) = UserApi.modifyPasswordBySms(smsCode, user)

    suspend fun modifyPasswordByOldPwd(modifyPwd: ModifyPwd) = UserApi.modifyPasswordByOldPwd(modifyPwd)

    suspend fun checkSmsCode(phoneNumber: String, smsCode: String) = UserApi.checkSmsCode(phoneNumber, smsCode)

    suspend fun sendForgetSmsVerifyCode(smsInfo: SmsInfo) = UserApi.sendForgetSmsVerifyCode(smsInfo)

    suspend fun registerAccount(smsCode: String, user: User) = UserApi.registerAccount(smsCode, user)

    suspend fun sendRegisterSmsVerifyCode(smsInfo: SmsInfo) = UserApi.sendRegisterSmsVerifyCode(smsInfo)

    suspend fun queryUserAvatar(account: String) = UserApi.queryUserAvatar(account)

    suspend fun logout() = UserApi.logout()

    suspend fun checkToken() = UserApi.checkToken()

    suspend fun login(captcha: String, user: User) = UserApi.login(captcha, user)

    suspend fun getUserInfo(userId: String) = UserApi.getUserInfo(userId)

    suspend fun followState(userId: String) = UserApi.followState(userId)

}