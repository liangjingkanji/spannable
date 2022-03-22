/*
 * Copyright (C) 2022 TxcA, Inc.
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

@file:JvmName("SpannableExtension")
@file:Suppress("unused")

package com.drake.spannable

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.*
import androidx.core.view.children
import androidx.viewbinding.ViewBinding
import com.drake.spannable.model.*
import com.drake.spannable.movement.ClickableMovementMethod
import com.drake.spannable.span.CenterImageSpan
import com.drake.spannable.span.ColorSpan
import com.drake.spannable.span.HighlightSpan
import java.util.*

//<editor-fold desc="内部方法">
private val EMPTY_SPAN = SpannableString("")

/**
 * ImageSpan Text标识
 */
private const val IMAGE_SPAN_TAG = " "

/**
 * [CenterImageSpan] 适配 [Drawable] size
 */
private fun CenterImageSpan.setupSize(
    useTextViewSize: TextView?,
    size: DrawableSize?
): CenterImageSpan = apply {
    useTextViewSize?.textSizeInt?.let { textSize ->
        width = textSize
        height = textSize
    } ?: size?.let { drawableSize ->
        width = drawableSize.width
        height = drawableSize.height
    }
}

/**
 * 适配[setSpan] 的返回值为 [Spannable], 以便进行plus操作
 */
private fun CharSequence.span(what: Any?): Spannable = setSpan(what) as Spannable

/**
 * 适配[replaceSpan] 的返回值为 [Spannable], 以便进行plus操作
 */
private fun CharSequence.spanReplace(
    regex: Regex,
    replacement: (MatchResult) -> Any?
): Spannable = replaceSpan(regex, replacement = replacement) as Spannable

/**
 * [setSpan] or [replaceSpan]
 */
private fun CharSequence.setOrReplaceSpan(
    replaceSpan: ReplaceSpan?,
    createWhat: (matchText: String) -> CharacterStyle
): Spannable = replaceSpan?.let { replace ->
    var currentMatchCount = 0
    spanReplace(replace.replaceRules) {
        if (++currentMatchCount <= replace.matchQuantity) {
            replace.replacementMatch?.onMatch(it)
            val characterStyle = createWhat.invoke(it.value)
            replaceSpan.newString?.span(characterStyle) ?: characterStyle
        } else null
    }
} ?: span(createWhat.invoke(this.toString()))


//</editor-fold>

//<editor-fold desc="Spannable Helper">
/**
 * [String] 转为 [Spannable], 以便进行plus操作
 */
val String.span: Spannable
    get() = SpannableString(this)

/**
 * operator [Spannable] + [Spannable]
 */
operator fun Spannable.plus(other: CharSequence): Spannable =
    when (this) {
        is SpannableStringBuilder -> append(other)
        else -> SpannableStringBuilder(this).append(other)
    }

/**
 * operator [CharSequence] + [CharSequence]
 */
operator fun CharSequence.plus(other: CharSequence): CharSequence =
    when (this) {
        is SpannableStringBuilder -> append(other)
        else -> SpannableStringBuilder(this).append(other)
    }
//</editor-fold>

//<editor-fold desc="Spannable 扩展 ">
/**
 * [StyleSpan] 设置文本样式
 */
fun CharSequence.spanStyle(
    @TextStyle style: Int,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    StyleSpan(style)
}

/**
 * [TypefaceSpan] 设置字体样式
 */
fun CharSequence.spanTypeface(
    typeface: Typeface? = null,
    family: String? = null,
    replaceSpan: ReplaceSpan? = null
): Spannable = (
        if (typeface != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            TypefaceSpan(typeface)
        } else family?.let { TypefaceSpan(it) }
        )?.let { typefaceSpan -> setOrReplaceSpan(replaceSpan) { typefaceSpan } } ?: EMPTY_SPAN

/**
 * [TextAppearanceSpan] 设置字体效果spanTypeface
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanTextAppearance(
    @TextStyle style: Int = Typeface.NORMAL,
    size: Int = -1,
    @ColorInt color: Int? = null,
    family: String? = null,
    linkColor: ColorStateList? = null,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    TextAppearanceSpan(family, style, size, color?.let(ColorStateList::valueOf), linkColor)
}

/**
 * [ColorSpan] 文本颜色
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanColor(
    colorString: String,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    ColorSpan(colorString)
}

/**
 * [ColorSpan] 文本颜色
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanColor(
    @ColorInt color: Int,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    ColorSpan(color)
}

/**
 * [ColorSpan] 文本颜色
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanBackground(
    colorString: String,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    BackgroundColorSpan(Color.parseColor(colorString))
}

/**
 * [ColorSpan] 文本颜色
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanBackground(
    @ColorInt color: Int,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    BackgroundColorSpan(color)
}

/**
 * [CenterImageSpan] 图片
 */
