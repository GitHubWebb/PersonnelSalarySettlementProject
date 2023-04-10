package com.personal.salary.kotlin.http.api.sob

import com.personal.salary.kotlin.http.ServiceCreator
import com.personal.salary.kotlin.http.annotation.baseurl.SobBaseUrl
import com.personal.salary.kotlin.model.ApiResponse
import com.personal.salary.kotlin.model.UserQa
import retrofit2.http.GET
import retrofit2.http.Path

@SobBaseUrl
interface QaApi {

    /**
     * 获取指定用户的回答列表
     */
    @GET("ct/wenda/comment/list/user/{userId}/{page}")
    suspend fun loadUserQaList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserQa>

    companion object : QaApi by ServiceCreator.create()
}