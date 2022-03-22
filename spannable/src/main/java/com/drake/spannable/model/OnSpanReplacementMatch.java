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

package com.drake.spannable.model;

import kotlin.jvm.functions.Function1;
import kotlin.text.MatchResult;
import kotlin.text.Regex;

/**
 * 当 {@link ReplaceSpan} 有匹配项时回调
 * 详细说明: {@link com.drake.spannable.SpanUtilsKt#replaceSpan(CharSequence, Regex, Function1)}
 */
public interface OnSpanReplacementMatch {
    /**
     * @param result 当前 @{@link Regex} 匹配到的结果
     */
    void onMatch(MatchResult result);
}