fun spanImage(
    drawable: Drawable,
    source: String? = null,
    useTextViewSize: TextView? = null,
    size: DrawableSize? = null
): Spannable = IMAGE_SPAN_TAG.spanImage(drawable, source, useTextViewSize, size)

/**
 * [CenterImageSpan] 图片
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanImage(
    drawable: Drawable,
    source: String? = null,
    useTextViewSize: TextView? = null,
    size: DrawableSize? = null,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    (source?.let {
        CenterImageSpan(drawable, it)
    } ?: CenterImageSpan(drawable)).setupSize(useTextViewSize, size)
}

/**
 * [CenterImageSpan] 图片
 */
fun spanImage(
    context: Context,
    uri: Uri,
    useTextViewSize: TextView? = null,
    size: DrawableSize? = null,
): Spannable = IMAGE_SPAN_TAG.spanImage(context, uri, useTextViewSize, size)

/**
 * [CenterImageSpan] 图片
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanImage(
    context: Context,
    uri: Uri,
    useTextViewSize: TextView? = null,
    size: DrawableSize? = null,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    CenterImageSpan(context, uri).setupSize(useTextViewSize, size)
}

/**
 * [CenterImageSpan] 图片
 */
fun spanImage(
    context: Context,
    @DrawableRes resourceId: Int,
    useTextViewSize: TextView? = null,
    size: DrawableSize? = null,
): Spannable = IMAGE_SPAN_TAG.spanImage(context, resourceId, useTextViewSize, size)

/**
 * [CenterImageSpan] 图片
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanImage(
    context: Context,
    @DrawableRes resourceId: Int,
    useTextViewSize: TextView? = null,
    size: DrawableSize? = null,
    replaceSpan: ReplaceSpan? = null,
): Spannable = setOrReplaceSpan(replaceSpan) {
    CenterImageSpan(context, resourceId).setupSize(useTextViewSize, size)
}

/**
 * [CenterImageSpan] 图片
 */
fun spanImage(
    context: Context,
    bitmap: Bitmap,
    useTextViewSize: TextView? = null,
    size: DrawableSize? = null,
): Spannable = IMAGE_SPAN_TAG.spanImage(context, bitmap, useTextViewSize, size)

/**
 * [CenterImageSpan] 图片
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanImage(
    context: Context,
    bitmap: Bitmap,
    useTextViewSize: TextView? = null,
    size: DrawableSize? = null,
    replaceSpan: ReplaceSpan? = null,
): Spannable = setOrReplaceSpan(replaceSpan) {
    CenterImageSpan(context, bitmap).setupSize(useTextViewSize, size)
}

/**
 * [ScaleXSpan] X轴文本缩放
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanScaleX(
    @FloatRange(from = 0.0) proportion: Float,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    ScaleXSpan(proportion)
}

/**
 * [MaskFilterSpan] 设置文本蒙版效果
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanMaskFilter(
    filter: MaskFilter,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    MaskFilterSpan(filter)
}

/**
 * [BlurMaskFilter] 设置文本模糊滤镜蒙版效果
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanBlurMask(
    @FloatRange(from = 0.0) radius: Float,
    style: BlurMaskFilter.Blur = BlurMaskFilter.Blur.NORMAL,
    replaceSpan: ReplaceSpan? = null
): Spannable = spanMaskFilter(BlurMaskFilter(radius, style), replaceSpan)

/**
 * [SuperscriptSpan] 设置文本为上标
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanSuperscript(
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    SuperscriptSpan()
}

/**
 * [SubscriptSpan] 设置文本为下标
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanSubscript(
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    SubscriptSpan()
}

/**
 * [AbsoluteSizeSpan] 设置文本绝对大小
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanAbsoluteSize(
    size: Int,
    dip: Boolean = true,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    AbsoluteSizeSpan(size, dip)
}

/**
 * [RelativeSizeSpan] 设置文本相对大小
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanRelativeSize(
    @FloatRange(from = 0.0) proportion: Float,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    RelativeSizeSpan(proportion)
}

/**
 * [StrikethroughSpan] 设置文本删除线
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanStrikethrough(
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    StrikethroughSpan()
}

/**
 * [UnderlineSpan] 设置文本下划线
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanUnderline(
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    UnderlineSpan()
}

/**
 * [URLSpan] 设置文本超链接
 * 配合[TextView.activateClick]使用
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanURL(
    url: String,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    URLSpan(url)
}

/**
 * [SuggestionSpan] 设置文本输入提示
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 */
fun CharSequence.spanSuggestion(
    context: Context,
    vararg suggestions: String,
    flags: Int = SuggestionSpan.SUGGESTIONS_MAX_SIZE,
    locale: Locale? = null,
    notificationTargetClass: Class<*>? = null,
    replaceSpan: ReplaceSpan? = null
): Spannable = setOrReplaceSpan(replaceSpan) {
    SuggestionSpan(context, locale, suggestions, flags, notificationTargetClass)
}

