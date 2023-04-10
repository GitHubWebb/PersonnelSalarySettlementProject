package com.personal.salary.kotlin.util

import android.graphics.Rect
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.Px
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.personal.salary.kotlin.ktx.dp

/**
 * @ApplicationName: oa-android
 * @Description: RecyclerView 间距装饰（线性布局管理器）
 * @author: anjiemo
 * @date: 2021/8/4 14:42
 * @version: V1.0.0
 */
open class SimpleLinearSpaceItemDecoration(
    // 单位间距（实际间距的一半）
    @Px private val unit: Int = 2.dp
) : RecyclerView.ItemDecoration() {

    @CallSuper
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val layoutManager = (parent.layoutManager as? LinearLayoutManager) ?: return
        // 获取 LinearLayoutManager 的布局方向
        val orientation = layoutManager.orientation
        if (orientation == RecyclerView.VERTICAL) {
            outRect.bottom = unit
        } else {
            outRect.right = unit
        }
    }
}