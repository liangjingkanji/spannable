package com.drake.spannable.span

import android.content.Context
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorRes

class ColorSpan(color: Int) : ForegroundColorSpan(color) {
    constructor(color: String) : this(Color.parseColor(color))
    constructor(
        context: Context,
        @ColorRes colorRes: Int
    ) : this(context.resources.getColor(colorRes))
}