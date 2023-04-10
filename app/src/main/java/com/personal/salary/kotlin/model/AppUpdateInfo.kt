package com.personal.salary.kotlin.model

import com.google.gson.annotations.SerializedName
import java.io.File

data class AppUpdateInfo(
    @SerializedName("updateLog")
    val updateLog: String = "",
    @SerializedName("versionName")
    val versionName: String = "KotlinApplication1.0",
    @SerializedName("versionCode")
    val versionCode: Int = 1,
    @SerializedName("minVersionCode")
    val minVersionCode: Int = 1,
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("apkSize")
    val apkSize: Long = 0,
    @SerializedName("apkHash")
    val apkHash: String? = null,
    @SerializedName("file")
    var file: File? = null,
    @SerializedName("forceUpdate")
    val forceUpdate: Boolean = false
)