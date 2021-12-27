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

package com.drake.spannable.sample

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import androidx.appcompat.app.AppCompatActivity
import com.drake.spannable.*
import com.drake.spannable.sample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 替换Span
        binding.tv.text = "隐私权政策 | 许可 | 品牌指南".replaceSpan("隐私权政策") {
            URLSpan("https://github.com/") // 仅替换效果
        }

        // 正则匹配两个单词
        binding.tv1.text = "隐私权政策 | 许可 | 品牌指南".replaceSpan("\\b\\w{2}\\b".toRegex()) {
            SpannableString("用户协议").setSpan(URLSpan("https://github.com/")) // 替换文字和效果
        }

        // 正则匹配同时使用分组引用
        binding.tv2.text = "隐私权政策 | 许可 | 品牌指南".replaceSpan("\\| (.*) \\|".toRegex(), true) {
            SpannableString("# $0 #").setSpan(URLSpan("https://github.com/"))
        }

        // 仅替换第一个匹配项
        binding.tv3.text = "隐私权政策 | 隐私权政策 | 品牌指南".replaceSpanFirst("隐私权政策") {
            SpannableString("用户协议").setSpan(URLSpan("https://github.com/")) // 替换文字和效果
        }

        // 添加一个字符串+Span, 注意括号保证函数执行优先级
        binding.tv4.text = ("隐私权政策 | 许可 | 品牌指南" + " | ").addSpan("官网", listOf(ForegroundColorSpan(Color.BLUE), StyleSpan(Typeface.BOLD)))

        // 通过拼接方式展示价格
        binding.tv5.text = "¥".setSpan(ForegroundColorSpan(Color.parseColor("#ed6a2c")))
            .addSpan("39.9", arrayOf(ForegroundColorSpan(Color.parseColor("#ed6a2c")), AbsoluteSizeSpan(18, true)))
            .addSpan(" 1000+ 人付款")

        // 通过替换方式展示价格
        binding.tv6.text = "¥39.9 1000+ 人付款".replaceSpan("¥[\\d\\.]+".toRegex()) { // 设置价格颜色
            ForegroundColorSpan(Color.parseColor("#ed6a2c"))
        }.replaceSpanFirst("[\\d\\.]+".toRegex()) { // 设置价格字号
            AbsoluteSizeSpan(18, true)
        }
    }
}





