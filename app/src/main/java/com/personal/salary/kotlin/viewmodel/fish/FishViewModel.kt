package com.personal.salary.kotlin.viewmodel.fish

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.personal.salary.kotlin.http.network.Repository

/**
 * author : wangwx
 * github : https://github.com/GitHubWebb
 * time   : 2023/04/10
 * desc   : 鱼塘 的 ViewModel
 */
class FishViewModel : ViewModel() {

    private val _fishListStateLiveData = MutableLiveData(Unit)
    val fishListStateLiveData = _fishListStateLiveData.switchMap { MutableLiveData(it) }

    fun refreshFishList() {
        _fishListStateLiveData.value = Unit
    }

    fun loadTopicList() = Repository.loadTopicList()

    fun dynamicLikes(momentId: String) = Repository.dynamicLikes(momentId)

    fun postComment(momentComment: Map<String, Any?>, isReply: Boolean) =
        Repository.postComment(momentComment, isReply)

    fun putFish(moment: Map<String, Any?>) = Repository.putFish(moment)

}