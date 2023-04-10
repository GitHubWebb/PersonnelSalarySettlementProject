package com.personal.salary.kotlin.action

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/24
 * desc   : 初始化，一般用于 Activity/Fragment 的初始化
 */
interface Init {

    fun initView() {}

    fun initData() {}

    fun initEvent() {}

    fun initObserver() {}
}