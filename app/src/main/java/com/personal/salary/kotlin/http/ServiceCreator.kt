package com.personal.salary.kotlin.http

import cn.android52.network.interceptor.BaseUrlInterceptor
import com.hjq.gson.factory.GsonFactory
import com.personal.salary.kotlin.http.interceptor.accountInterceptor
import com.personal.salary.kotlin.http.interceptor.loggingInterceptor
import com.personal.salary.kotlin.manager.LocalCookieManager
import com.personal.salary.kotlin.util.BASE_URL
import com.personal.salary.kotlin.util.CAI_YUN_BASE_URL
import com.personal.salary.kotlin.util.SUNNY_BEACH_API_BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/02
 * desc   : 网络请求服务创建者
 */
object ServiceCreator {

    val retrofit: Retrofit by lazy { createRetrofit { baseUrl(BASE_URL) } }

    private val cookieManager = LocalCookieManager.get()

    val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(BaseUrlInterceptor(setOf(SUNNY_BEACH_API_BASE_URL, CAI_YUN_BASE_URL, BASE_URL)))
            .addInterceptor(accountInterceptor)
            .addInterceptor(loggingInterceptor)
            .cookieJar(cookieManager)
            .build()
    }

    private fun createRetrofit(block: Retrofit.Builder.() -> Retrofit.Builder) = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonFactory.getSingletonGson()))
        .client(client)
        .run(block)
        .build()

    inline fun <reified T> create(): T = retrofit.create(T::class.java)
}