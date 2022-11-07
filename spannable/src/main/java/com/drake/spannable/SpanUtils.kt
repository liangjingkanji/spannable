/*
 * Copyright (C) 2018 Drake, Inc.
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

package com.drake.spannable

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import androidx.core.text.getSpans

/**
 * 设置Span文字效果
 * @param what 文字效果, 如果为数组或者集合则设置多个
 * @param flags 参考 [Spanned.SPAN_EXCLUSIVE_EXCLUSIVE]
 *
 * @return 如果[this]不为[Spannable]则将返回一个新的[SpannableStringBuilder]对象
 */
@JvmOverloads
fun CharSequence.setSpan(what: Any?, flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE): CharSequence {
    return setSpan(what, 0, length, flags)
}

/**
 * 设置Span文字效果
 * @param what 文字效果, 如果为数组或者集合则设置多个
 * @param start 开始索引
 * @param end 结束索引
 * @param flags 参考 [Spanned.SPAN_EXCLUSIVE_EXCLUSIVE]
 *
 * @return 如果[this]不为[Spannable]则将返回一个新的[SpannableStringBuilder]对象
 */
@JvmOverloads
fun CharSequence.setSpan(
    what: Any?,
    start: Int,
    end: Int,
    flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
): CharSequence {
    if (what == null) return this
    val str = when (this) {
        is Spannable -> this
        else -> SpannableStringBuilder(this)
    }
    when (what) {
        is Array<*> -> what.forEach {
            if (it == null) return@forEach
            val existSpan = str.getSpans(start, end, it::class.java).getOrNull(0)
            if (existSpan == null) {
                str.setSpan(it, start, end, flags)
            } else {
                if (str.getSpanStart(existSpan) != start || str.getSpanEnd(existSpan) != end) {
                    str.removeSpan(existSpan)
                    str.setSpan(it, start, end, flags)
                }
            }
        }
        is List<*> -> what.forEach {
            if (it == null) return@forEach
            val existSpan = str.getSpans(start, end, it::class.java).getOrNull(0)
            if (existSpan == null) {
                str.setSpan(it, start, end, flags)
            } else {
                if (str.getSpanStart(existSpan) != start || str.getSpanEnd(existSpan) != end) {
                    str.removeSpan(existSpan)
                    str.setSpan(it, start, end, flags)
                }
            }
        }
        else -> {
            val existSpan = str.getSpans(start, end, what::class.java).getOrNull(0)
            if (existSpan == null) {
                str.setSpan(what, start, end, flags)
            } else {
                if (str.getSpanStart(existSpan) != start || str.getSpanEnd(existSpan) != end) {
                    str.removeSpan(existSpan)
                    str.setSpan(what, start, end, flags)
                }
            }
        }
    }
    return str
}

/**
 * 添加字符串并添加效果, 同时保留以前文字效果
 * @param text 可以是[Spanned]或[Spanned]数组/集合或[CharSequence], 空字符则无效
 *
 * @return 如果接受者不为[SpannableStringBuilder]则将返回一个新的[SpannableStringBuilder]对象
 */
infix fun CharSequence.addSpan(text: CharSequence): CharSequence {
    return addSpan(text, null)
}

/**
 * 添加字符串并添加效果, 同时保留以前文字效果
 * @param text 可以是[Spanned]或[Spanned]数组/集合或[CharSequence], 空字符则无效
 * @param what 文字效果, 如果为数组或者集合则设置多个
 * @param flags 参考 [Spanned.SPAN_EXCLUSIVE_EXCLUSIVE]
 *
 * @return 如果接受者不为[SpannableStringBuilder]则将返回一个新的[SpannableStringBuilder]对象
 */
@JvmOverloads
fun CharSequence.addSpan(
    text: CharSequence, what: Any?, flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
): CharSequence {
    val spannable = when (what) {
        is Array<*> -> what.fold(text) { s, span ->
            s.setSpan(span, flags)
        }
        is List<*> -> what.fold(text) { s, span ->
            s.setSpan(span, flags)
        }
        else -> text.setSpan(what, flags)
    }
    return when (this) {
        is SpannableStringBuilder -> append(spannable)
        else -> SpannableStringBuilder(this).append(spannable)
    }
}

