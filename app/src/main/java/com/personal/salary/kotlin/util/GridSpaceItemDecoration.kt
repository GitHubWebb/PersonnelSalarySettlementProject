package com.personal.salary.kotlin.util

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView
import com.personal.salary.kotlin.ktx.dp
import com.personal.salary.kotlin.ktx.equilibriumAssignmentOfGrid

/**
 * @ApplicationName: oa-android
 * @Description: RecyclerView 间距装饰（网格布局管理器）
 * @author: anjiemo
 * @date: 2021/8/4 14:38
 * @version: V1.0.0
 */
class GridSpaceItemDecoration(
    // 单位间距（实际间距的一半）
    @Px private val unit: Int = 2.dp
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        equilibriumAssignmentOfGrid(unit, outRect, view, parent)
    }
}