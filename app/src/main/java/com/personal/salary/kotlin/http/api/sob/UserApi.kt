package com.personal.salary.kotlin.http.api.sob

import com.personal.salary.kotlin.http.ServiceCreator
import com.personal.salary.kotlin.http.annotation.baseurl.SobBaseUrl

import com.personal.salary.kotlin.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

@SobBaseUrl
interface UserApi {

    /**
     * 查询积分规则
     * item 参数格式：请求方法:请求路径(不含参数)，例：PUT:/uc/ucenter/cover
     *
     * 返回字段说明：
     * label：表示的人看的标签，这是个什么项
     * sob：表示积分数量，正数为加，负数为减
     * enable：1表示当前规则开启，0表示不执行当前规则
     * tax：为税费
     * mark：为备注说明
     */
    @GET("ast/sob-rule")
    suspend fun queryIntegralRule(@Query("item") item: String): ApiResponse<IntegralRule>

    /**
     * 修改用户头像
     */
    @PUT("uc/ucenter/user-info/avatar")
    suspend fun modifyAvatar(@Query("avatar") avatarUrl: String): ApiResponse<Any>

    /**
     * 根据分类 id 上传图片
     */
    @Multipart
    @POST("ct/ucenter/image")
    suspend fun uploadUserCenterImageByCategoryId(
        @Part part: MultipartBody.Part,
        @Query("categoryId") categoryId: String
    ): ApiResponse<String>

    /**
     * 发送邮箱验证码
     */
    @GET("uc/ucenter/send-email/{email}")
    suspend fun sendEmail(@Path("email") email: String): ApiResponse<Any>

    /**
     * 找回密码（通过短信找回）
     */
    @PUT("uc/user/forget/{smsCode}")
    suspend fun modifyPasswordBySms(
        @Path("smsCode") smsCode: String,
        @Body user: User
    ): ApiResponse<Any>

    /**
     * 修改密码（通过旧密码修改）
     */
    @PUT("uc/user/modify-pwd")
    suspend fun modifyPasswordByOldPwd(@Body modifyPwd: ModifyPwd): ApiResponse<Any>

    /**
     * 检查手机验证码是否正确
     */
    @GET("uc/ut/check-sms-code/{phoneNumber}/{smsCode}")
    suspend fun checkSmsCode(
        @Path("phoneNumber") phoneNumber: String,
        @Path("smsCode") smsCode: String
    ): ApiResponse<Any>

    /**
     * 获取找回密码的手机验证码（找回密码）
     */
    @POST("uc/ut/forget/send-sms")
    suspend fun sendForgetSmsVerifyCode(@Body smsInfo: SmsInfo): ApiResponse<Any>

    /**
     * 注册账号
     */
    @POST("uc/user/register/{smsCode}")
    suspend fun registerAccount(
        @Path("smsCode") smsCode: String,
        @Body user: User
    ): ApiResponse<Any>

    /**
     * 获取注册的手机验证码（注册）
     */
    @POST("uc/ut/join/send-sms")
    suspend fun sendRegisterSmsVerifyCode(@Body smsInfo: SmsInfo): ApiResponse<Any>

    /**
     * 根据手机号查询用户头像
     */
    @GET("uc/user/avatar/{phoneNum}")
    suspend fun queryUserAvatar(@Path("phoneNum") phoneNum: String): ApiResponse<String>

    /**
     * 退出登录
     */
    @GET("uc/user/logout")
    suspend fun logout(): ApiResponse<Any>

    /**
     * 登录账号
     */
    @POST("uc/user/login/{captcha}")
    suspend fun login(@Path("captcha") captcha: String, @Body user: User): ApiResponse<Any?>

    /**
     * 解析当前用户的 Token
     * Token 的有效期为 7天
     */
    @GET("uc/user/checkToken")
    suspend fun checkToken(): ApiResponse<UserBasicInfo>

    /**
     * 获取指定用户的信息
     */
    @GET("uc/user-info/{userId}")
    suspend fun getUserInfo(@Path("userId") userId: String): ApiResponse<UserInfo>

    /**
     * 自己与目标用户的关注状态
     *
     * 0：表示没有关注对方，可以显示为：关注
     * 1：表示对方关注自己，可以显示为：回粉
     * 2：表示已经关注对方，可以显示为：已关注
     * 3：表示相互关注，可以显示为：相互关注
     */
    @GET("uc/fans/state/{userId}")
    suspend fun followState(@Path("userId") userId: String): ApiResponse<Int>

    companion object : UserApi by ServiceCreator.create()
}