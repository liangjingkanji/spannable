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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.net.Uri
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.view.Gravity
import androidx.core.text.getSpans
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
    private var drawableWidth: Int = 0

    /** 图片高度 */
    private var drawableHeight: Int = 0

    /** 图片左间距 */
    private var marginLeft: Int = 0

    /** 图片右间距 */
    private var marginRight: Int = 0

    /** 文字总高度（包括上下空白区域） */
    private var paintMetricsHeight=-1

    private var drawableRef: WeakReference<Drawable>? = null

    /** 文字显示区域 */
    private var textDisplayRect = Rect()

    /** 图片原始间距 */
    private var drawableOriginPadding = Rect()

    override fun getDrawable(): Drawable {
        return drawableRef?.get() ?: super.getDrawable().apply {
            setFixedRatioZoom()
            drawableRef = WeakReference(this)
        }
    }

    /** 设置等比例缩放图片 */
    private fun Drawable.setFixedRatioZoom() {
        val ratio = intrinsicWidth.toDouble() / intrinsicHeight
        var width = when {
            drawableWidth > 0 -> drawableWidth
            drawableWidth == -1 -> textDisplayRect.width()
            else -> intrinsicWidth
        }
        var height = when {
            drawableHeight > 0 -> drawableHeight
            drawableWidth == -1 -> textDisplayRect.height()
            else -> intrinsicHeight
        }

        if (drawableWidth != -1 && intrinsicWidth > intrinsicHeight) {
            height = (width / ratio).toInt()
        } else if (drawableHeight != -1 && intrinsicWidth < intrinsicHeight) {
            width = (height * ratio).toInt()
        }

        getPadding(drawableOriginPadding)
        width += drawablePadding.left + drawablePadding.right + drawableOriginPadding.left + drawableOriginPadding.right
        height += drawablePadding.top + drawablePadding.bottom + drawableOriginPadding.top + drawableOriginPadding.bottom


        //点九适配
        var bottom=height
        if(this is NinePatchDrawable){
            bottom= paintMetricsHeight + drawablePadding.top + drawablePadding.bottom +
                    drawablePadding.top + drawablePadding.bottom +
                    drawableOriginPadding.top + drawableOriginPadding.bottom
        }

        bounds.set(0, 0, width, bottom)
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
        if (drawableWidth == -1 || drawableHeight == -1) {
            paint.getTextBounds(text.toString(), start, end, textDisplayRect)
        }
        paintMetricsHeight=paint.fontMetricsInt.bottom-paint.fontMetricsInt.top
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

            fm.top = fm.ascent - drawableOriginPadding.top
            fm.bottom = fm.descent + drawableOriginPadding.bottom
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
        if (drawableWidth == -1 || drawableHeight == -1) {
            paint.getTextBounds(text.toString(), start, end, textDisplayRect)
        }
        canvas.save()
        val drawable = drawable
        val bounds = drawable.bounds
        var transY = bottom - bounds.bottom
        if (align == Align.BASELINE) {
            transY -= paint.fontMetricsInt.descent
        } else if (align == Align.CENTER) {
            transY -= (bottom - top) / 2 - (bounds.bottom - bounds.top) / 2
        }
        canvas.translate(x + marginLeft, transY.toFloat())
        drawable.draw(canvas)
        canvas.translate(-drawablePadding.width() / 2f - drawableOriginPadding.right, transY.toFloat() - drawablePadding.height() / 2f)

        // draw text
        if (textVisibility) {
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
            if (text is Spanned) {
                // draw text color
                text.getSpans<ForegroundColorSpan>(start, end).lastOrNull()?.let {
                    paint.color = it.foregroundColor
                }
            }

            canvas.drawText(
                text, start, end,
                (textDrawRect.left + textOffset.left - textOffset.right).toFloat()+(drawableOriginPadding.right+drawableOriginPadding.left)/2,
                (textDrawRect.bottom - (paint.fontMetricsInt.descent / 2) + textOffset.top - textOffset.bottom).toFloat()-(drawableOriginPadding.bottom+drawableOriginPadding.top)/2,
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

    //<editor-fold desc="Image">
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
     * @param  width 指定图片宽度, -1 表示根据文字宽度自动设置图片宽度
     * @param  height 指定图片高度, -1 表示根据文字高度自动设置图片宽度
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

    private var drawablePadding = Rect()

    /**
     * 设置图片内间距
     */
    fun setPadding(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) = apply {
        drawablePadding.set(left, top, right, bottom)
    }
    //</editor-fold>

    //<editor-fold desc="Text">
    private var textOffset = Rect()
    private var textGravity = Gravity.CENTER
    private var textVisibility = false

    /**
     * 当前为背景图片, 这会导致显示文字内容, 但图片不会根据文字内容自动调整
     * @param visibility 是否显示文字
     */
    @JvmOverloads
    fun setTextVisibility(visibility: Boolean = true) = apply {
        this.textVisibility = visibility
    }

    /**
     * 文字偏移值
     */
    @JvmOverloads
    fun setTextOffset(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) = apply {
        textOffset.set(left, top, right, bottom)
    }

    /**
     * 文字对齐方式(基于图片), 默认对齐方式[Gravity.CENTER]
     * @param gravity 值等效于[android.widget.TextView.setGravity], 例如[Gravity.BOTTOM], 使用[or]组合多个值
     */
    fun setTextGravity(gravity: Int) = apply {
        this.textGravity = gravity
    }
    //</editor-fold>

}