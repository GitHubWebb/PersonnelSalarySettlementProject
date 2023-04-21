package com.personal.salary.kotlin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.personal.salary.kotlin.model.SearchResult
import com.personal.salary.kotlin.other.SearchType
import com.personal.salary.kotlin.other.SortType
import com.personal.salary.kotlin.paging.source.SearchPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索 ViewModel
 */
class SearchViewModel : ViewModel() {

    private val _keywordsLiveData = MutableLiveData<String>()
    val keywordsLiveData: LiveData<String> get() = _keywordsLiveData

    fun setKeywords(keywords: String) {
        _keywordsLiveData.value = keywords
    }

    fun searchByKeywords(keyword: String, searchType: SearchType, sortType: SortType): Flow<PagingData<SearchResult.SearchResultItem>> {
        if (keyword.isEmpty()) return flowOf<PagingData<SearchResult.SearchResultItem>>(PagingData.empty()).cachedIn(viewModelScope)
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                SearchPagingSource(keyword = keyword, searchType = searchType, sortType = sortType)
            }).flow.cachedIn(viewModelScope)
    }
}
