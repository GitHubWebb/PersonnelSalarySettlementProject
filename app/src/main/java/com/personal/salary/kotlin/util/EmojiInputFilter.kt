package com.personal.salary.kotlin.util

import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ImageSpan
import com.personal.salary.kotlin.app.AppApplication
import com.personal.salary.kotlin.util.EmojiMapHelper.getEmojiValue
import java.util.regex.Pattern

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2022/03/25
 * desc   : emoji 文字表情过滤器 [表情]
 */
class EmojiInputFilter : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        val sourceText = source.toString()
        // 新输入的字符串为空（删除剪切等）
        if (TextUtils.isEmpty(source)) {
            return ""
        }
        val emojiMap = EmojiMapHelper.emojiMap
        val ssb = SpannableStringBuilder(source)
        for ((emoji) in emojiMap) {
            replaceEmojiTextByImg(emoji, sourceText, ssb)
        }
        return ssb
    }

    private fun replaceEmojiTextByImg(
        emoji: String?,
        sourceText: String?,
        ssb: SpannableStringBuilder
    ) {
        emoji ?: return
        sourceText ?: return
        val pattern = Pattern.compile(emoji)
        val matcher = pattern.matcher(sourceText)
        var lastIndex = 0
        while (matcher.find()) {
            val startIndex = sourceText.indexOf(emoji, lastIndex)
            if (startIndex == -1) return
            val resId = getEmojiValue(emoji)
            val imageSpan = ImageSpan(AppApplication.getInstance(), resId)
            lastIndex = startIndex + emoji.length
            ssb.setSpan(imageSpan, startIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            // Timber.d("filter：===> startIndex is %s lastIndex is %s", startIndex, lastIndex);
        }
    }
}