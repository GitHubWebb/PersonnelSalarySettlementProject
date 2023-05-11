package com.personal.salary.kotlin.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import com.personal.salary.kotlin.databinding.RosterListItemBinding
import com.personal.salary.kotlin.databinding.UserQaListItemBinding
import com.personal.salary.kotlin.ktx.asViewBinding
import com.personal.salary.kotlin.ktx.itemDiffCallback
import com.personal.salary.kotlin.ktx.setFixOnClickListener
import com.personal.salary.kotlin.manager.EmployeeRosterStore
import com.personal.salary.kotlin.manager.UserManager
import com.personal.salary.kotlin.model.SearchResult
import com.personal.salary.kotlin.ui.adapter.delegate.AdapterDelegate

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/05/11
 * desc   : 搜索结果列表的适配器
 */
class SearchResultListAdapter(private val adapterDelegate: AdapterDelegate) :
    PagingDataAdapter<EmployeeRosterStore, SearchResultListAdapter.SearchResultListViewHolder>(diffCallback) {

    inner class SearchResultListViewHolder(val binding: RosterListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<RosterListItemBinding>())

        // Replace image with empty drawable.
        private val emptyDrawable = ColorDrawable()

        fun onBind(item: EmployeeRosterStore?, position: Int) {
            item ?: return
            with(binding) {
                // tvNickName.text = item.title.parseAsHtml()
                // We do not need to display image information in the search results,
                // so as to avoid typographical confusion caused by images.
                // tvTeamJobName.text = item.content.parseAsHtml(imageGetter = { emptyDrawable })
                tvNickName.text = "${item.empName} · ${
                    TimeUtils.getFriendlyTimeSpanByNow(item.entryDate)
                    /*TimeUtils.string2Date(
                        item.entryDate,
                        // 中国标准时间序列化 Fri Mar 31 00:00:00 GMT+08:00 2023
                        SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                    )
                )*/
                }"
                tvNickName.setTextColor(UserManager.getNickNameColor(false))
                tvTeamJobName.text = item.teamName + item.jobTitle
                tvRankName.text = item.rankName
            }
        }
    }

    override fun onViewAttachedToWindow(holder: SearchResultListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: SearchResultListAdapter.SearchResultListViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }
        holder.onBind(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultListViewHolder = SearchResultListViewHolder(parent)

    companion object {

        private val diffCallback =
            itemDiffCallback<EmployeeRosterStore>({ oldItem, newItem -> oldItem.uId == newItem.uId }) { oldItem, newItem -> oldItem == newItem }
    }
}