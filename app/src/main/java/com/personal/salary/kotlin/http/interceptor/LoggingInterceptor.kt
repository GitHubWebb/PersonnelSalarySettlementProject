package com.personal.salary.kotlin.http.interceptor

import com.personal.salary.kotlin.ktx.unicodeToString
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/24
 * desc   : 日志拦截器
 */
val loggingInterceptor =
    HttpLoggingInterceptor { result -> result.takeIf { debugLoggerEnable }?.let { Timber.d("===> result：${it.unicodeToString()}") } }
        .also { it.setLevel(HttpLoggingInterceptor.Level.BODY) }

const val debugLoggerEnable = true