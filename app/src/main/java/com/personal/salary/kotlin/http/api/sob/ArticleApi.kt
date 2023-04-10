package com.personal.salary.kotlin.http.api.sob

import com.personal.salary.kotlin.http.ServiceCreator
import com.personal.salary.kotlin.http.annotation.baseurl.SobBaseUrl
import com.personal.salary.kotlin.model.ApiResponse
import com.personal.salary.kotlin.model.ArticleDetail
import com.personal.salary.kotlin.model.UserArticle
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

@SobBaseUrl
interface ArticleApi {

    /**
     * 根据文章id获取文章详情内容
     */
    @GET("ct/article/detail/{articleId}")
    suspend fun getArticleDetailById(@Path("articleId") articleId: String): ApiResponse<ArticleDetail>

    /**
     * 获取指定用户的文章列表
     */
    @GET("ct/article/list/{userId}/{page}")
    suspend fun loadUserArticleList(
        @Path("userId") userId: String,
        @Path("page") page: Int
    ): ApiResponse<UserArticle>

    @PUT("ct/article/thumb-up/{articleId}")
    suspend fun articleLikes(@Path("articleId") articleId: String): ApiResponse<Int>

    companion object : ArticleApi by ServiceCreator.create()
}