/**
 * 添加字符串到指定位置并添加效果, 同时保留以前文字效果
 * @param where 插入位置
 * @param text 可以是[Spanned]或[Spanned]数组/集合或[CharSequence], 空字符则无效
 * @param what 文字效果, 如果为数组或者集合则设置多个
 * @param flags 参考 [Spanned.SPAN_EXCLUSIVE_EXCLUSIVE]
 *
 * @return 如果接受者不为[SpannableStringBuilder]则将返回一个新的[SpannableStringBuilder]对象
 */
@JvmOverloads
fun CharSequence.addSpan(
    where: Int, text: CharSequence, what: Any? = null, flags: Int = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
): CharSequence {
    val spannable = when (what) {
        is Array<*> -> what.fold(text) { s, span ->
            s.setSpan(span, flags)
        }
        is List<*> -> what.fold(text) { s, span ->
            s.setSpan(span, flags)
        }
        else -> text.setSpan(what, flags)
    }
    return when (this) {
        is SpannableStringBuilder -> insert(where, spannable)
        else -> SpannableStringBuilder(this).insert(where, spannable)
    }
}

/**
 * 替换匹配的字符串
 *
 * @param oldValue 被替换的字符串
 * @param ignoreCase 忽略大小写
 * @param replacement 每次匹配到字符串都会调用此函数, 该函数返回值可以是[Spanned]或[Spanned]数组/集合或[CharSequence], 空字符则无效
 * 1. 如果返回null则表示不执行任何操作
 * 2. 返回单个Span则应用效果, 当然返回Span集合或数组就会应用多个效果,
 * 3. 返回[Spanned]可以替换字符串同时添加Span效果.
 * 4. 返回[CharSequence]则仅仅是替换字符串.
 * 5. 并且本函数支持反向引用捕获组, 使用方法等同于RegEx: $捕获组索引
 * 6. 和[replace]函数不同的是本函数会保留原有[Spanned]的效果
 *
 * @return
 * 1. 没有匹配任何项返回[this]
 * 2. 匹配Span效果且[this]类型为[Spannable]返回[this]. 否则返回[SpannableStringBuilder]
 * 3. 匹配字符串且[this]类型为[SpannableStringBuilder]返回[this], 否则返回[SpannableStringBuilder]
 */
@JvmOverloads
fun CharSequence.replaceSpan(
    oldValue: String, ignoreCase: Boolean = false, replacement: (MatchResult) -> Any?
): CharSequence {
    val regex = if (ignoreCase) {
        Regex.escape(oldValue).toRegex(RegexOption.IGNORE_CASE)
    } else {
        Regex.escape(oldValue).toRegex()
    }
    return replaceSpan(regex, replacement = replacement)
}

/**
 * 使用正则替换匹配字符串
 *
 * @param regex 正则
 * @param quoteGroup 是否允许反向引用捕获组
 * @param replacement 每次匹配到字符串都会调用此函数, 该函数返回值可以是[Spanned]或[Spanned]数组/集合或[CharSequence], 空字符则无效
 * 1. 如果返回null则表示不执行任何操作
 * 2. 返回单个Span则应用效果, 当然返回Span集合或数组就会应用多个效果,
 * 3. 返回[Spanned]可以替换字符串同时添加Span效果.
 * 4. 返回[CharSequence]则仅仅是替换字符串.
 * 5. 并且本函数支持反向引用捕获组, 使用方法等同于RegEx: $捕获组索引
 * 6. 和[replace]函数不同的是本函数会保留原有[Spanned]的效果
 *
 * @return
 * 1. 没有匹配任何项返回[this]
 * 2. 匹配Span效果且[this]类型为[Spannable]返回[this]. 否则返回[SpannableStringBuilder]
 * 3. 匹配字符串且[this]类型为[SpannableStringBuilder]返回[this], 否则返回[SpannableStringBuilder]
 */
