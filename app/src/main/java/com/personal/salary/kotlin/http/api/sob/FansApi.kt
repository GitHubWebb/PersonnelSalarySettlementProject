package com.personal.salary.kotlin.http.api.sob

import com.personal.salary.kotlin.http.ServiceCreator
import com.personal.salary.kotlin.http.annotation.baseurl.SobBaseUrl
import com.personal.salary.kotlin.model.ApiResponse
import com.personal.salary.kotlin.model.UserFollow
import retrofit2.http.GET
import retrofit2.http.Path

@SobBaseUrl
interface FansApi {

    /**
     * 获取用户的粉丝列表
     */
    @GET("uc/fans/list/{userId}/{page}")
    suspend fun loadUserFansList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserFollow>

    companion object : FansApi by ServiceCreator.create()
}