package com.drake.spannable.span

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.style.ImageSpan
import com.drake.spannable.span.GlideImageSpan.Align
import java.lang.ref.WeakReference

/**
 * 图片完全垂直居中对齐文字(ImageSpan无法真正垂直居中)
 * 垂直居中文字不要求Api23以上
 * 设置图片宽高且保持固定比例
 *
 * 图片默认垂直居中对齐文字
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

    private var drawableRef: WeakReference<Drawable>? = null

    override fun getDrawable(): Drawable {
        return drawableRef?.get() ?: super.getDrawable().apply {
            val ratio = intrinsicWidth.toDouble() / intrinsicHeight.toDouble()
            drawableWidth = if (drawableWidth > 0) drawableWidth else intrinsicWidth
            drawableHeight = if (drawableHeight > 0) drawableHeight else intrinsicHeight
            if (drawableWidth > drawableHeight) {
                drawableWidth = (drawableHeight * ratio).toInt()
            } else if (drawableWidth < drawableHeight) {
                drawableHeight = (drawableWidth / ratio).toInt()
            }
            setBounds(0, 0, drawableWidth, drawableHeight)
            drawableRef = WeakReference(this)
        }
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
                    fm.ascent = fontMetricsInt.ascent - (imageHeight - fontHeight + (fontMetricsInt.descent / 2))
                    fm.descent = 0
                }
                Align.BOTTOM -> {
                    fm.ascent = fontMetricsInt.ascent - (imageHeight - fontHeight)
                    fm.descent = 0
                }
            }
            fm.top = fm.ascent
            fm.bottom = fm.descent
        }
        return bounds.right
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
        var transY = bottom - drawable.bounds.bottom
        val fontMetricsInt = paint.fontMetricsInt
        if (align == Align.BASELINE) {
            transY -= fontMetricsInt.descent
        } else if (align == Align.CENTER) {
            transY = (bottom - top) / 2 - drawable.bounds.height() / 2
        }
        canvas.translate(x, transY.toFloat())
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
     * 如果参数值为0则表示使用图片原始宽高
     */
    fun setDrawableSize(width: Int, height: Int = width) = apply {
        this.drawableWidth = width
        this.drawableHeight = height
        drawableRef?.clear()
    }

}