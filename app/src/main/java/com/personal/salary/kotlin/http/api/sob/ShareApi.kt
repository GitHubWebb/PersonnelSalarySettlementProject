package com.personal.salary.kotlin.http.api.sob

import com.personal.salary.kotlin.http.ServiceCreator
import com.personal.salary.kotlin.http.annotation.baseurl.SobBaseUrl
import com.personal.salary.kotlin.model.ApiResponse
import com.personal.salary.kotlin.model.UserShare
import retrofit2.http.GET
import retrofit2.http.Path

@SobBaseUrl
interface ShareApi {

    /**
     * 获取指定用户的分享列表
     */
    @GET("ct/share/list/{userId}/{page}")
    suspend fun loadUserShareList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserShare>

    companion object : ShareApi by ServiceCreator.create()
}