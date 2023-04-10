package com.personal.salary.kotlin.http.network

import com.personal.salary.kotlin.http.api.sob.QaApi

/**
 *    author : A Lonely Cat
 *    github : https://github.com/anjiemo/SunnyBeach
 *    time   : 2022/04/12
 *    desc   : 问答获取
 */
object QaNetwork {

    suspend fun loadUserQaList(userId: String, page: Int) =
        QaApi.loadUserQaList(userId, page)
}