package com.personal.salary.kotlin.http.network

import com.personal.salary.kotlin.http.api.sob.FollowApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 关注获取
 */
object FollowNetwork {

    suspend fun loadUserFollowList(userId: String, page: Int) = FollowApi.loadUserFollowList(userId, page)
}