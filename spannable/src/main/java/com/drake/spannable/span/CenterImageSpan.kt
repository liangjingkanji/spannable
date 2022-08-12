/*
 * Copyright (C) 2018 Drake, https://github.com/liangjingkanji
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
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextPaint
import android.text.style.ImageSpan
import android.view.Gravity
import androidx.annotation.ColorInt
import java.lang.ref.WeakReference

/**
 * 比官方[ImageSpan]更好用的图片显示Span
 *
 * 图片垂直对齐方式
 * 图片宽高且保持固定比例
 * 图片水平间距
 * 图片显示文字
 *
 * 默认图片垂直居中对齐文字, 使用[setAlign]可指定
 *
 * 需应对更复杂的图片加载需求请使用[GlideImageSpan]
 */
class CenterImageSpan : ImageSpan {

    /** 图片宽度 */
    var drawableWidth: Int = 0
        private set

    /** 图片高度 */
    var drawableHeight: Int = 0
        private set

    /** 图片左间距 */
    var marginLeft: Int = 0
        private set

    /** 图片右间距 */
    var marginRight: Int = 0
        private set

    private var drawableRef: WeakReference<Drawable>? = null

    override fun getDrawable(): Drawable {
        return drawableRef?.get() ?: super.getDrawable().apply {
            setFixedRatioZoom()
            drawableRef = WeakReference(this)
        }
    }

    /** 设置等比例缩放图片, 这会导致[drawableWidth]和[drawableHeight]根据图片原始比例变化 */
    private fun Drawable.setFixedRatioZoom() {
        val ratio = intrinsicWidth.toDouble() / intrinsicHeight
        drawableWidth = if (drawableWidth > 0) drawableWidth else intrinsicWidth
        drawableHeight = if (drawableHeight > 0) drawableHeight else intrinsicHeight
        if (intrinsicWidth > intrinsicHeight) {
            drawableHeight = (drawableWidth / ratio).toInt()
        } else if (intrinsicWidth < intrinsicHeight) {
            drawableWidth = (drawableHeight * ratio).toInt()
        }
        setBounds(0, 0, drawableWidth, drawableHeight)
    }

    constructor(drawable: Drawable) : super(drawable)
    constructor(drawable: Drawable, source: String) : super(drawable, source)
    constructor(context: Context, uri: Uri) : super(context, uri)
    constructor(context: Context, resourceId: Int) : super(context, resourceId)
    constructor(context: Context, bitmap: Bitmap) : super(context, bitmap)

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val bounds = drawable.bounds
        if (fm != null) {
            val fontMetricsInt = paint.fontMetricsInt
            val fontHeight = fontMetricsInt.descent - fontMetricsInt.ascent
            val imageHeight = bounds.height()
            when (align) {
                Align.CENTER -> {
                    fm.ascent = fontMetricsInt.ascent - ((imageHeight - fontHeight) / 2.0f).toInt()
                    fm.descent = fm.ascent + imageHeight
                }
                Align.BASELINE -> {
                    fm.ascent = -bounds.bottom
                    fm.descent = 0
                }
                Align.BOTTOM -> {
                    fm.ascent = -bounds.bottom + fm.descent
                    fm.descent = 0
                }
            }
            fm.top = fm.ascent
            fm.bottom = fm.descent
        }
        return bounds.right + marginLeft + marginRight
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        canvas.save()
        val bounds = drawable.bounds
        var transY = bottom - bounds.bottom
        if (align == Align.BASELINE) {
            transY -= paint.fontMetricsInt.descent
        } else if (align == Align.CENTER) {
            transY -= (bottom - top) / 2 - (bounds.bottom - bounds.top) / 2
        }
        canvas.translate(x + marginLeft, transY.toFloat())
        drawable.draw(canvas)

        // 绘制文字
        if (textVisibility) {
            textSize?.let { paint.textSize = it.toFloat() }
            textColor?.let { paint.color = it }
            typeface?.let { paint.typeface = it }
            (paint as? TextPaint)?.let { paintConfig?.invoke(it) }
            val textWidth = paint.measureText(text, start, end)
            val textDrawRect = Rect()
            val textContainerRect = Rect(bounds)
            Gravity.apply(
                textGravity,
                textWidth.toInt(),
                paint.textSize.toInt(),
                textContainerRect,
                textDrawRect
            )
            canvas.drawText(
                text, start, end,
                (textDrawRect.left + textOffsetRect.left - textOffsetRect.right).toFloat(),
                (textDrawRect.bottom - (paint.fontMetricsInt.descent / 2) + textOffsetRect.top - textOffsetRect.bottom).toFloat(),
                paint
            )
        }
        canvas.restore()
    }


    enum class Align {
        BASELINE,
        CENTER,
        BOTTOM
    }

    private var align: Align = Align.CENTER

    /**
     * 设置图片垂直对其方式
     * 图片默认垂直居中对齐文字: [Align.CENTER]
     */
    fun setAlign(align: Align) = apply {
        this.align = align
    }

    /**
     * 设置图片宽高
     * 如果参数值为0则表示使用图片原始宽高, 无论宽高值如何图片都将会按照固定比例缩放, 你无需但需错误值导致图片拉伸变形
     */
    @JvmOverloads
    fun setDrawableSize(width: Int, height: Int = width) = apply {
        this.drawableWidth = width
        this.drawableHeight = height
        drawableRef?.clear()
    }

    /** 设置图片水平间距 */
    @JvmOverloads
    fun setMarginHorizontal(left: Int, right: Int = left) = apply {
        this.marginLeft = left
        this.marginRight = right
        drawableRef?.clear()
    }

    //<editor-fold desc="Text">
    private var textOffsetRect = Rect()
    private var textGravity = Gravity.CENTER
    private var textVisibility = false
    private var textSize: Int? = null
    private var textColor: Int? = null
    private var typeface: Typeface? = null
    private var paintConfig: (TextPaint.() -> Unit)? = null

    /**
     * 当前为背景图片, 这会导致显示文字内容, 但图片不会根据文字内容自动调整
     * @param visibility 是否显示文字
     */
    @JvmOverloads
    fun setTextVisibility(visibility: Boolean = true) = apply {
        this.textVisibility = visibility
    }

    /** 文字偏移值 */
    @JvmOverloads
    fun setTextOffset(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) = apply {
        textOffsetRect.set(left, top, right, bottom)
    }

    /**
     * 文字对齐方式(基于图片), 默认对齐方式[Gravity.CENTER]
     * @param gravity 值等效于[android.widget.TextView.setGravity], 例如[Gravity.BOTTOM], 使用[or]组合多个值
     */
    fun setTextGravity(gravity: Int) = apply {
        this.textGravity = gravity
    }

    /** 配置文字画笔, 可以配置颜色/粗体/斜体等效果 */
    fun setTextPaint(paint: TextPaint.() -> Unit) = apply {
        paintConfig = paint
    }

    /**
     * 设置文字样式, 例如[android.graphics.Typeface.BOLD]粗体
     */
    fun setTypeface(typeface: Typeface) = apply {
        this.typeface = typeface
    }

    fun setTextSize(size: Int) = apply {
        textSize = size
    }

    fun setTextColor(color: String) = apply {
        textColor = Color.parseColor(color)
    }

    fun setTextColor(@ColorInt color: Int) = apply {
        textColor = color
    }
    //</editor-fold>

}