/**
 * [HighlightSpan] 设置文本高亮点击
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 * @param onClick [OnHighlightClickListener] 点击回调
 */
fun CharSequence.spanHighlight(
    @ColorInt color: Int? = null,
    colorString: String? = null,
    typeface: Typeface? = null,
    replaceSpan: ReplaceSpan? = null,
    onClick: OnHighlightClickListener? = null,
): Spannable = setOrReplaceSpan(replaceSpan) { matchText ->
    colorString?.let { cString ->
        HighlightSpan(cString, typeface) { onClick?.onClick(it, matchText) }
    } ?: HighlightSpan(color, typeface) { onClick?.onClick(it, matchText) }
}

/**
 * [HighlightSpan] 设置文本高亮点击
 *
 * @param replaceSpan [ReplaceSpan] 替换规则
 * @param onClick [OnHighlightClickListener] 点击回调
 */
fun CharSequence.spanHighlight(
    context: Context,
    @ColorRes colorRes: Int,
    typeface: Typeface? = null,
    replaceSpan: ReplaceSpan? = null,
    onClick: OnHighlightClickListener? = null,
): Spannable = setOrReplaceSpan(replaceSpan) { matchText ->
    HighlightSpan(context, colorRes, typeface) {
        onClick?.onClick(it, matchText)
    }
}
//</editor-fold>

//<editor-fold desc="配置 Movement Method">
/**
 * 配置 [LinkMovementMethod] 或 [ClickableMovementMethod]
 * @param background 是否显示点击背景
 */
fun TextView.activateClick(background: Boolean = true): TextView = apply {
    movementMethod = if (background) LinkMovementMethod.getInstance() else ClickableMovementMethod.getInstance()
}

/**
 * 循环获取控件并配置 [LinkMovementMethod] 或 [ClickableMovementMethod]
 * @param background 是否显示点击背景
 * @param ignoreId 忽略配置movementMethod的ViewId
 */
fun View?.autoActivateClick(background: Boolean, @IdRes vararg ignoreId: Int) {
    when (this) {
        is TextView -> {
            if (!ignoreId.contains(id)) {
                activateClick(background)
            }
        }
        is ViewGroup -> {
            children.forEach {
                it.autoActivateClick(background, *ignoreId)
            }
        }
    }
}

/**
 * 循环 [ViewBinding] 控件并配置 [LinkMovementMethod] 或 [ClickableMovementMethod]
 * @param background 是否显示点击背景
 * @param ignoreId 忽略配置movementMethod的ViewId
 */
fun ViewBinding.activateAllTextViewClick(background: Boolean = true, @IdRes vararg ignoreId: Int) {
    root.autoActivateClick(background, *ignoreId)
}

/**
 * 循环 [Activity] 控件并配置 [LinkMovementMethod] 或 [ClickableMovementMethod]
 * @param background 是否显示点击背景
 * @param ignoreId 忽略配置movementMethod的ViewId
 */
fun Activity.activateAllTextViewClick(background: Boolean = true, @IdRes vararg ignoreId: Int) {
    findViewById<ViewGroup>(android.R.id.content).children.first()
        .autoActivateClick(background, *ignoreId)
}

/**
 * 循环 [Fragment] 控件并配置 [LinkMovementMethod] 或 [ClickableMovementMethod]
 * @param background 是否显示点击背景
 * @param ignoreId 忽略配置movementMethod的ViewId
 */
fun Fragment.activateAllTextViewClick(background: Boolean = true, @IdRes vararg ignoreId: Int) {
    view.autoActivateClick(background, *ignoreId)
}
//</editor-fold>