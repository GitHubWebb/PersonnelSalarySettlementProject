package com.personal.salary.kotlin.ktx

import androidx.fragment.app.Fragment
import com.dylanc.longan.viewLifecycleScope
import com.personal.salary.kotlin.execption.NotLoginException
import com.personal.salary.kotlin.http.network.Repository
import com.personal.salary.kotlin.model.UserBasicInfo
import kotlinx.coroutines.launch

/**
 * 解析当前用户的 Token
 */
fun Fragment.checkToken(block: suspend (Result<UserBasicInfo>) -> Unit) {
    viewLifecycleScope.launch {
        when (val result = Repository.checkToken()) {
            null -> block.invoke(Result.failure(NotLoginException()))
            else -> block.invoke(Result.success(result))
        }
    }
}

/**
 * 检查是否用户登录
 * 如果没有登录则跳转至登录界面，否则执行 block Lambda 中的代码
 */
fun Fragment.takeIfLogin(block: suspend (userBasicInfo: UserBasicInfo) -> Unit) {
    checkToken {
        when (val userBasicInfo = it.getOrNull()) {
            null -> requireActivity().tryShowLoginDialog()
            else -> block.invoke(userBasicInfo)
        }
    }
}