package com.personal.salary.kotlin.http.api.app

import com.personal.salary.kotlin.http.ServiceCreator
import com.personal.salary.kotlin.http.annotation.baseurl.GiteeBaseUrl
import com.personal.salary.kotlin.model.ApiResponse
import com.personal.salary.kotlin.model.AppUpdateInfo
import com.personal.salary.kotlin.model.MourningCalendar
import retrofit2.http.GET

@GiteeBaseUrl
interface AppApi {

    /**
     * 检查 App 更新
     */
    @GET("appconfig.json")
    suspend fun checkAppUpdate(): ApiResponse<AppUpdateInfo>

    /**
     * 获取哀悼日历
     */
    @GET("mourning_calendar.json")
    suspend fun getMourningCalendar(): ApiResponse<List<MourningCalendar>>

    companion object : AppApi by ServiceCreator.create()
}
