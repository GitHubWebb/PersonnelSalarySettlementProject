package com.personal.salary.kotlin.ui.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.parseAsHtml
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.personal.salary.kotlin.ui.adapter.delegate.AdapterDelegate
import com.personal.salary.kotlin.aop.ui.adapter.delegate.NineGridAdapterDelegate
import com.blankj.utilcode.util.TimeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.databinding.RosterListItemBinding
import com.personal.salary.kotlin.ktx.*
import com.personal.salary.kotlin.manager.UserManager
import com.personal.salary.kotlin.model.Fish
import com.personal.salary.kotlin.ui.activity.BrowserActivity
import com.personal.salary.kotlin.ui.activity.ViewUserActivity
import com.personal.salary.kotlin.util.EmojiImageGetter
import java.text.SimpleDateFormat
import java.util.*

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2021/09/06
 * desc   : 花名册列表的适配器
 */
class RosterListAdapter(private val adapterDelegate: AdapterDelegate, private val expandContent: Boolean = false) :
    PagingDataAdapter<Fish.FishItem, RosterListAdapter.FishListViewHolder>(diffCallback) {

    private val nineGridAdapterDelegate = NineGridAdapterDelegate()
    private val mSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.SIMPLIFIED_CHINESE)

    private var mMenuItemClickListener: (view: View, item: Fish.FishItem, position: Int) -> Unit =
        { _, _, _ -> }

    fun setOnMenuItemClickListener(block: (view: View, item: Fish.FishItem, position: Int) -> Unit) {
        mMenuItemClickListener = block
    }

    inner class FishListViewHolder(val binding: RosterListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup) : this(parent.asViewBinding<RosterListItemBinding>())

        @SuppressLint("SetTextI18n")
        fun onBinding(item: Fish.FishItem?, position: Int) {
            item ?: return
            with(binding) {
                val userId = item.userId
                ivFishPondAvatar.setFixOnClickListener { takeIf { userId.isNotEmpty() }?.let { ViewUserActivity.start(context, userId) } }
                ivFishPondAvatar.loadAvatar(item.vip, item.avatar)
                tvFishPondNickName.setTextColor(UserManager.getNickNameColor(item.vip))
                tvFishPondNickName.text = item.nickname
                val job = item.position.ifNullOrEmpty { "滩友" }
                tvFishPondDesc.text = "$job · " + TimeUtils.getFriendlyTimeSpanByNow(item.createTime, mSdf)
                tvFishPondContent.setTextIsSelectable(false)
                tvFishPondContent.apply {
                    if (expandContent) {
                        maxLines = Int.MAX_VALUE
                        ellipsize = null
                    } else {
                        maxLines = 5
                        ellipsize = TextUtils.TruncateAt.END
                    }
                }
                val content = item.content
                // 设置默认表情符号解析器
                tvFishPondContent.setDefaultEmojiParser()
                tvFishPondContent.text = content.parseAsHtml(imageGetter = EmojiImageGetter(tvFishPondContent.textSize.toInt()))
                val topicName = item.topicName
                val images = item.images
                val imageCount = images.size
                simpleGridLayout.setOnNineGridClickListener(nineGridAdapterDelegate)
                    .setData(images)
                simpleGridLayout.isVisible = imageCount != 0
                tvFishPondLabel.isVisible = TextUtils.isEmpty(topicName).not()
                tvFishPondLabel.text = topicName
                val linkUrl = item.linkUrl
                val hasLink = TextUtils.isEmpty(linkUrl).not()
                val hasLinkCover = TextUtils.isEmpty(item.linkCover).not()
                val linkCover = if (hasLinkCover) item.linkCover
                else R.mipmap.ic_link_default
                llLinkContainer.isVisible = hasLink
                llLinkContainer.setFixOnClickListener {
                    BrowserActivity.start(context, linkUrl)
                }
                Glide.with(context)
                    .load(linkCover)
                    .placeholder(R.mipmap.ic_link_default)
                    .error(R.mipmap.ic_link_default)
                    .transform(RoundedCorners(3.dp))
                    .into(ivLinkCover)
                tvLinkTitle.text = item.linkTitle
                tvLinkUrl.text = linkUrl
                val currUserId = UserManager.loadCurrUserId()
                val like = currUserId in item.thumbUpList
                val defaultColor = ContextCompat.getColor(context, R.color.menu_default_font_color)
                val likeColor = ContextCompat.getColor(context, R.color.menu_like_font_color)
                with(listMenuItem) {
                    llShare.setFixOnClickListener { mMenuItemClickListener.invoke(it, item, bindingAdapterPosition) }
                    llGreat.setFixOnClickListener { mMenuItemClickListener.invoke(it, item, bindingAdapterPosition) }
                    ivGreat.imageTintList = ColorStateList.valueOf((if (like) likeColor else defaultColor))
                    tvComment.text = with(item.commentCount) {
                        if (this == 0) {
                            "评论"
                        } else {
                            toString()
                        }
                    }
                    tvGreat.text = with(item.thumbUpList.size) {
                        if (this == 0) "点赞" else toString()
                    }
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: FishListViewHolder) {
        super.onViewAttachedToWindow(holder)
        adapterDelegate.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: RosterListAdapter.FishListViewHolder, position: Int) {
        holder.itemView.setFixOnClickListener { adapterDelegate.onItemClick(it, holder.bindingAdapterPosition) }
        holder.onBinding(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FishListViewHolder = FishListViewHolder(parent)

    fun setOnNineGridClickListener(block: (sources: List<String>, index: Int) -> Unit) {
        nineGridAdapterDelegate.setOnNineGridItemClickListener(block)
    }

    companion object {

        private val diffCallback =
            itemDiffCallback<Fish.FishItem>({ oldItem, newItem -> oldItem.id == newItem.id }) { oldItem, newItem -> oldItem == newItem }
    }
}