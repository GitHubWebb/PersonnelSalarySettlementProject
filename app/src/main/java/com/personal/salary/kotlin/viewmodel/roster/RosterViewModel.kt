package com.personal.salary.kotlin.viewmodel.roster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.personal.salary.kotlin.http.network.Repository
import com.personal.salary.kotlin.model.ArticleInfo
import com.personal.salary.kotlin.model.ArticleSearchFilter
import com.personal.salary.kotlin.model.UserArticle
import com.personal.salary.kotlin.paging.source.ArticlePagingSource
import com.personal.salary.kotlin.paging.source.UserArticlePagingSource
import com.personal.salary.kotlin.paging.source.UserPublishArticlePagingSource
import kotlinx.coroutines.flow.Flow

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/27
 * desc   : 文章 ViewModel
 */
class RosterViewModel : ViewModel() {

    fun searchUserArticleList(searchFilter: ArticleSearchFilter): Flow<PagingData<UserArticle.UserArticleItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                UserPublishArticlePagingSource(searchFilter)
            }).flow.cachedIn(viewModelScope)
    }

    fun getArticleDetailById(articleId: String) = Repository.getArticleDetailById(articleId)

    fun articleLikes(articleId: String) = Repository.articleLikes(articleId)

    fun getUserArticleList(userId: String): Flow<PagingData<UserArticle.UserArticleItem>> {
        return Pager(config = PagingConfig(30),
            pagingSourceFactory = {
                UserArticlePagingSource(userId)
            }).flow.cachedIn(viewModelScope)
    }

    fun getArticleListByCategoryId(categoryId: String): Flow<PagingData<ArticleInfo.ArticleItem>> {
        return Pager(
            config = PagingConfig(30),
            pagingSourceFactory = {
                ArticlePagingSource(categoryId)
            }).flow.cachedIn(viewModelScope)
    }
}