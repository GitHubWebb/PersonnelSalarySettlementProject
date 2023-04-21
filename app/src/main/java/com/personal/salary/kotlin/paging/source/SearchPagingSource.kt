package com.personal.salary.kotlin.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.personal.salary.kotlin.execption.ServiceException
import com.personal.salary.kotlin.http.api.sob.HomeApi
import com.personal.salary.kotlin.model.SearchResult
import com.personal.salary.kotlin.other.SearchType
import com.personal.salary.kotlin.other.SortType
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索 PagingSource
 */
class SearchPagingSource(private val keyword: String, private val searchType: SearchType, private val sortType: SortType) :
    PagingSource<Int, SearchResult.SearchResultItem>() {

    override fun getRefreshKey(state: PagingState<Int, SearchResult.SearchResultItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchResult.SearchResultItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            val searchType = searchType.value
            val sortType = sortType.value
            Timber.d("load：===> keyword is $keyword page is $page searchType is $searchType sortType is $sortType")
            val response = HomeApi.searchByKeywords(keyword = keyword, type = searchType, page = page, sort = sortType)
            val responseData = response.getData()
            val currentPage = responseData.currentPage
            val prevKey = if (responseData.hasPre) currentPage - 1 else null
            val nextKey = if (responseData.hasNext) currentPage + 1 else null
            if (response.isSuccess()) LoadResult.Page(
                data = responseData.list,
                prevKey = prevKey,
                nextKey = nextKey
            )
            else LoadResult.Error(ServiceException())
        } catch (t: Throwable) {
            t.printStackTrace()
            LoadResult.Error(t)
        }
    }

    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}