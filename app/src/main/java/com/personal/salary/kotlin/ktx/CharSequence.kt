package com.personal.salary.kotlin.ktx

public inline fun CharSequence?.orEmpty(): String = this?.toString() ?: ""