package com.drake.spannable.span

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.style.ImageSpan

/** 居中显示图片 */
class CenterImageSpan : ImageSpan {

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
            val imageHeight = drawable.bounds.height()
            it.ascent = paint.fontMetricsInt.ascent - ((imageHeight - fontHeight) / 2.0f).toInt()
            it.top = it.ascent
            it.descent = it.ascent + imageHeight
            it.bottom = it.descent
        }
        return drawable.bounds.right
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int, // line top,It is the largest top in line.
        y: Int, // baseline y position
        bottom: Int, // line bottom,contain lineSpacingExtra if there is more than one line.
        paint: Paint
    ) {
        val fontHeight = paint.fontMetricsInt.descent - paint.fontMetricsInt.ascent
        val imageAscent = paint.fontMetricsInt.ascent - ((drawable.bounds.height() - fontHeight) / 2.0f).toInt()
        canvas.save()
        canvas.translate(x, (y + imageAscent).toFloat())
        drawable.draw(canvas)
        canvas.restore()
    }
}