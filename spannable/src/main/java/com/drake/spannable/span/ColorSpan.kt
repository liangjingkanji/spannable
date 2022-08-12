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

import android.content.Context
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorRes

/**
 * 快速渲染文字颜色
 * @param color 字体颜色
 * @see ForegroundColorSpan
 */
class ColorSpan(color: Int) : ForegroundColorSpan(color) {
    constructor(color: String) : this(Color.parseColor(color))

    /**
     * 快速创建[ForegroundColorSpan]
     * @param colorRes 字体颜色
     */
    constructor(
        context: Context,
        @ColorRes colorRes: Int
    ) : this(context.resources.getColor(colorRes))
}