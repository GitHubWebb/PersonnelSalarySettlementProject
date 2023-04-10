package com.personal.salary.kotlin.model

import com.google.gson.annotations.SerializedName

/**
 * 文章推荐
 */
class ArticleInfo : Page<ArticleInfo.ArticleItem>() {
    data class ArticleItem(
        @SerializedName("avatar")
        val avatar: String = "",
        @SerializedName("covers")
        val covers: List<String> = listOf(),
        @SerializedName("createTime")
        val createTime: String = "",
        @SerializedName("id")
        val id: String = "",
        @SerializedName("nickName")
        val nickName: String = "",
        @SerializedName("thumbUp")
        var thumbUp: Int = 0,
        @SerializedName("title")
        val title: String = "",
        @SerializedName("type")
        val type: Int = 0,
        @SerializedName("userId")
        val userId: String = "",
        @SerializedName("viewCount")
        val viewCount: Int = 0,
        @SerializedName("vip")
        val vip: Boolean = false
    )
}