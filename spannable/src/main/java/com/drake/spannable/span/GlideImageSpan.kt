package com.drake.spannable.span

import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.text.style.ReplacementSpan
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.ColorInt
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.Transition
import java.util.concurrent.atomic.AtomicReference

/**
 * 使用Glide加载图片资源, 请先依赖[Glide](https://github.com/bumptech/glide)
 *
 * 图片垂直对齐方式
 * 图片宽高且保持固定比例, 如果存在占位图会优先使用占位图宽高比
 * 图片水平间距
 * 图片显示文字
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

    private var requestOption: RequestOptions = RequestOptions()
    private var drawableRef: AtomicReference<Drawable> = AtomicReference()


    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val bounds = getDrawable()?.bounds ?: Rect(0, 0, drawableWidth, drawableHeight)
        if (fm != null) {
            val fontMetricsInt = paint.fontMetricsInt
            val fontHeight = fontMetricsInt.descent - fontMetricsInt.ascent
            when (align) {
                Align.CENTER -> {
                    fm.ascent = fontMetricsInt.ascent - ((bounds.height() - fontHeight) / 2.0f).toInt()
                    fm.descent = fm.ascent + bounds.height()
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
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        getDrawable()?.let { drawable ->
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

            // 绘制文字
            if (textVisibility) {
                textSize?.let { paint.textSize = it.toFloat() }
                textColor?.let { paint.color = it }
                typeface?.let { paint.typeface = it }
                (paint as? TextPaint)?.let { paintConfig?.invoke(it) }
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
                canvas.drawText(
                    text, start, end,
                    (textDrawRect.left + textOffsetRect.left - textOffsetRect.right).toFloat(),
                    (textDrawRect.bottom - (paint.fontMetricsInt.descent / 2) + textOffsetRect.top - textOffsetRect.bottom).toFloat(),
                    paint
                )
            }
            canvas.restore()
        }
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

    fun getDrawable(): Drawable? {
        if (drawableRef.get() == null) {
            val placeHolder = try {
                requestOption.placeholderDrawable ?: view.context.resources.getDrawable(
                    requestOption.placeholderId
                )
            } catch (e: Exception) {
                null
            }
            placeHolder?.setFixedRatioZoom()
            val width = if (drawableWidth > 0) drawableWidth else SIZE_ORIGINAL
            val height = if (drawableHeight > 0) drawableHeight else SIZE_ORIGINAL

            Glide.with(view.context).load(url).fitCenter().apply(requestOption).into(object : CustomTarget<Drawable>(
                width,
                height
            ) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    if (resource is GifDrawable) {
                        resource.callback = drawableCallback
                        resource.setLoopCount(loopCount)
                        resource.start()
                    }
                    resource.setBounds(0, 0, resource.intrinsicWidth, resource.intrinsicHeight)
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
            })
        }
        return drawableRef.get()
    }

    /** 设置等比例缩放图片, 这会导致[drawableWidth]和[drawableHeight]根据图片原始比例变化 */
    private fun Drawable.setFixedRatioZoom() {
        val ratio = intrinsicWidth.toDouble() / intrinsicHeight
        drawableWidth = if (drawableWidth > 0) drawableWidth else intrinsicWidth
        drawableHeight = if (drawableHeight > 0) drawableHeight else intrinsicHeight
        if (intrinsicWidth > intrinsicHeight) {
            drawableHeight = (drawableWidth / ratio).toInt()
        } else if (intrinsicWidth < intrinsicHeight) {
            drawableWidth = (drawableHeight * ratio).toInt()
        }
        setBounds(0, 0, drawableWidth, drawableHeight)
    }


    /**
     * 配置Glide请求选项, 例如占位图、加载失败图等
     * 如果使用[RequestOptions.placeholder]占位图会导致默认使用占位图宽高, 除非你使用[setDrawableSize]覆盖默认值
     *
     * 默认会使用[RequestOptions.fitCenterTransform]保持图片纵横比例不变, 当然你可以覆盖该配置
     */
    fun setRequestOption(requestOption: RequestOptions) = apply {
        this.requestOption = requestOption
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
    @JvmOverloads
    fun setDrawableSize(width: Int, height: Int = width) = apply {
        this.drawableWidth = width
        this.drawableHeight = height
        drawableRef.set(null)
    }

    /** 设置图片水平间距 */
    @JvmOverloads
    fun setMarginHorizontal(left: Int, right: Int = left) = apply {
        this.marginLeft = left
        this.marginRight = right
        drawableRef.set(null)
    }

    /** GIF动画播放循环次数, 默认无限循环 */
    fun setLoopCount(loopCount: Int) = apply {
        this.loopCount = loopCount
    }

    //<editor-fold desc="Text">
    private var textOffsetRect = Rect()
    private var textGravity = Gravity.CENTER
    private var textVisibility = false
    private var textSize: Int? = null
    private var typeface: Typeface? = null
    private var textColor: Int? = null
    private var paintConfig: (TextPaint.() -> Unit)? = null

    /**
     * 当前为背景图片, 这会导致显示文字内容, 但图片不会根据文字内容自动调整
     * @param visibility 是否显示文字
     */
    @JvmOverloads
    fun setTextVisibility(visibility: Boolean = true) = apply {
        this.textVisibility = visibility
    }

    /** 文字偏移值 */
    @JvmOverloads
    fun setTextOffset(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) = apply {
        textOffsetRect.set(left, top, right, bottom)
    }

    /**
     * 文字对齐方式(基于图片), 默认对齐方式[Gravity.CENTER]
     * @param gravity 值等效于[android.widget.TextView.setGravity], 例如[Gravity.BOTTOM], 使用[or]组合多个值
     */
    fun setTextGravity(gravity: Int) = apply {
        this.textGravity = gravity
    }

    /** 配置文字画笔, 可以配置颜色/粗体/斜体等效果 */
    fun setTextPaint(paint: TextPaint.() -> Unit) = apply {
        paintConfig = paint
    }

    /**
     * 设置文字样式, 例如[android.graphics.Typeface.BOLD]粗体
     */
    fun setTypeface(typeface: Typeface) = apply {
        this.typeface = typeface
    }

    fun setTextSize(size: Int) = apply {
        textSize = size
    }

    fun setTextColor(color: String) = apply {
        textColor = Color.parseColor(color)
    }

    fun setTextColor(@ColorInt color: Int) = apply {
        textColor = color
    }
    //</editor-fold>

}
