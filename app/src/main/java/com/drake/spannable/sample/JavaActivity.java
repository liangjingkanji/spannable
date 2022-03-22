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

package com.drake.spannable.sample;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.drake.spannable.Span;
import com.drake.spannable.SpannableExtension;
import com.drake.spannable.model.DrawableSizeKt;
import com.drake.spannable.sample.databinding.ActivityMainBinding;

/**
 * Span Java sample
 */
public class JavaActivity extends AppCompatActivity {

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setSubtitle(this.getClass().getSimpleName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 图片Span
        binding.tvImage.setText(Span.create()
                .image(this, R.mipmap.ic_launcher, binding.tvImage)
                .text(" spannable").color(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .spannable());

        // 替换Span
        Span.activateClick(binding.tv, false).setText(Span.create()
                .text("隐私权政策 | 许可 | 品牌指南").url("https://github.com/", Span.builderReplace("隐私权政策"))
                .spannable());

        // 自动设置网址
        Span.activateClick(binding.tv1).setText("打开官网: https://github.com/");

        // 使用正则匹配
        Span.activateClick(binding.tv2, false).setText(Span.create()
                .text("我们可以艾特用户@刘强东 或者创建#热门标签 ")
                .highlight("#ed6a2c",
                        null, Span.builderReplace("@[^@]+?(?=\\s|\\$)", true),
                        (v, matchText) ->
                                Toast.makeText(JavaActivity.this, "点击用户 " + matchText, Toast.LENGTH_SHORT).show())
                .highlight("#4a70d2",
                        Typeface.defaultFromStyle(Typeface.BOLD), Span.builderReplace("#[^#]+?(?=\\s|\\$)", true),
                        (v, matchText) -> Toast.makeText(JavaActivity.this, "点击标签 " + matchText, Toast.LENGTH_SHORT).show())

                .text("@liangjingkanji").highlight("#ed6a2c", null, null, (v, matchText) ->
                        Toast.makeText(JavaActivity.this, "点击用户 " + matchText, Toast.LENGTH_SHORT).show())
                .spannable()
        );

        // 仅替换第一个匹配项
        binding.tv3.setText(Span.create()
                .text("隐私权政策 | 隐私权政策 | 品牌指南").url("https://github.com/", Span.builderReplace("隐私权政策", false, 1))
                .spannable()
        );

        // 添加一个字符串+Span
        binding.tv4.setText(Span.create()
                .text("隐私权政策 | 许可 | 品牌指南" + " | ")
                .text("官网").style(Typeface.BOLD).color(Color.BLUE)
                .spannable()
        );

        // 通过拼接方式展示价格
        binding.tv5.setText(Span.create()
                .text("¥").color("#ed6a2c")
                .text("39.9").color("#ed6a2c").absoluteSize(18, true)
                .text(" 1000+ 人付款")
                .spannable()

        );

        // 通过替换方式展示价格
        binding.tv6.setText(Span.create()
                .text("¥39.9 1000+ 人付款")
                .color("#ed6a2c", Span.builderReplace("¥[\\d\\.]+", true, 1))
                .absoluteSize(18, true, Span.builderReplace("¥[\\d\\.]+", true, 1))
                .spannable());

        // 常用效果展示
        binding.tv7.setVisibility(View.VISIBLE);
        Span.activateClick(binding.tv7, true).setText(Span.create()
                .text("All SpanExtension\n").style(Typeface.BOLD).color(Color.RED, Span.builderReplace("SpanExtension"))
                .text("spanStyle").style(Typeface.BOLD | Typeface.ITALIC)
                .text("spanTypeface").typeface(Typeface.createFromAsset(getAssets(), "NotoSans.ttf"))
                .text("spanTextAppearance").textAppearance(Typeface.ITALIC, DrawableSizeKt.getSp(14), Color.RED, "serif", ColorStateList.valueOf(Color.BLUE))
                .text("spanColor").color(ContextCompat.getColor(this, R.color.colorPrimary))
                .text("spanBackground").background(ContextCompat.getColor(this, R.color.colorAccent)).saveCache()
                .image(this, R.mipmap.ic_launcher, binding.tv7).saveCache()
                .image(this, R.mipmap.ic_launcher, null, DrawableSizeKt.getDrawableSize(DrawableSizeKt.getSp(14)))
                .text("Image X").image(this, R.mipmap.ic_launcher, binding.tv7, null, Span.builderReplace("X"))
                .text("spanScaleX").scaleX(2.0f, Span.builderReplace("X"))
                .text("spanBlurMask").blurMask(5.0f)
                .text("spanSuperscriptSubscriptTopBottom").superscript(Span.builderReplace("Top")).subscript(Span.builderReplace("Bottom"))
                .text("spanAbsoluteSize").absoluteSize(14, true)
                .text("spanRelativeSize").relativeSize(1.5f)
                .text("spanStrikethrough").strikethrough()
                .text("spanUnderline").underline()
                .text("spanURL").url("https://github.com/liangjingkanji/spannable")
                .text("spanSuggestion").highlight((String) null, null, null, (v, text) -> {
                            binding.et.setText("刘");
                            Toast.makeText(this, "点击EditText中的刘", Toast.LENGTH_SHORT).show();
                        }
                )
                .text("spanHighlightSpan").highlight(Color.BLUE, Typeface.DEFAULT, null, (v, text) ->
                        Toast.makeText(this, "click $matchText", Toast.LENGTH_SHORT).show()
                )
                .spannable()
        );


        // 输入提示
        binding.et.setVisibility(View.VISIBLE);
        binding.et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null) {
                    return;
                }

                if (s.toString().contains("刘")) {
                    SpannableExtension.spanSuggestion(s, JavaActivity.this, new String[]{"刘强东", "liangjingkanji", "https://github.com/liangjingkanji/"}, 1, null, null, null);
                }
            }
        });
    }
}

