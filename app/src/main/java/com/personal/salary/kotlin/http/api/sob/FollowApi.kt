package com.personal.salary.kotlin.http.api.sob

import com.personal.salary.kotlin.http.ServiceCreator
import com.personal.salary.kotlin.http.annotation.baseurl.SobBaseUrl
import com.personal.salary.kotlin.model.ApiResponse
import com.personal.salary.kotlin.model.UserFollow
import retrofit2.http.GET
import retrofit2.http.Path

@SobBaseUrl
interface FollowApi {

    /**
     * 获取用户关注的用户列表
     */
    @GET("uc/follow/list/{userId}/{page}")
    suspend fun loadUserFollowList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserFollow>

    companion object : FollowApi by ServiceCreator.create()
}