@JvmOverloads
fun CharSequence.replaceSpan(
    regex: Regex, quoteGroup: Boolean = false, replacement: (MatchResult) -> Any?
): CharSequence {
    val sequence = regex.findAll(this)
    val count = sequence.count()
    if (count == 0) return this
    var spanBuilder = if (this is Spannable) this else SpannableStringBuilder(this)
    var offset = 0
    sequence.forEach { matchResult ->
        val range = matchResult.range
        replacement(matchResult)?.let { spanned ->
            when (spanned) {
                is List<*> -> for (item in spanned) {
                    spanBuilder.setSpan(item ?: continue, range.first, range.last + 1)
                }
                is Array<*> -> for (item in spanned) {
                    spanBuilder.setSpan(item ?: continue, range.first, range.last + 1)
                }
                is CharSequence -> {
                    var adjustReplacement: CharSequence = spanned
                    val groups = matchResult.destructured.toList()
                    if (quoteGroup && groups.isNotEmpty()) {
                        val attachedSpans = if (spanned is Spanned) {
                            spanned.getSpans(0, spanned.length, Any::class.java)
                        } else null
                        groups.forEachIndexed { index, s ->
                            val groupRegex = "\\$$index".toRegex()
                            if (adjustReplacement.contains(groupRegex)) {
                                adjustReplacement = adjustReplacement.replace(groupRegex, s)
                            }
                        }
                        if (attachedSpans != null && adjustReplacement !== spanned) {
                            attachedSpans.forEach {
                                if (adjustReplacement is Spannable) {
                                    adjustReplacement.setSpan(it)
                                } else {
                                    adjustReplacement = SpannableStringBuilder(adjustReplacement).apply { setSpan(it) }
                                }
                            }
                        }
                    }
                    val matchLength = matchResult.value.length
                    if (spanBuilder !is SpannableStringBuilder) {
                        spanBuilder = SpannableStringBuilder(spanBuilder)
                    }
                    (spanBuilder as SpannableStringBuilder).replace(range.first + offset, range.first + offset + matchLength, adjustReplacement)
                    offset += adjustReplacement.length - matchLength
                }
                else -> spanBuilder.setSpan(spanned, range.first, range.last + 1)
            }
        }
    }
    return spanBuilder
}

/**
 * 替换第一个匹配的字符串
 *
 * @param oldValue 被替换的字符串
 * @param ignoreCase 忽略大小写
 * @param replacement 每次匹配到字符串都会调用此函数, 该函数返回值可以是[Spanned]或[Spanned]数组/集合或[CharSequence], 空字符则无效
 * 1. 如果返回null则表示不执行任何操作
 * 2. 返回单个Span则应用效果, 当然返回Span集合或数组就会应用多个效果,
 * 3. 返回[Spanned]可以替换字符串同时添加Span效果.
 * 4. 返回[CharSequence]则仅仅是替换字符串.
 * 5. 并且本函数支持反向引用捕获组, 使用方法等同于RegEx: $捕获组索引
 * 6. 和[replace]函数不同的是本函数会保留原有[Spanned]的效果
 *
 * @return
 * 1. 没有匹配任何项返回[this]
 * 2. 匹配Span效果且[this]类型为[Spannable]返回[this]. 否则返回[SpannableStringBuilder]
 * 3. 匹配字符串且[this]类型为[SpannableStringBuilder]返回[this], 否则返回[SpannableStringBuilder]
 */
@JvmOverloads
fun CharSequence.replaceSpanFirst(
    oldValue: String, ignoreCase: Boolean = false, replacement: (MatchResult) -> Any?
): CharSequence {
    val regex = if (ignoreCase) {
        Regex.escape(oldValue).toRegex(RegexOption.IGNORE_CASE)
    } else {
        Regex.escape(oldValue).toRegex()
    }
    return replaceSpanFirst(regex, replacement = replacement)
}

