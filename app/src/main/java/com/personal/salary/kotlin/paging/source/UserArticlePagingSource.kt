package com.personal.salary.kotlin.paging.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.personal.salary.kotlin.execption.ServiceException
import com.personal.salary.kotlin.http.network.ArticleNetwork
import com.personal.salary.kotlin.model.UserArticle
import timber.log.Timber

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/10/31
 * desc   : 指定用户的文章 PagingSource
 */
class UserArticlePagingSource(private val userId: String) :
    PagingSource<Int, UserArticle.UserArticleItem>() {

    override fun getRefreshKey(state: PagingState<Int, UserArticle.UserArticleItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserArticle.UserArticleItem> {
        return try {
            val page = params.key ?: FIRST_PAGE_INDEX
            Timber.d("load：===> userId is $userId page is $page")
            val response = ArticleNetwork.loadUserArticleList(userId = userId, page = page)
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