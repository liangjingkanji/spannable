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

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.style.ReplacementSpan
import androidx.annotation.IntRange

class MarginSpan @JvmOverloads constructor(
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