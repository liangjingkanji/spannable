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

package com.drake.spannable.listener

import android.text.Editable
import android.text.TextWatcher

/**
 * 允许修改已输入内容
 */
abstract class ModifyTextWatcher : TextWatcher {

    /** 是否为修改事件 */
    protected var isModifyEvent = false

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable) {
        if (!isModifyEvent) {
            isModifyEvent = true
            onModify(s)
            isModifyEvent = false
        }
    }

    /**
     * 每次输入完成会被触发, 在[onModify]中可以修改已输入内容, 且不会导致死循环
     */
    abstract fun onModify(s: Editable)
}