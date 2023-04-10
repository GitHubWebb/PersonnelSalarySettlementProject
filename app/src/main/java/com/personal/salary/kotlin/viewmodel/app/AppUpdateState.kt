package com.personal.salary.kotlin.viewmodel.app

import com.personal.salary.kotlin.model.AppUpdateInfo

/**
 * App检查更新的状态
 */
data class AppUpdateState(
    @JvmField
    val checkUpdateError: Int? = null,
    @JvmField
    val networkError: Int? = null,
    @JvmField
    val appUpdateInfo: AppUpdateInfo? = null,
    @JvmField
    val isDataValid: Boolean = false
)