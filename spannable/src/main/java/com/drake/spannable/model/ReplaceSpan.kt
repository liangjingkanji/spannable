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

package com.drake.spannable.model

/**
 * 替换规则
 */
data class ReplaceSpan(
    /**
     * 查找的字符串或正则文本
     */
    val replaceString: String,
    /**
     * [replaceString]是否为正则
     */
    val isRegex: Boolean,
    /**
     * 最大匹配数量
     */
    val matchQuantity: Int,
    /**
     * 替换文本(null 为不替换)
     */
    val newString: CharSequence?,
    /**
     * 匹配时回调
     */
    val replacementMatch: OnSpanReplacementMatch?
) {
   internal val replaceRules: Regex
        get() = (if (isRegex) replaceString else Regex.escape(replaceString)).toRegex()
}

/**
 * 创建替换规则
 * @receiver 查找的字符串或正则文本
 * @param isRegex receiver是否为正则
 * @param matchQuantity 最大匹配数量
 * @param newString 替换文本(null 为不替换)
 * @param replacementMatch 匹配时回调
 */
fun String.toReplaceSpan(
    isRegex: Boolean = false,
    matchQuantity: Int = Int.MAX_VALUE,
    newString: CharSequence? = null,
    replacementMatch: OnSpanReplacementMatch? = null
): ReplaceSpan = ReplaceSpan(this, isRegex, matchQuantity, newString, replacementMatch)