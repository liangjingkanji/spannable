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
@file:JvmName("Span")
@file:Suppress("unused")

package com.drake.spannable

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.MaskFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.SpannedString
import android.text.style.SuggestionSpan
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FloatRange
import androidx.core.text.buildSpannedString
import com.drake.spannable.model.*
import java.util.*

/**
 * eg.
 * ```kotlin
 * TextView.setText(Span.create()
 *                   .text("this is real text.")
 *                   .text("spannable").color(Color.BLUE).style(Typeface.BOLD)
 *                   .spannable())
 * ```
 */
class Span private constructor() {

    private val spannableBuilder = SpannableStringBuilder()
    private var spannableCache: Spannable? = null

    private val Spannable?.isNotNullAndEmpty: Boolean
        get() = this != null && this.isNotEmpty()

    private fun runOnSelf(block: () -> Spannable?): Span = apply {
        block.invoke()?.let { spannableCache = it }
    }

    private fun checkImageSpan() {
        if (spannableCache.isNullOrEmpty()) {
            spannableCache = SpannableString(" ")
        }
    }

    fun saveCache(): Span = apply {
        if (spannableCache.isNotNullAndEmpty) {
            spannableBuilder.append(spannableCache)
        }
    }

    /**
     * 插入待处理字符串
     * 在使用[style] [typeface] [color]... 等等之前，需调用该方法插入当前需要处理的字符串
     */
    fun text(text: CharSequence): Span = apply {
        saveCache()
        spannableCache = SpannableString(text)
    }

    /**
     * 构建Spannable
     */
    fun spannable(): SpannedString {
        saveCache()
        spannableCache = null
        return SpannedString(spannableBuilder)
    }

    /**
     * @see [CharSequence.spanStyle]
     */
    @JvmOverloads
    fun style(
        @TextStyle style: Int,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanStyle(style, replaceSpan) }

    /**
     * @see [CharSequence.spanTypeface]
     */
    @JvmOverloads
    fun typeface(
        typeface: Typeface? = null,
        family: String? = null,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanTypeface(typeface, family, replaceSpan) }

