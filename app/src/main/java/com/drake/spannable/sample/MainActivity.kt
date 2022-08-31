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

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import com.drake.engine.utils.dp
import com.drake.spannable.addSpan
import com.drake.spannable.movement.ClickableMovementMethod
import com.drake.spannable.replaceSpan
import com.drake.spannable.replaceSpanFirst
import com.drake.spannable.sample.base.BaseMenuActivity
import com.drake.spannable.sample.databinding.ActivityMainBinding
import com.drake.spannable.setSpan
import com.drake.spannable.span.CenterImageSpan
import com.drake.spannable.span.ColorSpan
import com.drake.spannable.span.GlideImageSpan
import com.drake.spannable.span.HighlightSpan


class MainActivity : BaseMenuActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 替换Span
        binding.tv.movementMethod = ClickableMovementMethod.getInstance()
        binding.tv.text = "隐私权政策 | 许可 | 品牌指南".replaceSpan("隐私权政策") {
            URLSpan("https://github.com/") // 仅替换效果
        }

        // 自动设置网址
        binding.tv1.movementMethod = LinkMovementMethod()
        binding.tv1.text = "打开官网: https://github.com/" // 地址/邮箱/手机号码等匹配可以不使用Span, 可以在xml中指定autoLink属性, 会有点击背景色

        // 使用正则匹配
        binding.tv2.movementMethod = ClickableMovementMethod.getInstance() // 保证没有点击背景色
        binding.tv2.text = "我们可以艾特用户@刘强东 或者创建#热门标签"
            .replaceSpan("@[^@]+?(?=\\s|\$)".toRegex()) { matchResult ->
                HighlightSpan("#ed6a2c") {
                    Toast.makeText(this@MainActivity, "点击用户 ${matchResult.value}", Toast.LENGTH_SHORT).show()
                }
            }.replaceSpan("#[^@]+?(?=\\s|\$)".toRegex()) { matchResult ->
                HighlightSpan("#4a70d2", Typeface.defaultFromStyle(Typeface.BOLD)) {
                    Toast.makeText(this@MainActivity, "点击标签 ${matchResult.value}", Toast.LENGTH_SHORT).show()
                }
            }

        // 仅替换第一个匹配项
        binding.tv3.text = "隐私权政策 | 隐私权政策 | 品牌指南".replaceSpanFirst("隐私权政策") {
            SpannableString("用户协议").setSpan(URLSpan("https://github.com/")) // 替换文字和效果
        }

        // 添加一个字符串+Span, 注意括号保证函数执行优先级
        binding.tv4.text = ("隐私权政策 | 许可 | 品牌指南" + " | ").addSpan("官网", listOf(ColorSpan(Color.BLUE), StyleSpan(Typeface.BOLD)))

        // 通过拼接方式展示价格
        binding.tv5.text = "¥".setSpan(ColorSpan("#ed6a2c"))
            .addSpan("39.9", arrayOf(ColorSpan("#ed6a2c"), AbsoluteSizeSpan(18, true)))
            .addSpan(" 1000+ 人付款")
            .addSpan("image", CenterImageSpan(this, R.drawable.ic_touch).setDrawableSize(20.dp).setMarginHorizontal(4.dp)).addSpan("点击付款")

        // 通过替换方式展示价格
        binding.tv6.text = "头像".setSpan(
            GlideImageSpan(binding.tv6, "https://avatars.githubusercontent.com/u/21078112?v=4")
                .setRequestOption(RequestOptions.circleCropTransform()) // 圆形裁剪图片
                .setAlign(GlideImageSpan.Align.BOTTOM)
                .setDrawableSize(50.dp)
        ).addSpan("¥39.9 1000+ 人付款 ")
            .replaceSpan("¥[\\d\\.]+".toRegex()) { // 匹配价格颜色(包含货币符号)
                ColorSpan("#479fd1")
            }.replaceSpanFirst("[\\d\\.]+".toRegex()) { // 匹配价格字号
                AbsoluteSizeSpan(18, true)
            }

        // GIF图文混排
        binding.tv7.text = "播放GIF动画[晕]表情".replaceSpan("[晕]") {
            GlideImageSpan(binding.tv7, R.drawable.ic_gif).setDrawableSize(50.dp)
        }

        binding.tv8.text = "使用".addSpan(
            "Shape",
            listOf(
                CenterImageSpan(this, R.drawable.bg_label)
                    .setDrawableSize(-1)
                    .setPadding(10.dp, 5.dp, 10.dp, 5.dp)
                    .setTextVisibility(),
                ColorSpan(Color.WHITE),
                AbsoluteSizeSpan(40, true),
                StyleSpan(Typeface.BOLD)
            )
        ).addSpan("作为可伸展标签背景")

        binding.tv9.text = "自适应点九图片".setSpan(
            listOf(
                CenterImageSpan(this, R.drawable.bg_msg_bubble_left)
                    .setDrawableSize(-1)
                    .setTextVisibility(),
                ColorSpan(Color.WHITE),
                AbsoluteSizeSpan(30, true),
                StyleSpan(Typeface.BOLD)
            )
        ).addSpan("适用于可伸展PNG")
    }
}





