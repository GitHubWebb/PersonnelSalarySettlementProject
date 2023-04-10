package com.personal.salary.kotlin.http.api.other

import com.hjq.http.config.IRequestApi

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/07
 *    desc   : 退出登录
 */
class LogoutApi : IRequestApi {

    override fun getApi(): String {
        return "user/logout"
    }
}