package com.personal.salary.kotlin.model

import com.google.gson.annotations.SerializedName
import java.io.File

/**
 * author : wangwx
 * github : https://github.com/GitHubWebb
 * time   : 2023/04/11
 * desc   : Fir.im版本更新接口返回实体
 */
class FirApiResponse(
    @SerializedName(value = "updateLog", alternate = ["changelog"])
    val updateLog: String = "",
    @SerializedName(value = "versionName", alternate = ["name"])
    val versionName: String = "人员薪资汇算1.0",
    @SerializedName(value = "versionCode", alternate = ["version"])
    val versionCode: Int = 1,
    @SerializedName("minVersionCode")
    val minVersionCode: Int = 1,
    @SerializedName(value = "url", alternate = ["installUrl"])
    val url: String? = null,
    @SerializedName("apkSize")
    val apkSize: Long = 0,
    @SerializedName("apkHash")
    val apkHash: String? = null,
    @SerializedName("file")
    var file: File? = null,
    @SerializedName("forceUpdate")
    val forceUpdate: Boolean = true

) : ApiResponse<FirApiResponse.FirAppUpdateInfo>(
    code = 200,
    success = true,
    message = "",
    data = FirApiResponse.FirAppUpdateInfo()
) {

    override fun getData(): FirAppUpdateInfo {
        var firAppUpdateInfo = super.getData()
        firAppUpdateInfo.updateLog = updateLog
        firAppUpdateInfo.versionName = versionName
        firAppUpdateInfo.versionCode = versionCode
        firAppUpdateInfo.minVersionCode = minVersionCode
        firAppUpdateInfo.url = url
        firAppUpdateInfo.forceUpdate = forceUpdate
        return firAppUpdateInfo
    }

    data class FirAppUpdateInfo(
        @SerializedName(value = "updateLog", alternate = ["changelog"])
        var updateLog: String? = "",
        @SerializedName(value = "versionName", alternate = ["name"])
        var versionName: String? = "人员薪资汇算1.0",
        @SerializedName(value = "versionCode", alternate = ["version"])
        var versionCode: Int = 1,
        @SerializedName("minVersionCode")
        var minVersionCode: Int = 1,
        @SerializedName(value = "url", alternate = ["installUrl"])
        var url: String? = null,
        @SerializedName("apkSize")
        var apkSize: Long = 0,
        @SerializedName("apkHash")
        val apkHash: String? = null,
        @SerializedName("file")
        var file: File? = null,
        @SerializedName("forceUpdate")
        var forceUpdate: Boolean = false
    )
}