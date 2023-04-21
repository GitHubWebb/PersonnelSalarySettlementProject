package com.personal.salary.kotlin.http.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.hjq.http.model.HttpMethod
import com.personal.salary.kotlin.db.dao.PlaceDao
import com.personal.salary.kotlin.execption.NotLoginException
import com.personal.salary.kotlin.execption.ServiceException
import com.personal.salary.kotlin.ktx.getOrNull
import com.personal.salary.kotlin.ktx.lowercaseMd5
import com.personal.salary.kotlin.ktx.toErrorResult
import com.personal.salary.kotlin.ktx.toJson
import com.personal.salary.kotlin.manager.UserManager
import com.personal.salary.kotlin.model.*
import com.personal.salary.kotlin.model.wallpaper.WallpaperBean
import com.personal.salary.kotlin.model.weather.Place
import com.personal.salary.kotlin.model.weather.Weather
import com.xiaomi.push.it
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2021/09/07
 *    desc   : 存储库
 */
object Repository {

    private const val NOT_LOGIN_CODE = 11126

    private val cachePhotoIdList = arrayListOf<WallpaperBean.Res.Vertical>()

    fun queryIntegralRule(method: HttpMethod, path: String) = launchAndGetData { UserNetwork.queryIntegralRule("$method:$path") }

    fun getArticleDetailById(articleId: String) = launchAndGetData { ArticleNetwork.getArticleDetailById(articleId) }

    fun articleLikes(articleId: String) = launchAndGetData { ArticleNetwork.articleLikes(articleId) }

    fun modifyAvatar(avatarUrl: String) = liveData(build = { UserNetwork.modifyAvatar(avatarUrl) }) {
        when {
            it.isSuccess() -> Result.success(true)
            else -> it.toErrorResult()
        }
    }

    suspend fun uploadUserCenterImageByCategoryId(imageFile: File, categoryId: String) = try {
        val fileName = imageFile.name
        Timber.d("===> fileName is $fileName")
        val requestBody = RequestBody.create("image/png".toMediaType(), imageFile)
        val part = MultipartBody.Part.createFormData("image", fileName, requestBody)
        val result = UserNetwork.uploadUserCenterImageByCategoryId(part, categoryId)
        Timber.d("result is ${result.toJson()}")
        if (result.isSuccess()) Result.success(result.getData()) else result.toErrorResult()
    } catch (t: Throwable) {
        t.printStackTrace()
        Result.failure(t)
    }

    fun sendEmail(email: String) = launchAndGetMsg { UserNetwork.sendEmail(email) }

    fun modifyPasswordBySms(smsCode: String, user: User) = launchAndGetMsg { UserNetwork.modifyPasswordBySms(smsCode, user) }

    fun modifyPasswordByOldPwd(modifyPwd: ModifyPwd) = launchAndGetMsg { UserNetwork.modifyPasswordByOldPwd(modifyPwd) }

    fun checkSmsCode(phoneNumber: String, smsCode: String) = launchAndGetMsg { UserNetwork.checkSmsCode(phoneNumber, smsCode) }

    fun sendForgetSmsVerifyCode(smsInfo: SmsInfo) = launchAndGetMsg { UserNetwork.sendForgetSmsVerifyCode(smsInfo) }

    fun registerAccount(smsCode: String, user: User) = launchAndGetMsg { UserNetwork.registerAccount(smsCode, user) }

    fun sendRegisterSmsVerifyCode(smsInfo: SmsInfo) = launchAndGetMsg { UserNetwork.sendRegisterSmsVerifyCode(smsInfo) }

    private inline fun <reified T> IApiResponse<T>.toAllowanceResult(): Result<Boolean> = when (getCode()) {
        // 未领取VIP津贴
        11129 -> Result.success(false)
        // 已经领取了VIP津贴
        11128 -> Result.success(true)
        else -> toErrorResult()
    }

    fun followState(userId: String) = launchAndGetData { UserNetwork.followState(userId) }

    fun getUserInfo(userId: String) = launchAndGetData { UserNetwork.getUserInfo(userId) }

