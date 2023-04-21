package com.personal.salary.kotlin.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import com.personal.salary.kotlin.aop.ui.adapter.delegate.NineGridAdapterDelegate
import com.personal.salary.kotlin.databinding.ArticleListItemBinding
import com.personal.salary.kotlin.ktx.asViewBinding
import com.personal.salary.kotlin.ktx.context
import com.personal.salary.kotlin.ktx.itemDiffCallback
import com.personal.salary.kotlin.ktx.setFixOnClickListener
import com.personal.salary.kotlin.manager.UserManager
import com.personal.salary.kotlin.model.ArticleInfo
import com.personal.salary.kotlin.ui.activity.ViewUserActivity
import com.personal.salary.kotlin.ui.adapter.delegate.AdapterDelegate
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/06/18
 * desc   : 文章列表的适配器
 */
class ArticleAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<ArticleInfo.ArticleItem, ArticleAdapter.ArticleViewHolder>(diffCallback) {

    private val nineGridAdapterDelegate = NineGridAdapterDelegate()

    private var mMenuItemClickListener: (view: View, item: ArticleInfo.ArticleItem, position: Int) -> Unit =
        { _, _, _ -> }

    fun setOnMenuItemClickListener(block: (view: View, item: ArticleInfo.ArticleItem, position: Int) -> Unit) {
        mMenuItemClickListener = block
    }

    fun setOnNineGridClickListener(block: (sources: List<String>, index: Int) -> Unit) {
        nineGridAdapterDelegate.setOnNineGridItemClickListener(block)
    }

    @SuppressLint("SetTextI18n")
    inner class ArticleViewHolder(val binding: ArticleListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<ArticleListItemBinding>())

        fun onBinding(item: ArticleInfo.ArticleItem?, position: Int) {
            item ?: return
            with(binding) {
                val userId = item.userId
                ivAvatar.setFixOnClickListener { takeIf { userId.isNotEmpty() }?.let { ViewUserActivity.start(context, userId) } }
                ivAvatar.loadAvatar(item.isVip, item.avatar)
                tvArticleTitle.text = item.title
                tvNickName.text = "${item.nickName} · ${TimeUtils.getFriendlyTimeSpanByNow(item.createTime)}"
                tvNickName.setTextColor(UserManager.getNickNameColor(item.isVip))
                val covers = item.covers
                val imageCount = covers.size
                simpleGridLayout.setOnNineGridClickListener(nineGridAdapterDelegate)
                    .setData(covers)
                simpleGridLayout.isVisible = imageCount != 0
                // tvCreateTime.text = item.createTime
                with(listMenuItem) {
                    llShare.setFixOnClickListener { mMenuItemClickListener.invoke(it, item, bindingAdapterPosition) }
                    llGreat.setFixOnClickListener { mMenuItemClickListener.invoke(it, item, bindingAdapterPosition) }
                    tvComment.text = item.viewCount.toString()
                    tvGreat.text = with(item.thumbUp) {
                        if (this == 0) "点赞" else toString()
                    }
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: ArticleViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val itemView = holder.itemView
        itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder = ArticleViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<ArticleInfo.ArticleItem>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem.id == newItem.id }
    }
}