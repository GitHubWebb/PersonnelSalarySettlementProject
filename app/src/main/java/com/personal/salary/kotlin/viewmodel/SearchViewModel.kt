package com.personal.salary.kotlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.personal.salary.kotlin.manager.EmployeeRosterManager
import com.personal.salary.kotlin.manager.EmployeeRosterStore
import com.personal.salary.kotlin.manager.FirstDeptAndRosterCountVO
import com.personal.salary.kotlin.model.SearchResult
import com.personal.salary.kotlin.other.SearchType
import com.personal.salary.kotlin.other.SortType
import com.personal.salary.kotlin.paging.source.SearchPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索 ViewModel
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val _keywordsLiveData = MutableLiveData<String>()
    val keywordsLiveData: LiveData<String> get() = _keywordsLiveData

    private var _firstDeptAndRosterCountVOS = mutableListOf<FirstDeptAndRosterCountVO>()
    val firstDeptAndRosterCountVOS get() = _firstDeptAndRosterCountVOS

    private val employeeRosterManager = EmployeeRosterManager.get()

    fun setKeywords(keywords: String) {
        _keywordsLiveData.value = keywords
    }

    /** 文章 */
    fun searchArticleByKeywords(
        keyword: String, searchType: SearchType, sortType: SortType
    ): Flow<PagingData<SearchResult.SearchResultItem>> {
        if (keyword.isEmpty()) return flowOf<PagingData<SearchResult.SearchResultItem>>(PagingData.empty()).cachedIn(
            viewModelScope
        )
        return Pager(config = PagingConfig(30), pagingSourceFactory = {
            SearchPagingSource(keyword = keyword, searchType = searchType, sortType = sortType)
        }).flow.cachedIn(viewModelScope)
    }

    /** 搜索人员信息 */
    fun searchByKeywords(
        keyword: String, searchType: String
    ): Flow<PagingData<EmployeeRosterStore>> {
        if (keyword.isEmpty() && searchType.isEmpty()) return flowOf<PagingData<EmployeeRosterStore>>(
            PagingData.empty()
        ).cachedIn(
            viewModelScope
        )

        return Pager(config = PagingConfig(30), pagingSourceFactory = {
            employeeRosterManager.getRosterByKeywords(keyword, searchType)
        }).flow.cachedIn(viewModelScope)
    }

    fun getDeptNameAndRosterCount() {
        _firstDeptAndRosterCountVOS = employeeRosterManager.getDeptNameAndRosterCount()
            ?: mutableListOf<FirstDeptAndRosterCountVO>()
        var rosterTotal = 0
        _firstDeptAndRosterCountVOS.forEachIndexed { position, it ->
            rosterTotal += it.rosterCount
        }

        _firstDeptAndRosterCountVOS.add(
            0, FirstDeptAndRosterCountVO(
                rosterTotal, "全部"
            )
        )
    }
}
