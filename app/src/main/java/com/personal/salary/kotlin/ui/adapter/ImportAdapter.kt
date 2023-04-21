package com.personal.salary.kotlin.ui.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.dylanc.longan.pxToDp
import com.hjq.widget.view.DrawableTextView
import com.personal.salary.kotlin.R
import com.personal.salary.kotlin.app.AppAdapter
import com.personal.salary.kotlin.ktx.dp
import com.personal.salary.kotlin.model.ImportRule
import com.xiaomi.push.it

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/09/22
 *    desc   : 状态数据列表
 */
class ImportAdapter constructor(context: Context) : AppAdapter<ImportRule.ImportType?>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder()
    }

    inner class ViewHolder : AppViewHolder(R.layout.status_item) {

        private val textView: DrawableTextView? by lazy { findViewById(R.id.tv_status_text) }

        override fun onBindView(position: Int) {
            textView?.apply {
                text = getItem(position)?.itemName

            }
        }
    }
}