    /**
     * @see [CharSequence.spanTextAppearance]
     */
    @JvmOverloads
    fun textAppearance(
        @TextStyle style: Int = Typeface.NORMAL,
        size: Int = -1,
        @ColorInt color: Int? = null,
        family: String? = null,
        linkColor: ColorStateList? = null,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf {
        spannableCache?.spanTextAppearance(
            style,
            size,
            color,
            family,
            linkColor,
            replaceSpan
        )
    }

    /**
     * @see [CharSequence.spanColor]
     */
    @JvmOverloads
    fun color(
        colorString: String,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanColor(colorString, replaceSpan) }


    /**
     * @see [CharSequence.spanColor]
     */
    @JvmOverloads
    fun color(
        @ColorInt color: Int,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanColor(color, replaceSpan) }

    /**
     * @see [CharSequence.spanBackground]
     */
    @JvmOverloads
    fun background(
        colorString: String,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanBackground(colorString, replaceSpan) }

    /**
     * @see [CharSequence.spanBackground]
     */
    @JvmOverloads
    fun background(
        @ColorInt color: Int,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanBackground(color, replaceSpan) }

    /**
     * @see [CharSequence.spanImage]
     */
    @JvmOverloads
    fun image(
        drawable: Drawable,
        source: String? = null,
        useTextViewSize: TextView? = null,
        size: DrawableSize? = null,
        replaceSpan: ReplaceSpan? = null,
    ): Span = runOnSelf {
        checkImageSpan()
        spannableCache?.spanImage(drawable, source, useTextViewSize, size, replaceSpan)
    }

    /**
     * @see [CharSequence.spanImage]
     */
    @JvmOverloads
    fun image(
        context: Context,
        uri: Uri,
        useTextViewSize: TextView? = null,
        size: DrawableSize? = null,
        replaceSpan: ReplaceSpan? = null,
    ): Span = runOnSelf {
        checkImageSpan()
        spannableCache?.spanImage(context, uri, useTextViewSize, size, replaceSpan)
    }

    /**
     * @see [CharSequence.spanImage]
     */
    @JvmOverloads
    fun image(
        context: Context,
        @DrawableRes resourceId: Int,
        useTextViewSize: TextView? = null,
        size: DrawableSize? = null,
        replaceSpan: ReplaceSpan? = null,
    ): Span = runOnSelf {
        checkImageSpan()
        spannableCache?.spanImage(context, resourceId, useTextViewSize, size, replaceSpan)
    }

    /**
     * @see [CharSequence.spanImage]
     */
    @JvmOverloads
    fun image(
        context: Context,
        bitmap: Bitmap,
        useTextViewSize: TextView? = null,
        size: DrawableSize? = null,
        replaceSpan: ReplaceSpan? = null,
    ): Span = runOnSelf {
        checkImageSpan()
        spannableCache?.spanImage(context, bitmap, useTextViewSize, size, replaceSpan)
    }

    /**
     * @see [CharSequence.spanScaleX]
     */
    @JvmOverloads
    fun scaleX(
        @FloatRange(from = 0.0) proportion: Float,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanScaleX(proportion, replaceSpan) }


    /**
     * @see [CharSequence.spanMaskFilter]
     */
    @JvmOverloads
    fun maskFilter(
        filter: MaskFilter,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanMaskFilter(filter, replaceSpan) }

    /**
     * @see [CharSequence.spanBlurMask]
     */
    @JvmOverloads
    fun blurMask(
        @FloatRange(from = 0.0) radius: Float,
        style: BlurMaskFilter.Blur = BlurMaskFilter.Blur.NORMAL,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanBlurMask(radius, style, replaceSpan) }

    /**
     * @see [CharSequence.spanSuperscript]
     */
    @JvmOverloads
    fun superscript(
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanSuperscript(replaceSpan) }

    /**
     * @see [CharSequence.spanSubscript]
     */
    @JvmOverloads
    fun subscript(
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanSubscript(replaceSpan) }

    /**
     * @see [CharSequence.spanAbsoluteSize]
     */
    @JvmOverloads
    fun absoluteSize(
        size: Int,
        dip: Boolean = true,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanAbsoluteSize(size, dip, replaceSpan) }

    /**
     * @see [CharSequence.spanRelativeSize]
     */
    @JvmOverloads
    fun relativeSize(
        @FloatRange(from = 0.0) proportion: Float,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanRelativeSize(proportion, replaceSpan) }

    /**
     * @see [CharSequence.spanStrikethrough]
     */
    @JvmOverloads
    fun strikethrough(
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanStrikethrough(replaceSpan) }

    /**
     * @see [CharSequence.spanUnderline]
     */
    @JvmOverloads
    fun underline(
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanUnderline(replaceSpan) }

    /**
     * @see [CharSequence.spanURL]
     */
    @JvmOverloads
    fun url(
        url: String,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf { spannableCache?.spanURL(url, replaceSpan) }

    /**
     * @see [CharSequence.spanSuggestion]
     */
    @JvmOverloads
    fun suggestion(
        context: Context,
        vararg suggestions: String,
        flags: Int = SuggestionSpan.SUGGESTIONS_MAX_SIZE,
        locale: Locale? = null,
        notificationTargetClass: Class<*>? = null,
        replaceSpan: ReplaceSpan? = null
    ): Span = runOnSelf {
        spannableCache?.spanSuggestion(
            context,
            suggestions = suggestions,
            flags,
            locale,
            notificationTargetClass,
            replaceSpan
        )
    }

    /**
     * @see [CharSequence.spanHighlight]
     */
    @JvmOverloads
    fun highlight(
        colorString: String?,
        typeface: Typeface? = null,
        replaceSpan: ReplaceSpan? = null,
        onClick: OnHighlightClickListener? = null,
    ): Span = runOnSelf {
        spannableCache?.spanHighlight(
            colorString = colorString,
            typeface = typeface,
            replaceSpan = replaceSpan,
            onClick = onClick
        )
    }

    /**
     * @see [CharSequence.spanHighlight]
     */
    @JvmOverloads
    fun highlight(
        @ColorInt color: Int? = null,
        typeface: Typeface? = null,
        replaceSpan: ReplaceSpan? = null,
        onClick: OnHighlightClickListener? = null,
    ): Span = runOnSelf {
        spannableCache?.spanHighlight(
            color,
            typeface = typeface,
            replaceSpan = replaceSpan,
            onClick = onClick
        )
    }

    /**
     * @see [CharSequence.spanHighlight]
     */
    @JvmOverloads
    fun highlight(
        context: Context,
        @ColorRes colorRes: Int,
        typeface: Typeface? = null,
        replaceSpan: ReplaceSpan? = null,
        onClick: OnHighlightClickListener? = null,
    ): Span = runOnSelf {
        spannableCache?.spanHighlight(context, colorRes, typeface, replaceSpan, onClick)
    }

    companion object {
        /**
         * 构建Span
         * @see [Span]
         */
        @JvmStatic
        fun create(): Span = Span()

        //<editor-fold desc="Java 适配">
        /**
         * 适配Java不支持CharSequence operator plus
         * eg. `SpanExtension.spannedString(spanImage(...),spanColor(..))`
         */
        @JvmStatic
        fun spannedString(vararg texts: CharSequence): SpannedString = buildSpannedString {
            texts.forEach(this::append)
        }

        /**
         * 兼容Java 适配
         * @see [toReplaceSpan]
         */
        @JvmStatic
        @JvmOverloads
        fun builderReplace(
            replaceString: String,
            isRegex: Boolean = false,
            matchQuantity: Int = Int.MAX_VALUE,
            newString: CharSequence? = null,
            replacementMatch: OnSpanReplacementMatch? = null
        ): ReplaceSpan =
            replaceString.toReplaceSpan(isRegex, matchQuantity, newString, replacementMatch)

        /**
         * 兼容Java 适配
         * @see [TextView.activateClick]
         */
        @JvmStatic
        @JvmOverloads
        fun activateClick(textView: TextView, background: Boolean = true): TextView =
            textView.activateClick(background)
        //</editor-fold>
    }
}