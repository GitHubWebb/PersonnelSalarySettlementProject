package com.oubowu.stickyitemdecoration.adapter

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.oubowu.stickyitemdecoration.FullSpanUtil

/**
 * author : wangwx
 * github : https://github.com/GitHubWebb
 * time   : 2023/04/17
 * desc   : 支持Item吸顶列表的适配器
 */
abstract class StickerItemAdapter<T : Any, VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<T>) :
    PagingDataAdapter<T, VH>(diffCallback) {

    companion object {
        /** item吸顶 */
        @JvmField
        val TYPE_STICKY_HEAD = 1

        /** itemData */
        @JvmField
        val TYPE_DATA = 2

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        FullSpanUtil.onAttachedToRecyclerView(
            recyclerView, this, TYPE_STICKY_HEAD
        )
    }

    override fun onViewAttachedToWindow(holder: VH) {
        super.onViewAttachedToWindow(holder)
        FullSpanUtil.onViewAttachedToWindow(
            holder, this, TYPE_STICKY_HEAD
        )
    }

    abstract fun getItemLayoutId(viewType: Int): Int
}