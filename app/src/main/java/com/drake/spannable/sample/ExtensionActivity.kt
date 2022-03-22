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

package com.drake.spannable.sample

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.drake.spannable.*
import com.drake.spannable.model.drawableSize
import com.drake.spannable.model.sp
import com.drake.spannable.model.toReplaceSpan
import com.drake.spannable.sample.databinding.ActivityMainBinding

/**
 * Span Extension sample
 */
class ExtensionActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home){
            finish()
            true
        } else super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.subtitle = this.javaClass.simpleName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 开启全部可点击
        // binding.activateAllTextViewClick()
        // activateAllTextViewClick()
        // binding.tvImage.activateClick()

        // 图片Span
        binding.tvImage.text = spanImage(this, R.mipmap.ic_launcher, binding.tvImage) + " spannable".spanColor(ContextCompat.getColor(this,R.color.colorPrimaryDark))

        // 替换Span
        binding.tv.activateClick(false).text = "隐私权政策 | 许可 | 品牌指南".spanURL("https://github.com/", "隐私权政策".toReplaceSpan())

        // 自动设置网址
        binding.tv1.activateClick().text = "打开官网: https://github.com/" // 地址/邮箱/手机号码等匹配可以不使用Span, 可以在xml中指定autoLink属性, 会有点击背景色

        // 使用正则匹配
        binding.tv2.activateClick(false).text = "我们可以艾特用户@刘强东 或者创建#热门标签"
            .spanHighlight(colorString = "#ed6a2c", replaceSpan = "@[^@]+?(?=\\s|\$)".toReplaceSpan(true)) { _, matchText ->
                Toast.makeText(
                    this@ExtensionActivity,
                    "点击用户 $matchText",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .spanHighlight(colorString = "#4a70d2", typeface = Typeface.defaultFromStyle(Typeface.BOLD), replaceSpan = "#[^#]+?(?=\\s|\$)".toReplaceSpan(true)) { _, matchText ->
                Toast.makeText(
                    this@ExtensionActivity,
                    "点击标签 $matchText",
                    Toast.LENGTH_SHORT
                ).show()
            } + "@liangjingkanji".spanHighlight(colorString = "#ed6a2c") { _, matchText ->
            Toast.makeText(
                this@ExtensionActivity,
                "点击用户 $matchText",
                Toast.LENGTH_SHORT
            ).show()
        }

        // 仅替换第一个匹配项
        binding.tv3.text = "隐私权政策 | 隐私权政策 | 品牌指南".spanURL("https://github.com/", "隐私权政策".toReplaceSpan(matchQuantity = 1, newString = "用户协议")) // 替换文字和效果

        // 添加一个字符串+Span, 注意.span保证函数通过 [Spannable#Spannable.plus] 拼接
        binding.tv4.text = ("隐私权政策 | 许可 | 品牌指南" + " | ").span + "官网".spanStyle(Typeface.BOLD).spanColor(Color.BLUE)

        // 通过拼接方式展示价格
        binding.tv5.text = "¥".spanColor("#ed6a2c") +
            "39.9".spanColor("#ed6a2c").spanAbsoluteSize(18, true) +
            " 1000+ 人付款"

        // 通过替换方式展示价格
        binding.tv6.text = "¥39.9 1000+ 人付款".spanColor("#ed6a2c","¥[\\d\\.]+".toReplaceSpan(true, 1))
            .spanAbsoluteSize(18,true,"[\\d\\.]+".toReplaceSpan(true,1))

        // 常用效果展示
        binding.tv7.isVisible = true
        binding.tv7.activateClick(true)
        binding.tv7.text = "All SpanExtension\n".spanStyle(Typeface.BOLD).spanColor(Color.RED, "SpanExtension".toReplaceSpan()) +
                "spanStyle".spanStyle(Typeface.BOLD or Typeface.ITALIC) +
                "spanTypeface".spanTypeface(Typeface.createFromAsset(assets,"NotoSans.ttf")) +
                "spanTextAppearance".spanTextAppearance(Typeface.ITALIC, 14.sp, Color.RED,"serif", ColorStateList.valueOf(Color.BLUE)) +
                "spanColor".spanColor(ContextCompat.getColor(this, R.color.colorPrimary)) +
                "spanBackground".spanBackground(ContextCompat.getColor(this, R.color.colorAccent)) +
                spanImage(this, R.mipmap.ic_launcher, binding.tv7) +
                spanImage(this, R.mipmap.ic_launcher, size = 14.sp.drawableSize) +
                "Image X".spanImage(this,R.mipmap.ic_launcher, binding.tv7, replaceSpan = "X".toReplaceSpan()) +
                "spanScaleX".spanScaleX(2.0f, "X".toReplaceSpan()) +
                "spanBlurMask".spanBlurMask(5.0f) +
                "spanSuperscriptSubscriptTopBottom".spanSuperscript("Top".toReplaceSpan()).spanSubscript("Bottom".toReplaceSpan()) +
                "spanAbsoluteSize".spanAbsoluteSize(14, true) +
                "spanRelativeSize".spanRelativeSize(1.5f) +
                "spanStrikethrough".spanStrikethrough() +
                "spanUnderline".spanUnderline() +
                "spanURL".spanURL("https://github.com/liangjingkanji/spannable") +
                "spanSuggestion".spanHighlight{ _, _->
                    binding.et.setText("刘")
                    Toast.makeText(this, "点击EditText中的刘", Toast.LENGTH_SHORT).show()
                } +
                "spanHighlightSpan".spanHighlight(Color.BLUE, typeface = Typeface.DEFAULT){ _, matchText->
                    Toast.makeText(this, "click $matchText", Toast.LENGTH_SHORT).show()
                }

        // 输入提示
        binding.et.isVisible = true
        binding.et.addTextChangedListener {
            it ?: return@addTextChangedListener
            if (it.contains("刘")){
              it.spanSuggestion(this,"刘强东","liangjingkanji","https://github.com/liangjingkanji/", flags = 1)
            }
        }
    }
}





