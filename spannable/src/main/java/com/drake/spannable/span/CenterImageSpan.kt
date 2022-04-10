package com.drake.spannable.span

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.style.ImageSpan
import java.lang.ref.WeakReference

/**
 * 比官方[ImageSpan]更好用的图片显示Span
 *
 * 设置图片垂直对齐方式
 * 设置图片宽高且保持固定比例
 * 设置图片水平间距
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
        } else if (intrinsicHeight > intrinsicWidth) {
            drawableWidth = (drawableHeight / ratio).toInt()
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
        text: CharSequence?,
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
        text: CharSequence?,
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
    fun setDrawableSize(width: Int, height: Int = width) = apply {
        this.drawableWidth = width
        this.drawableHeight = height
        drawableRef?.clear()
    }

    /** 设置图片水平间距 */
    fun setMarginHorizontal(left: Int, right: Int = left) = apply {
        this.marginLeft = left
        this.marginRight = right
        drawableRef?.clear()
    }

}