package com.personal.salary.kotlin.http.network

import com.personal.salary.kotlin.http.api.sob.CollectionApi

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/06/08
 * desc   : 收藏信息获取
 */
object CollectionNetwork {

    suspend fun getCollectionList(page: Int) = CollectionApi.getCollectionList(page)
}