package com.drake.spannable.span

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.style.ImageSpan
import com.drake.spannable.model.drawableSize
import java.lang.ref.WeakReference

/** 居中显示图片 */
class CenterImageSpan : ImageSpan {

    private var drawableRef: WeakReference<Drawable>? = null
    private val drawableCache: Drawable
        get() = drawableRef?.get() ?: drawable.apply {
            drawableSize(
                if (width == DRAWABLE_AUTO_SIZE) intrinsicWidth else width,
                if (height == DRAWABLE_AUTO_SIZE) intrinsicHeight else height
            )
            drawableRef = WeakReference(this)
        }

    var width: Int = DRAWABLE_AUTO_SIZE
        set(value) {
            clearDrawableCache()
            field = value
        }

    var height: Int = DRAWABLE_AUTO_SIZE
        set(value) {
            clearDrawableCache()
            field = value
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
        canvas: Canvas, text: CharSequence?, start: Int, end: Int,
        x: Float, top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        val fontHeight = paint.fontMetricsInt.descent - paint.fontMetricsInt.ascent
        val imageAscent = paint.fontMetricsInt.ascent - ((drawableCache.bounds.height() - fontHeight) / 2.0f).toInt()
        canvas.save()
        canvas.translate(x, (y + imageAscent).toFloat())
        drawableCache.draw(canvas)
        canvas.restore()
    }

    fun clearDrawableCache() {
        drawableRef?.clear()
    }

    companion object {
        const val DRAWABLE_AUTO_SIZE = -1
    }
}