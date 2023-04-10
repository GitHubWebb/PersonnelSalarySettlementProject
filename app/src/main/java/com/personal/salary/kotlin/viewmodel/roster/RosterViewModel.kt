package com.personal.salary.kotlin.viewmodel.roster

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.personal.salary.kotlin.http.network.Repository

/**
 * author : wangwx
 * github : https://github.com/GitHubWebb
 * time   : 2023/04/10
 * desc   : 花名册(人员列表) 的 ViewModel
 */
class RosterViewModel : ViewModel() {

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