/**
 * 使用正则替换第一个匹配字符串
 *
 * @param regex 正则
 * @param quoteGroup 是否允许反向引用捕获组
 * @param replacement 每次匹配到字符串都会调用此函数, 该函数返回值可以是[Spanned]或[Spanned]数组/集合或[CharSequence], 空字符则无效
 * 1. 如果返回null则表示不执行任何操作
 * 2. 返回单个Span则应用效果, 当然返回Span集合或数组就会应用多个效果,
 * 3. 返回[Spanned]可以替换字符串同时添加Span效果.
 * 4. 返回[CharSequence]则仅仅是替换字符串.
 * 5. 并且本函数支持反向引用捕获组, 使用方法等同于RegEx: $捕获组索引
 * 6. 和[replace]函数不同的是本函数会保留原有[Spanned]的效果
 *
 * @return
 * 1. 没有匹配任何项返回[this]
 * 2. 匹配Span效果且[this]类型为[Spannable]返回[this]. 否则返回[SpannableStringBuilder]
 * 3. 匹配字符串且[this]类型为[SpannableStringBuilder]返回[this], 否则返回[SpannableStringBuilder]
 */
@JvmOverloads
fun CharSequence.replaceSpanFirst(
    regex: Regex, quoteGroup: Boolean = false, replacement: (MatchResult) -> Any?
): CharSequence {
    val matchResult = regex.find(this) ?: return this
    var spanBuilder = if (this is Spannable) this else SpannableStringBuilder(this)
    val range = matchResult.range
    replacement(matchResult)?.let { spanned ->
        when (spanned) {
            is List<*> -> for (item in spanned) {
                spanBuilder.setSpan(item ?: continue, range.first, range.last + 1)
            }
            is Array<*> -> for (item in spanned) {
                spanBuilder.setSpan(item ?: continue, range.first, range.last + 1)
            }
            is CharSequence -> {
                var adjustReplacement: CharSequence = spanned
                val groups = matchResult.destructured.toList()
                if (quoteGroup && groups.isNotEmpty()) {
                    val attachedSpans = if (spanned is Spanned) {
                        spanned.getSpans(0, spanned.length, Any::class.java)
                    } else null
                    groups.forEachIndexed { index, s ->
                        val groupRegex = "\\$$index".toRegex()
                        if (adjustReplacement.contains(groupRegex)) {
                            adjustReplacement = adjustReplacement.replace(groupRegex, s)
                        }
                    }
                    if (attachedSpans != null && adjustReplacement !== spanned) {
                        attachedSpans.forEach {
                            if (adjustReplacement is Spannable) {
                                adjustReplacement.setSpan(it)
                            } else {
                                adjustReplacement = SpannableStringBuilder(adjustReplacement).apply { setSpan(it) }
                            }
                        }
                    }
                }
                val matchLength = matchResult.value.length
                if (spanBuilder !is SpannableStringBuilder) {
                    spanBuilder = SpannableStringBuilder(spanBuilder)
                }
                (spanBuilder as SpannableStringBuilder).replace(range.first, range.first + matchLength, adjustReplacement)
            }
            else -> spanBuilder.setSpan(spanned, range.first, range.last + 1)
        }
    }
    return spanBuilder
}

/**
 * 替换最后一个匹配的字符串
 *
 * @param oldValue 被替换的字符串
 * @param ignoreCase 忽略大小写
 * @param replacement 每次匹配到字符串都会调用此函数, 该函数返回值可以是[Spanned]或[Spanned]数组/集合或[CharSequence], 空字符则无效
 * 1. 如果返回null则表示不执行任何操作
 * 2. 返回单个Span则应用效果, 当然返回Span集合或数组就会应用多个效果,
 * 3. 返回[Spanned]可以替换字符串同时添加Span效果.
 * 4. 返回[CharSequence]则仅仅是替换字符串.
 * 5. 并且本函数支持反向引用捕获组, 使用方法等同于RegEx: $捕获组索引
 * 6. 和[replace]函数不同的是本函数会保留原有[Spanned]的效果
 *
 * @return
 * 1. 没有匹配任何项返回[this]
 * 2. 匹配Span效果且[this]类型为[Spannable]返回[this]. 否则返回[SpannableStringBuilder]
 * 3. 匹配字符串且[this]类型为[SpannableStringBuilder]返回[this], 否则返回[SpannableStringBuilder]
 */
