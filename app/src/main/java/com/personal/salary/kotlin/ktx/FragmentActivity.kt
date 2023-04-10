package com.personal.salary.kotlin.ktx

import android.app.Activity
import android.content.Context
import androidx.core.app.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.personal.salary.kotlin.execption.NotLoginException
import com.personal.salary.kotlin.manager.UserManager
import com.personal.salary.kotlin.model.UserBasicInfo
import com.personal.salary.kotlin.ui.activity.LoginActivity
import com.personal.salary.kotlin.ui.dialog.MessageDialog
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 解析当前用户的 Token
 */
fun ComponentActivity.checkToken(block: (userBasicInfo: Result<UserBasicInfo>) -> Unit) {
    lifecycleScope.launch {
        when (val result = UserManager.loadUserBasicInfo()) {
            null -> block.invoke(Result.failure(NotLoginException()))
            else -> block.invoke(Result.success(result))
        }
    }
}

/**
 * We use an atomic boolean to mark whether the login dialog is currently displayed.
 */
private val isShowing = AtomicBoolean(false)

/**
 * 检查是否用户登录
 * 如果没有登录则跳转至登录界面，否则执行 block Lambda 中的代码
 */
fun ComponentActivity.takeIfLogin(block: (userBasicInfo: UserBasicInfo) -> Unit) {
    checkToken {
        when (val userBasicInfo = it.getOrNull()) {
            null -> tryShowLoginDialog()
            else -> block.invoke(userBasicInfo)
        }
    }
}

fun Activity.tryShowLoginDialog() {
    takeUnless { isShowing.get() }?.let { showLoginDialog() }
}

private fun Context.showLoginDialog() {
    MessageDialog.Builder(this)
        .setTitle("系统消息")
        .setMessage("账号未登录，是否登录？")
        .setConfirm("现在登录")
        .setCancel("暂不登录")
        .addOnShowListener {
            isShowing.set(true)
        }
        .addOnDismissListener {
            isShowing.set(false)
        }
        .setListener {
            LoginActivity.start(this, UserManager.getCurrLoginAccount(), UserManager.getCurrLoginAccountPassword())
        }.show()
}