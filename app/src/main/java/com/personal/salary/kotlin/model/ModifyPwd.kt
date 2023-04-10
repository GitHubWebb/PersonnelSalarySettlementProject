package com.personal.salary.kotlin.model

/**
 * 修改密码
 */
data class ModifyPwd(
    private val oldPwd: String = "",
    private val newPwd: String = "",
    private val captcha: String
)