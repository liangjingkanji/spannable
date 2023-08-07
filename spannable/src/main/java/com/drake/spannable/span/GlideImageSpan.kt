/*
 * MIT License
 *
 * Copyright (c) 2023 劉強東 https://github.com/liangjingkanji
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.drake.spannable.span

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ReplacementSpan
import android.view.Gravity
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.max

/**
 * 使用Glide加载图片资源, 请先依赖[Glide](https://github.com/bumptech/glide)
 *
 * 图片垂直对齐方式
 * 图片宽高且保持固定比例, 如果存在占位图会优先使用占位图宽高比
 * 图片水平间距
 * 图片显示文字
 * shape自适应文字
 * 播放GIF动画
 *
 * 默认图片垂直居中对齐文字, 使用[setAlign]可指定
 *
 * @see CenterImageSpan 如果你不需要加载网络图片可以使用该类
 */
class GlideImageSpan(val view: TextView, val url: Any) : ReplacementSpan() {

    /** gif循环次数 */
    private var loopCount: Int = GifDrawable.LOOP_FOREVER

    /** 图片宽度 */
    private var drawableWidth: Int = 0

    /** 图片高度 */
    private var drawableHeight: Int = 0

    /** 图片间距 */
    private var drawableMargin: Rect = Rect()

    /** 图片内间距 */
    private var drawablePadding = Rect()

    private var requestOption: RequestOptions = RequestOptions()

    private var drawableRef: AtomicReference<Drawable> = AtomicReference()

    /** 文字显示区域 */
    private var textDisplayRect = Rect()

    /** 图片原始间距 */
    private var drawableOriginPadding = Rect()

    /** 初始固定图片显示区域, 优先级: 自定义尺寸 > 占位图尺寸 > 文字尺寸 */
    private var fixDrawableBounds = Rect()

    private var request: Request? = null

    /** 占位图 */
    private val placeHolder: Drawable? by lazy {
        val placeHolder = try {
            requestOption.placeholderDrawable ?: view.context.resources.getDrawable(
                requestOption.placeholderId
            )
        } catch (e: Exception) {
            null
        }
        placeHolder?.setFixedRatioZoom()
        placeHolder
    }

    private fun getDrawable(): Drawable? {
        val request = request
        if (drawableRef.get() == null && (request == null || request.isComplete)) {
            val drawableSize = getDrawableSize()
            this.request = Glide.with(view).load(url).apply(requestOption).into(object : CustomTarget<Drawable>(drawableSize.width(), drawableSize.height()) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    if (resource is GifDrawable) {
                        resource.callback = drawableCallback
                        resource.setLoopCount(loopCount)
                        resource.start()
                    }
                    if (fixDrawableBounds.isEmpty) {
                        fixDrawableBounds = getDrawableSize()
                    }
                    resource.bounds = fixDrawableBounds
                    drawableRef.set(resource)
                    view.invalidate()
                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    if (placeholder != null) {
                        placeholder.setFixedRatioZoom()
                        drawableRef.set(placeholder)
                    }
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    if (errorDrawable != null && errorDrawable != drawableRef.get()) {
                        errorDrawable.setFixedRatioZoom()
                        drawableRef.set(errorDrawable)
                        view.invalidate()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            }).request
        }
        return drawableRef.get()
    }