@JvmOverloads
fun CharSequence.replaceSpanLast(
    oldValue: String, ignoreCase: Boolean = false, replacement: (MatchResult) -> Any?
): CharSequence {
    val regex = if (ignoreCase) {
        Regex.escape(oldValue).toRegex(RegexOption.IGNORE_CASE)
    } else {
        Regex.escape(oldValue).toRegex()
    }
    return replaceSpanLast(regex, replacement = replacement)
}

/**
 * 使用正则替换最后一个匹配字符串
 *
 * @param regex 正则
 * @param quoteGroup 是否允许反向引用捕获组
 * @param replacement 每次匹配到字符串都会调用此函数, 该函数返回值可以是[Spanned]或[Spanned]数组/集合或[CharSequence], 空字符则无效
 * 1. 如果返回null则表示不执行任何操作
 * 2. 返回单个Span则应用效果, 当然返回Span集合或数组就会应用多个效果,
 * 3. 返回[Spanned]可以替换字符串同时添加Span效果.
 * 4. 返回[CharSequence]则仅仅是替换字符串.
 * 5. 并且本函数支持反向引用捕获组, 使用方法等同于RegEx: $捕获组索引
 * 6. 和[replace]函数不同的是本函数会保留原有[Spanned]的效果
 *
 * @return
 * 1. 没有匹配任何项返回[this]
 * 2. 匹配Span效果且[this]类型为[Spannable]返回[this]. 否则返回[SpannableStringBuilder]
 * 3. 匹配字符串且[this]类型为[SpannableStringBuilder]返回[this], 否则返回[SpannableStringBuilder]
 */
@JvmOverloads
fun CharSequence.replaceSpanLast(
    regex: Regex, quoteGroup: Boolean = false, replacement: (MatchResult) -> Any?
): CharSequence {
    val matchResult = regex.findAll(this).lastOrNull() ?: return this
    var spanBuilder = if (this is Spannable) this else SpannableStringBuilder(this)
    val range = matchResult.range
    replacement(matchResult)?.let { spanned ->
        when (spanned) {
            is List<*> -> for (item in spanned) {
                spanBuilder.setSpan(item ?: continue, range.first, range.last + 1)
            }
            is Array<*> -> for (item in spanned) {
                spanBuilder.setSpan(item ?: continue, range.first, range.last + 1)
            }
            is CharSequence -> {
                var adjustReplacement: CharSequence = spanned
                val groups = matchResult.destructured.toList()
                if (quoteGroup && groups.isNotEmpty()) {
                    val attachedSpans = if (spanned is Spanned) {
                        spanned.getSpans(0, spanned.length, Any::class.java)
                    } else null
                    groups.forEachIndexed { index, s ->
                        val groupRegex = "\\$$index".toRegex()
                        if (adjustReplacement.contains(groupRegex)) {
                            adjustReplacement = adjustReplacement.replace(groupRegex, s)
                        }
                    }
                    if (attachedSpans != null && adjustReplacement !== spanned) {
                        attachedSpans.forEach {
                            if (adjustReplacement is Spannable) {
                                adjustReplacement.setSpan(it)
                            } else {
                                adjustReplacement = SpannableStringBuilder(adjustReplacement).apply { setSpan(it) }
                            }
                        }
                    }
                }
                val matchLength = matchResult.value.length
                if (spanBuilder !is SpannableStringBuilder) {
                    spanBuilder = SpannableStringBuilder(spanBuilder)
                }
                (spanBuilder as SpannableStringBuilder).replace(range.first, range.first + matchLength, adjustReplacement)
            }
            else -> spanBuilder.setSpan(spanned, range.first, range.last + 1)
        }
    }
    return spanBuilder
}

/**
 * 删除指定泛型Span
 */
inline fun <reified T : Any> CharSequence.clearSpans(
    start: Int = 0,
    end: Int = length
): CharSequence {
    if (this is Spannable) {
        getSpans<T>(start, end).forEach {
            removeSpan(it)
        }
    }
    return this
}