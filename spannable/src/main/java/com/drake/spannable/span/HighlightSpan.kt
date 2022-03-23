/*
 * Copyright (C) 2018 Drake, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.drake.spannable.span

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * 创建字体颜色/字体样式/可点击效果
 * @param color 字体颜色
 * @param typeface 字体样式
 * @param onClick 点击事件
 */
class HighlightSpan(
    @ColorInt val color: Int? = null,
    val typeface: Typeface? = null,
    val onClick: ((View) -> Unit)? = null
) : ClickableSpan() {

    /**
     * 创建字体颜色/字体样式/可点击效果
     * @param color 字体颜色
     * @param typeface 字体样式
     * @param onClick 点击事件
     */
    constructor(
        color: String? = null,
        typeface: Typeface? = null,
        onClick: ((View) -> Unit)? = null
    ) : this(Color.parseColor(color), typeface, onClick)

    /**
     * 创建字体颜色/字体样式/可点击效果
     * @param colorRes 字体颜色
     * @param typeface 字体样式
     * @param onClick 点击事件
     */
    constructor(
        context: Context,
        @ColorRes colorRes: Int,
        typeface: Typeface? = null,
        onClick: ((View) -> Unit)? = null
    ) : this(ContextCompat.getColor(context, colorRes), typeface, onClick)

    override fun updateDrawState(ds: TextPaint) {
        color?.let(ds::setColor)
        typeface?.let(ds::setTypeface)
    }

    override fun onClick(widget: View) {
        onClick?.invoke(widget)
    }
}