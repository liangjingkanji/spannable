/*
 * MIT License
 *
 * Copyright (c) 2023 劉強東 https://github.com/liangjingkanji
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.drake.spannable.span

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.drake.spannable.movement.ClickableMovementMethod

/**
 * 创建字体颜色/字体样式/可点击效果
 * @param color 字体颜色
 * @param typeface 字体样式
 * @param onClick 点击事件 要求设置[ClickableMovementMethod]或者[LinkMovementMethod], 否则点击事件是无效的, 此为Android官方限制
 */
class HighlightSpan @JvmOverloads constructor(
    @ColorInt val color: Int? = null,
    val typeface: Typeface? = null,
    val onClick: ((View) -> Unit)? = null
) : ClickableSpan() {

    /**
     * 创建字体颜色/字体样式/可点击效果
     * @param color 字体颜色
     * @param typeface 字体样式
     * @param onClick 点击事件 要求设置[ClickableMovementMethod]或者[LinkMovementMethod], 否则点击事件是无效的, 此为Android官方限制
     */
    @JvmOverloads
    constructor(
        color: String,
        typeface: Typeface? = null,
        onClick: ((View) -> Unit)? = null
    ) : this(Color.parseColor(color), typeface, onClick)

    /**
     * 创建字体颜色/字体样式/可点击效果
     * @param colorRes 字体颜色
     * @param typeface 字体样式
     * @param onClick 点击事件 要求设置[ClickableMovementMethod]或者[LinkMovementMethod], 否则点击事件是无效的, 此为Android官方限制
     */
    @JvmOverloads
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