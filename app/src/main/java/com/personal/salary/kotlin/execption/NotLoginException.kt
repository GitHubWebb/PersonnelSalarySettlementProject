package com.personal.salary.kotlin.execption

class NotLoginException(override val message: String = "账号未登录") : RuntimeException(message)