    fun logout() = launchAndGetMsg { UserNetwork.logout() }

    suspend fun checkToken() = try {
        val result = UserNetwork.checkToken()
        val userBasicInfo = result.getOrNull() ?: throw ServiceException()
        UserManager.saveUserBasicInfo(userBasicInfo)
        UserManager.setupAutoLogin(true)
        userBasicInfo
    } catch (t: Throwable) {
        t.printStackTrace()
        UserManager.saveUserBasicInfo(null)
        UserManager.setupAutoLogin(false)
        null
    }

    fun login(userAccount: String, password: String, captcha: String) = launchAndGetData {
        val user = User(userAccount, password.lowercaseMd5)
        val loginResult = UserNetwork.login(captcha, user)
        val checkTokenResult = UserNetwork.checkToken()
        val loginCode = loginResult.getCode()
        val checkTokenCode = checkTokenResult.getCode()
        val loginSuccess = loginResult.isSuccess()
        val checkSuccess = checkTokenResult.isSuccess()
        val loginMsg = loginResult.getMessage()
        val checkTokenMsg = checkTokenResult.getMessage()
        when {
            loginSuccess && checkSuccess -> ApiResponse(loginCode, true, loginMsg, checkTokenResult.getData())
            loginSuccess.not() -> ApiResponse(loginCode, false, loginResult.getMessage(), null)
            checkSuccess.not() -> ApiResponse(checkTokenCode, false, checkTokenMsg, null)
            else -> ApiResponse(loginCode, false, loginMsg, null)
        }
    }

    fun queryUserAvatar(account: String) = launchAndGetData { UserNetwork.queryUserAvatar(account) }

    fun checkAppUpdate() = liveData(build = { AppNetwork.checkAppUpdate() }) {
        Timber.d("checkAppUpdate：===> result is ${it.toJson()}")

        when {
            it.isSuccess() -> Result.success(it.getData())
            else -> it.toErrorResult()
        }
    }

    fun getMourningCalendar(): LiveData<Result<List<MourningCalendar>>> = launchAndGetData { AppNetwork.getMourningCalendar() }

    fun setPhotoIdList(photoIdList: List<WallpaperBean.Res.Vertical>) {
        if (photoIdList !== cachePhotoIdList) {
            cachePhotoIdList.clear()
            if (photoIdList.isEmpty().not()) {
                cachePhotoIdList.addAll(photoIdList)
            }
        } else {
            if (photoIdList.isEmpty().not()) {
                val newList = photoIdList.toList()
                cachePhotoIdList.clear()
                cachePhotoIdList.addAll(newList)
            } else {
                cachePhotoIdList.clear()
            }
        }
    }

    fun getPhotoList() = cachePhotoIdList.toList()

    fun getFishCommendListById(momentId: String, page: Int) = launchAndGetData { FishNetwork.getFishCommendListById(momentId, page) }

    suspend fun uploadFishImage(imageFile: File): Result<String> = try {
        val fileName = imageFile.name
        Timber.d("===> fileName is $fileName")
        val requestBody = RequestBody.create("image/png".toMediaType(), imageFile)
        val part = MultipartBody.Part.createFormData("image", fileName, requestBody)
        val result = FishNetwork.uploadFishImage(part)
        Timber.d("result is ${result.toJson()}")
        val imageUrl = result.getData()
        Timber.d("uploadFishImage：===> file name is $fileName imageUrl is $imageUrl")
        if (result.isSuccess() && imageUrl != null) Result.success(imageUrl) else result.toErrorResult()
    } catch (t: Throwable) {
        t.printStackTrace()
        Result.failure(t)
    }

    fun putFish(moment: Map<String, Any?>) = launchAndGetData { FishNetwork.putFish(moment) }

    fun loadFishDetailById(momentId: String) = launchAndGetData { FishNetwork.loadFishDetailById(momentId) }

    fun loadWallpaperBannerList() = liveData(build = { PhotoNetwork.loadWallpaperBannerList() }) {
        if ("0" == it.errno) Result.success(it.data)
        else Result.failure(ServiceException(it.errmsg))
    }

