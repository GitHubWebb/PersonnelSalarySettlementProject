package com.personal.salary.kotlin.ktx

import org.json.JSONObject

// region Kotlin-DSL

// 更多用法查阅：https://github.com/Kotlin/KEEP/blob/master/proposals/context-receivers.md

 // Kotlin 1.6.20 新功能 使用版本为 1.5.31 需要 暂时屏蔽
fun json(build: JSONObject.() -> Unit) = JSONObject().apply { build() }

context(JSONObject)
        infix fun String.by(build: JSONObject.() -> Unit): JSONObject = put(this, JSONObject().build())

context(JSONObject)
        infix fun String.by(value: Any): JSONObject = put(this, value)

// endregion

