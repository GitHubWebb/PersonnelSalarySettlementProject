package com.personal.salary.kotlin.http.api.sob

import com.personal.salary.kotlin.http.ServiceCreator
import com.personal.salary.kotlin.http.annotation.baseurl.SobBaseUrl
import com.personal.salary.kotlin.model.ApiResponse
import com.personal.salary.kotlin.model.Bookmark
import com.personal.salary.kotlin.model.BookmarkDetail
import retrofit2.http.GET
import retrofit2.http.Path

@SobBaseUrl
interface CollectionApi {

    /**
     * 获取收藏夹列表
     */
    @GET("ct/ucenter/collection/list/{page}")
    suspend fun getCollectionList(@Path("page") page: Int): ApiResponse<Bookmark>

    /**
     * 根据收藏夹 id 获取收藏夹详情列表
     * collectionId：收藏夹 id
     * page：页码
     * order：排序方式 0、表示降序，1表示升序，按添加时间
     */
    @GET("ct/ucenter/favorite/list/{collectionId}/{page}/{order}")
    suspend fun getCollectionDetailListById(
        @Path("collectionId") collectionId: String,
        @Path("page") page: Int,
        @Path("order") order: Int
    ): ApiResponse<BookmarkDetail>

    companion object : CollectionApi by ServiceCreator.create()
}