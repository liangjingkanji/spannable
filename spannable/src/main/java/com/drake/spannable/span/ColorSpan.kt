package com.drake.spannable.span

import android.content.Context
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorRes

/**
 * 快速创建[ForegroundColorSpan]
 * @param color 字体颜色
 */
class ColorSpan(color: Int) : ForegroundColorSpan(color) {
    constructor(color: String) : this(Color.parseColor(color))

    /**
     * 快速创建[ForegroundColorSpan]
     * @param colorRes 字体颜色
     */
    constructor(
        context: Context,
        @ColorRes colorRes: Int
    ) : this(context.resources.getColor(colorRes))
}