    fun postComment(momentComment: Map<String, Any?>, isReply: Boolean) = launchAndGetData {
        if (isReply) FishNetwork.replyComment(momentComment)
        else FishNetwork.postComment(momentComment)
    }

    fun dynamicLikes(momentId: String) = launchAndGetMsg { FishNetwork.dynamicLikes(momentId) }

    fun loadTopicList() = launchAndGetData { FishNetwork.loadTopicList() }

    fun searchPlaces(query: String) = liveData(build = { WeatherNetwork.searchPlace(query) }) { placeResponse ->
        Timber.d("searchPlaces：===> query status is ${placeResponse.status}|${placeResponse.places[0].name}")
        if (placeResponse.status == "ok") Result.success(placeResponse.places)
        else Result.failure(RuntimeException("response status is ${placeResponse.status}"))
    }

    /**
     * 刷新天气
     */
    fun refreshWeather(lng: String, lat: String): LiveData<Result<Weather>> = liveData(build = {
        Timber.d("refreshWeather：===> lng is $lng lat is $lat")
        Pair(withContext(Dispatchers.IO) { WeatherNetwork.getRealtimeWeather(lng, lat) },
            withContext(Dispatchers.IO) { WeatherNetwork.getDailyWeather(lng, lat) })
    }) { (realtimeResponse, dailyResponse) ->
        if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
            Result.success(Weather(realtimeResponse.result.realtime, dailyResponse.result.daily))
        } else {
            val errorMsg = """
                    "realtimeWeather status is ${realtimeResponse.status} 
                    dailyWeather status is ${dailyResponse.status}"
                """.trimIndent()
            Result.failure(RuntimeException(errorMsg))
        }
    }

    /**
     * 保存地点
     */
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    /**
     * 获取保存的地点
     */
    fun getSavedPlace() = PlaceDao.getSavedPlace()

    /**
     * 是否保存了地点
     */
    fun isSaved() = PlaceDao.isSaved()

    private inline fun <R, T> liveData(
        context: CoroutineContext = Dispatchers.IO,
        crossinline build: suspend CoroutineScope.() -> R,
        crossinline onError: (Throwable) -> Unit = { it.printStackTrace() },
        crossinline action: (R) -> Result<T>
    ) = liveData(context) {
        val result = try {
            coroutineScope { action.invoke(build.invoke(this)) }
        } catch (t: Throwable) {
            onError.invoke(t)
            Result.failure(t)
        }
        emit(result)
    }

    /**
     * 启动并获取数据
     */
    private inline fun <reified T> launchAndGetData(
        context: CoroutineContext = Dispatchers.IO,
        crossinline action: suspend () -> ApiResponse<T>
    ) =
        launchAndGet(context = context, action = action, onSuccess = { it.getData() })

    /**
     * 启动并获取消息
     */
    private inline fun launchAndGetMsg(context: CoroutineContext = Dispatchers.IO, crossinline action: suspend () -> ApiResponse<Any>) =
        launchAndGet(context = context, action = action, onSuccess = { it.getMessage() })

    /**
     * 启动并获取
     * 返回一个
     */
    private inline fun <reified T> launchAndGet(
        context: CoroutineContext = Dispatchers.IO,
        // 需要调用的接口
        crossinline action: suspend () -> ApiResponse<T>,
        // 请求成功时的回调
        crossinline onSuccess: (ApiResponse<T>) -> T
    ) = liveData(context = context) {
        val result = try {
            coroutineScope {
                val result = action.invoke()
                Timber.d("launchAndGet：===> result is ${result.toJson()}")
                if (result.isSuccess()) Result.success(onSuccess.invoke(result))
                else when (result.getCode()) {
                    NOT_LOGIN_CODE -> {
                        checkToken()
                        Result.failure(NotLoginException(result.getMessage()))
                    }
                    else -> Result.failure(ServiceException(result.getMessage()))
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.failure(t)
        }
        emit(result)
    }
}