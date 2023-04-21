package com.personal.salary.kotlin.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import com.oubowu.stickyitemdecoration.adapter.StickerItemAdapter
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.aop.ui.adapter.delegate.NineGridAdapterDelegate
import com.personal.salary.kotlin.databinding.ItemRosterStickyHeadBinding
import com.personal.salary.kotlin.databinding.RosterListItemBinding
import com.personal.salary.kotlin.ktx.asViewBinding
import com.personal.salary.kotlin.ktx.context
import com.personal.salary.kotlin.ktx.itemDiffCallback
import com.personal.salary.kotlin.ktx.setFixOnClickListener
import com.personal.salary.kotlin.manager.EmployeeRosterStore
import com.personal.salary.kotlin.manager.UserManager
import com.personal.salary.kotlin.ui.activity.ViewUserActivity
import com.personal.salary.kotlin.ui.adapter.delegate.AdapterDelegate
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : wangwx
 * github : https://github.com/GitHubWebb
 * time   : 2023/04/17
 * desc   : 花名册(人员列表)列表的适配器
 */
class RosterAdapter(private val adapterDelegate: AdapterDelegate) :
    StickerItemAdapter<EmployeeRosterStore, RecyclerView.ViewHolder>(diffCallback) {

    private val nineGridAdapterDelegate = NineGridAdapterDelegate()

    private var mMenuItemClickListener: (view: View, item: EmployeeRosterStore, position: Int) -> Unit =
        { _, _, _ -> }

    fun setOnMenuItemClickListener(block: (view: View, item: EmployeeRosterStore, position: Int) -> Unit) {
        mMenuItemClickListener = block
    }

    fun setOnNineGridClickListener(block: (sources: List<String>, index: Int) -> Unit) {
        nineGridAdapterDelegate.setOnNineGridItemClickListener(block)
    }

    @SuppressLint("SetTextI18n")
    inner class RosterHeaderHolder(val binding: ItemRosterStickyHeadBinding) :
        RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<ItemRosterStickyHeadBinding>())

        fun onBinding(item: EmployeeRosterStore?, position: Int) {
            item ?: return
            with(binding) {
                tvRosterStickyHeadName.text = item.firstOrderDept
                tvCount.text = "${item.itemCount}人"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    inner class RosterHolder(val binding: RosterListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<RosterListItemBinding>())

        fun onBinding(item: EmployeeRosterStore?, position: Int) {
            item ?: return
            with(binding) {
                val userId = item.empIdCard
                userId?.let {
                    ivAvatar.setFixOnClickListener {
                        takeIf { userId.isNotEmpty() }?.let {
                            ViewUserActivity.start(
                                context, userId
                            )
                        }
                    }
                }
                ivAvatar.loadAvatar(true, item.affiliatedUnit)
                tvNickName.text = "${item.empName} · ${
                    TimeUtils.getFriendlyTimeSpanByNow(
                        TimeUtils.string2Date(
                            item.entryDate,
                            // 中国标准时间序列化 Fri Mar 31 00:00:00 GMT+08:00 2023
                            SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
                        )
                    )
                }"
                tvNickName.setTextColor(UserManager.getNickNameColor(false))
                tvTeamJobName.text = item.teamName + item.jobTitle
                tvRankName.text = item.rankName

                // tvCreateTime.text = item.createTime
                with(listMenuItem) {
                    llShare.setFixOnClickListener {
                        mMenuItemClickListener.invoke(
                            it, item, bindingAdapterPosition
                        )
                    }
                    llGreat.setFixOnClickListener {
                        mMenuItemClickListener.invoke(
                            it, item, bindingAdapterPosition
                        )
                    }
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun getItemLayoutId(viewType: Int): Int = when (viewType) {
        TYPE_STICKY_HEAD -> R.layout.item_roster_sticky_head
        else -> R.layout.roster_list_item
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView
        itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, position) }

        when (holder) {
            is RosterHeaderHolder -> holder.onBinding(getItem(position), position)
            is RosterHolder -> holder.onBinding(getItem(position), position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_STICKY_HEAD -> RosterHeaderHolder(parent)
            else -> RosterHolder(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.run { itemType } ?: TYPE_STICKY_HEAD
    }

    companion object {

        private val diffCallback =
            itemDiffCallback<EmployeeRosterStore>({ oldItem, newItem -> oldItem.uId == newItem.uId }) { oldItem, newItem -> oldItem.uId == newItem.uId }
    }
}