package com.personal.salary.kotlin.db

import com.personal.salary.kotlin.util.SUNNY_BEACH_API_BASE_URL
import com.tencent.mmkv.MMKV
import okhttp3.Headers
import okhttp3.Request
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/01/01
 * desc   : Sob 缓存管理者
 */
object SobCacheManager {

    private const val SOB_ACCOUNT_MAP = "SOB_ACCOUNT_MAP"
    private val mmkv = MMKV.mmkvWithID(SOB_ACCOUNT_MAP)
    private const val SOB_CAPTCHA_KEY_NAME = "l_c_i"
    private var sobCaptchaKey = ""
    const val SOB_TOKEN_NAME = "sob_token"

    fun addHeadersByNeed(request: Request, requestBuilder: Request.Builder) {
        val url = request.url.toString()
        val topDomain = com.personal.salary.kotlin.util.StringUtil.getTopDomain(SUNNY_BEACH_API_BASE_URL)
        when {
            url.contains("uc/user/login") ||
                    url.contains("uc/ut/join/send-sms") ||
                    url.contains("uc/user/register") ||
                    url.contains("uc/ut/forget/send-sms") -> {
                Timber.d("addHeadersByNeed：===> sobCaptchaKey is $sobCaptchaKey")
                requestBuilder.addHeader(SOB_CAPTCHA_KEY_NAME, sobCaptchaKey)
            }
            url.contains(topDomain) -> {
                val sobToken = getHeader(SOB_TOKEN_NAME)
                Timber.d("addHeadersByNeed：===> sobToken is $sobToken")
                requestBuilder.addHeader(SOB_TOKEN_NAME, sobToken)
            }
            else -> {
                // Nothing to do.
            }
        }
    }

    fun saveHeadersByNeed(request: Request, headers: Headers) {
        val url = request.url.toString()
        Timber.d("saveSobCaptchaKeyByNeed：===> url is $url")
        when {
            url.contains("uc/ut/captcha") -> {
                sobCaptchaKey = headers[SOB_CAPTCHA_KEY_NAME].orEmpty()
                Timber.d("saveSobCaptchaKeyByNeed：===> sobCaptchaKey is $sobCaptchaKey")
            }
            url.contains("uc/user/login") -> {
                val sobToken = headers[SOB_TOKEN_NAME]
                Timber.d("saveSobCaptchaKeyByNeed：===> sobToken is $sobToken")
                saveHeader(SOB_TOKEN_NAME, sobToken)
            }
            url.contains("uc/user/checkToken") -> {
                val sobToken = headers[SOB_TOKEN_NAME].orEmpty()
                if (sobToken.isNotBlank()) {
                    Timber.d("saveSobCaptchaKeyByNeed：===> sobToken is $sobToken")
                    saveHeader(SOB_TOKEN_NAME, sobToken)
                }
            }
            else -> {
                // Nothing to do.
            }
        }
    }

    fun saveHeader(key: String, value: String?) {
        mmkv.putString(key, value)
    }

    fun getSobToken() = mmkv.getString(SOB_TOKEN_NAME, "").orEmpty()

    fun getHeader(key: String) = mmkv.getString(key, "").orEmpty()

    fun onAccountLoginOut() {
        mmkv.clearAll()
    }
}