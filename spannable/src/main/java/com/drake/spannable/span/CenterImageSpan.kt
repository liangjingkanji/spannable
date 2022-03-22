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
 * 居中文字显示图片
 * @see setDrawableSize 设置图片尺寸
 */
class CenterImageSpan : ImageSpan {

    private var drawableRef: WeakReference<Drawable>? = null
    private val drawableCache: Drawable
        get() = drawableRef?.get() ?: drawable.apply {
            setBounds(
                0, 0,
                if (drawableWidth == -1) intrinsicWidth else drawableWidth,
                if (drawableHeight == -1) intrinsicHeight else drawableHeight
            )
            drawableRef = WeakReference(this)
        }

    /**
     * -1 等于 [Drawable.getIntrinsicWidth], 即图片原始宽高
     * @see [setDrawableSize]
     */
    var drawableWidth: Int = -1
        private set

    /**
     * -1 等于 [Drawable.getIntrinsicHeight], 即图片原始宽高
     * @see [setDrawableSize]
     */
    var drawableHeight: Int = -1
        private set

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
        fm?.let {
            val fontHeight = paint.fontMetricsInt.descent - paint.fontMetricsInt.ascent
            val imageHeight = drawableCache.bounds.height()
            it.ascent = paint.fontMetricsInt.ascent - ((imageHeight - fontHeight) / 2.0f).toInt()
            it.top = it.ascent
            it.descent = it.ascent + imageHeight
            it.bottom = it.descent
        }
        return drawableCache.bounds.right
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
        val fontHeight = paint.fontMetricsInt.descent - paint.fontMetricsInt.ascent
        val imageAscent = paint.fontMetricsInt.ascent - ((drawableCache.bounds.height() - fontHeight) / 2.0f).toInt()
        canvas.save()
        canvas.translate(x, (y + imageAscent).toFloat())
        drawableCache.draw(canvas)
        canvas.restore()
    }

    /**
     * 设置图片宽高
     */
    fun setDrawableSize(width: Int, height: Int = width): CenterImageSpan = apply {
        this.drawableWidth = width
        this.drawableHeight = height
        drawableRef?.clear()
    }

}