    /** 设置等比例缩放图片 */
    private fun Drawable.setFixedRatioZoom() {
        var width = when {
            drawableWidth > 0 -> drawableWidth
            drawableWidth == -1 -> textDisplayRect.width()
            else -> intrinsicWidth
        }
        var height = when {
            drawableHeight > 0 -> drawableHeight
            drawableHeight == -1 -> textDisplayRect.height()
            else -> intrinsicHeight
        }

        getPadding(drawableOriginPadding)
        width += drawablePadding.left + drawablePadding.right + drawableOriginPadding.left + drawableOriginPadding.right
        height += drawablePadding.top + drawablePadding.bottom + drawableOriginPadding.top + drawableOriginPadding.bottom

        if (this is NinePatchDrawable) {
            width = max(width, intrinsicWidth)
            height = max(height, intrinsicHeight)
        }
        bounds.set(0, 0, width, height)
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        if (textSize > 0) {
            paint.textSize = textSize.toFloat()
        }
        val fontMetrics = paint.fontMetricsInt
        if (drawableWidth <= 0 || drawableHeight <= 0) {
            val r = Rect()
            paint.getTextBounds(text.toString(), start, end, r)
            val resizeFontMetrics = paint.fontMetricsInt
            textDisplayRect.set(0, 0, r.width(), resizeFontMetrics.descent - resizeFontMetrics.ascent)
        }
        val drawable = getDrawable()
        val bounds = drawable?.bounds ?: getDrawableSize()
        fixDrawableBounds = bounds
        val imageHeight = bounds.height()
        if (fm != null) {
            when (align) {
                Align.CENTER -> {
                    val fontHeight = fontMetrics.descent - fontMetrics.ascent
                    fm.ascent = fontMetrics.ascent - (imageHeight - fontHeight) / 2 - drawableMargin.top
                    fm.descent = fm.ascent + imageHeight + drawableMargin.bottom
                }

                Align.BASELINE -> {
                    fm.ascent = fontMetrics.bottom - imageHeight - fontMetrics.descent - drawableMargin.top - drawableMargin.bottom
                    fm.descent = 0
                }

                Align.BOTTOM -> {
                    fm.ascent = fontMetrics.descent - imageHeight - drawableMargin.top - drawableMargin.bottom
                    fm.descent = 0
                }
            }

            fm.top = fm.ascent
            fm.bottom = fm.descent
        }

        return bounds.right + drawableMargin.left + drawableMargin.right
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
        val drawable = getDrawable()
        canvas.save()
        val bounds = drawable?.bounds ?: getDrawableSize()
        val transY = when (align) {
            Align.CENTER -> (2 * y + paint.fontMetricsInt.ascent + paint.fontMetricsInt.descent) / 2 - bounds.bottom / 2 - drawableMargin.height() / 2
            Align.BASELINE -> bottom - bounds.bottom - paint.fontMetricsInt.descent - drawableMargin.bottom
            Align.BOTTOM -> bottom - bounds.bottom - drawableMargin.bottom
        }
        canvas.translate(x + drawableMargin.left, transY.toFloat())
        drawable?.draw(canvas)

        // draw text
        if (textVisibility) {
            canvas.translate(-drawablePadding.width() / 2F - drawableOriginPadding.right, -drawablePadding.height() / 2F + drawableOriginPadding.top)
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
                text.getSpans(start, end, ForegroundColorSpan::class.java).lastOrNull()?.let {
                    paint.color = it.foregroundColor
                }
            }
            canvas.drawText(
                text, start, end,
                (textDrawRect.left + textOffset.left - textOffset.right).toFloat() + (drawableOriginPadding.right + drawableOriginPadding.left) / 2,
                (textDrawRect.bottom - paint.fontMetricsInt.descent / 2 + textOffset.top - textOffset.bottom).toFloat() - (drawableOriginPadding.bottom + drawableOriginPadding.top) / 2,
                paint
            )
        }
        canvas.restore()
    }

    /**
     * 默认显示区域
     * 优先使用自定义尺寸, 如果没用配置则使用文字显示区域
     */
    private fun getDrawableSize(): Rect {
        val placeHolder = placeHolder
        var width = when {
            drawableWidth > 0 -> drawableWidth
            drawableWidth == -1 -> textDisplayRect.width()
            placeHolder != null -> placeHolder.intrinsicWidth
            else -> textDisplayRect.width()
        }
        var height = when {
            drawableHeight > 0 -> drawableHeight
            drawableHeight == -1 -> textDisplayRect.height()
            placeHolder != null -> placeHolder.intrinsicHeight
            else -> textDisplayRect.height()
        }
        if (width != placeHolder?.intrinsicWidth) {
            width += drawablePadding.left + drawablePadding.right + drawableOriginPadding.left + drawableOriginPadding.right
        }
        if (height != placeHolder?.intrinsicHeight) {
            height += drawablePadding.top + drawablePadding.bottom + drawableOriginPadding.top + drawableOriginPadding.bottom
        }
        return Rect(0, 0, width, height)
    }

    /** GIF动画触发刷新文字的回调 */
    private val drawableCallback = object : Drawable.Callback {
        override fun invalidateDrawable(who: Drawable) {
            view.invalidate()
        }

        override fun scheduleDrawable(
            who: Drawable,
            what: Runnable,
            `when`: Long
        ) {
        }

        override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        }
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
     * 如果指定大于零值则会基于图片宽高中最大值然后根据宽高比例固定缩放图片
     * @param  width 指定图片宽度, -1 使用文字宽度, 0 使用图片原始宽度
     * @param  height 指定图片高度, -1 使用文字高度, 0 使用图片原始高度
     */
    @JvmOverloads
    fun setDrawableSize(width: Int, height: Int = width) = apply {
        this.drawableWidth = width
        this.drawableHeight = height
        drawableRef.set(null)
    }

    /** 设置图片水平间距 */
    @JvmOverloads
    fun setMarginHorizontal(left: Int, right: Int = left) = apply {
        drawableMargin.left = left
        drawableMargin.right = right
    }

    /** 设置图片水平间距 */
    @JvmOverloads
    fun setMarginVertical(top: Int, bottom: Int = top) = apply {
        drawableMargin.top = top
        drawableMargin.bottom = bottom
    }

    /**
     * 设置图片水平内间距
     */
    @JvmOverloads
    fun setPaddingHorizontal(left: Int, right: Int = left) = apply {
        drawablePadding.left = left
        drawablePadding.right = right
        drawableRef.set(null)
    }

    /**
     * 设置图片垂直内间距
     */
    @JvmOverloads
    fun setPaddingVertical(top: Int, bottom: Int = top) = apply {
        drawablePadding.top = top
        drawablePadding.bottom = bottom
        drawableRef.set(null)
    }

    /**
     * 配置Glide请求选项, 例如占位图、加载失败图等
     * 如果使用[RequestOptions.placeholder]占位图会导致默认使用占位图宽高, 除非你使用[setDrawableSize]覆盖默认值
     *
     * 默认会使用[RequestOptions.fitCenterTransform]图片会拉伸显示, 当然你可以覆盖该配置, 比如使用[RequestOptions.centerCropTransform]裁剪显示
     */
    fun setRequestOption(requestOption: RequestOptions) = apply {
        this.requestOption = requestOption
    }

    /** GIF动画播放循环次数, 默认无限循环 */
    fun setLoopCount(loopCount: Int) = apply {
        this.loopCount = loopCount
    }
    //</editor-fold>

    //<editor-fold desc="Text">
    private var textOffset = Rect()
    private var textGravity = Gravity.CENTER
    private var textVisibility = false
    private var textSize = 0

    /**
     * 当前为背景图片, 这会导致显示文字内容, 但图片不会根据文字内容自动调整
     * @param visibility 是否显示文字
     */
    @JvmOverloads
    fun setTextVisibility(visibility: Boolean = true) = apply {
        textVisibility = visibility
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
     * @param gravity 值等效于[TextView.setGravity], 例如[Gravity.BOTTOM], 使用[or]组合多个值
     */
    fun setTextGravity(gravity: Int) = apply {
        this.textGravity = gravity
    }

    /**
     * 配合[AbsoluteSizeSpan]设置字体大小则图片/文字会基线对齐, 而使用本方法则图片/文字会居中对齐
     * @param size 文字大小, 单位px
     * @see setTextVisibility
     */
    fun setTextSize(size: Int) = apply {
        textSize = size
    }
    //</editor-fold>

}
