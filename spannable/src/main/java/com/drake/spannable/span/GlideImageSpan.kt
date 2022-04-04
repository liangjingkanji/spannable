package com.drake.spannable.span

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.ReplacementSpan
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import java.util.concurrent.atomic.AtomicReference

/**
 * ** 使用本类请先依赖Glide库 **
 * 使用Glide加载图片资源
 * 图片完全垂直居中对齐文字(ImageSpan无法真正垂直居中)
 * 垂直居中文字不要求Api23以上
 * 设置图片宽高且保持固定比例
 *
 * 图片默认垂直居中对齐文字
 *
 * @see CenterImageSpan 如果你不需要加载网络图片可以使用该类
 */
class GlideImageSpan(val view: TextView, val url: Any) : ReplacementSpan() {

    /** 图片宽度 */
    var drawableWidth: Int = 0
        private set

    /** 图片高度 */
    var drawableHeight: Int = 0
        private set

    private var requestOption: RequestOptions = RequestOptions()
    private var drawableRef: AtomicReference<Drawable> = AtomicReference()


    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val bounds = getDrawable()?.bounds
        if (fm != null) {
            val fontMetricsInt = paint.fontMetricsInt
            val fontHeight = fontMetricsInt.descent - fontMetricsInt.ascent
            val imageHeight = drawableHeight.takeIf { it > 0 } ?: bounds?.height() ?: 0
            fm.ascent = fontMetricsInt.ascent - ((imageHeight - fontHeight) / 2.0f).toInt()
            fm.top = fm.ascent
            fm.descent = fm.ascent + imageHeight
            fm.bottom = fm.descent
        }
        return bounds?.right ?: 0
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
        getDrawable()?.let { drawable ->
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
    }

    fun getDrawable(): Drawable? {
        if (drawableRef.get() == null) {
            val width = if (drawableWidth > 0) drawableWidth else SIZE_ORIGINAL
            val height = if (drawableHeight > 0) drawableHeight else SIZE_ORIGINAL
            Glide.with(view.context).load(url).apply(requestOption).into(object : SimpleTarget<Drawable>(width, height) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                ) {
                    resource.setBounds(0, 0, resource.intrinsicWidth, resource.intrinsicHeight)
                    drawableRef.set(resource)
                    view.invalidate()
                }

                override fun onLoadStarted(placeholder: Drawable?) {
                    setDrawable(placeholder)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    if (errorDrawable != getDrawable()) {
                        setDrawable(errorDrawable)
                        errorDrawable?.let { view.invalidate() }
                    }
                }
            })
        }
        return drawableRef.get()
    }

    private fun setDrawable(resource: Drawable?) {
        resource ?: return
        val ratio = resource.intrinsicWidth.toDouble() / resource.intrinsicHeight.toDouble()
        drawableWidth = if (drawableWidth > 0) drawableWidth else resource.intrinsicWidth
        drawableHeight = if (drawableHeight > 0) drawableHeight else resource.intrinsicHeight
        if (drawableWidth > drawableHeight) {
            drawableWidth = (drawableHeight * ratio).toInt()
        } else if (drawableWidth < drawableHeight) {
            drawableHeight = (drawableWidth / ratio).toInt()
        }
        resource.setBounds(0, 0, drawableWidth, drawableHeight)
        drawableRef.set(resource)
    }

    /**
     * 配置Glide请求选项, 例如占位图、加载失败图等
     * 默认启用裁剪centerCrop
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
     * 如果参数值为0则表示使用图片原始宽高
     */
    fun setDrawableSize(width: Int, height: Int = width, dp: Boolean = false) = apply {
        this.drawableWidth = if (dp) dp2px(width) else width
        this.drawableHeight = if (dp) dp2px(height) else height
        drawableRef.set(null)
    }

    private fun dp2px(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

}
