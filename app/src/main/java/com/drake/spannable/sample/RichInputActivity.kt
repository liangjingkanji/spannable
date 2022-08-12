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

package com.drake.spannable.sample

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import com.drake.engine.utils.dp
import com.drake.spannable.listener.ModifyTextWatcher
import com.drake.spannable.replaceSpan
import com.drake.spannable.sample.base.BaseMenuActivity
import com.drake.spannable.sample.databinding.ActivityRichInputBinding
import com.drake.spannable.span.CenterImageSpan
import com.drake.spannable.span.HighlightSpan

class RichInputActivity : BaseMenuActivity() {

    private val binding by lazy { ActivityRichInputBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // 包含 @用户 #标签 表情包 等自动替换规则
        binding.etInput.addTextChangedListener(object : ModifyTextWatcher() {
            override fun onModify(s: Editable) {
                s.replaceSpan("@[^@]+?(?=\\s|\$)".toRegex()) {
                    HighlightSpan("#ed6a2c")
                }.replaceSpan("#[^@]+?(?=\\s|\$)".toRegex()) {
                    HighlightSpan("#4a70d2", Typeface.defaultFromStyle(Typeface.BOLD))
                }.replaceSpan("蚂蚁") {
                    CenterImageSpan(this@RichInputActivity, R.drawable.ic_ant).setDrawableSize(50.dp)
                }.replaceSpan("生气|angry".toRegex()) {
                    CenterImageSpan(this@RichInputActivity, R.drawable.ic_angry).setDrawableSize(50.dp)
                }.replaceSpan("开心".toRegex()) {
                    CenterImageSpan(this@RichInputActivity, R.drawable.ic_happy).setDrawableSize(50.dp)
                }
            }
        })
    }
}