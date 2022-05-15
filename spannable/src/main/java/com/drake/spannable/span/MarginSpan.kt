package com.drake.spannable.span

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.style.ReplacementSpan
import androidx.annotation.IntRange

class MarginSpan(
    private val width: Int,
    color: Int = Color.TRANSPARENT
) : ReplacementSpan() {

    private val paint = Paint()

    init {
        paint.color = color
        paint.style = Paint.Style.FILL
    }

    override fun getSize(
        paint: Paint, text: CharSequence,
        @IntRange(from = 0) start: Int,
        @IntRange(from = 0) end: Int,
        fm: FontMetricsInt?
    ): Int {
        return width
    }

    override fun draw(
        canvas: Canvas, text: CharSequence,
        @IntRange(from = 0) start: Int,
        @IntRange(from = 0) end: Int,
        x: Float, top: Int, y: Int, bottom: Int,
        paint: Paint
    ) {
        canvas.drawRect(x, top.toFloat(), x + width, bottom.toFloat(), this.paint)
    }
}