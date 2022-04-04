package com.drake.spannable.span

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.style.ImageSpan
import java.lang.ref.WeakReference

/**
 * 解决ImageSpan的垂直居中对齐要求api23以上
 * 相对ImageSpan可以控制图片宽高固定值
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
            fm.ascent = fontMetricsInt.ascent - ((imageHeight - fontHeight) / 2.0f).toInt()
            fm.top = fm.ascent
            fm.descent = fm.ascent + imageHeight
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
     */
    fun setAlign(align: Align) = apply {
        this.align = align
    }

    /**
     * 设置图片宽高
     * 如果参数值为0则表示使用图片原始宽高
     */
    fun setDrawableSize(width: Int, height: Int = width, dp: Boolean = false) = apply {
        this.drawableWidth = if (dp) dp2px(width) else width
        this.drawableHeight = if (dp) dp2px(height) else height
        drawableRef?.clear()
    }

    private fun dp2px(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

}