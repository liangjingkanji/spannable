<p align="center"> <strong>Spannable和创建字符串一样简单</strong> </p>

<br>
<p align="center">
<img src="https://raw.githubusercontent.com/liangjingkanji/spannable/master/preview_img.png" width="450"/><br>
<img src="https://user-images.githubusercontent.com/21078112/184396518-4022db12-0fa9-48a0-97c1-22960db9362b.png" width="350"/>
</p>

<p align="center">
<a href="https://jitpack.io/#liangjingkanji/spannable"><img src="https://jitpack.io/v/liangjingkanji/spannable.svg"/></a>
<img src="https://img.shields.io/badge/language-kotlin-orange.svg"/>
<img src="https://img.shields.io/badge/license-MIT-blue"/>
<img src="https://raw.githubusercontent.com/liangjingkanji/liangjingkanji/master/img/group.svg"/>
</p>

<p align="center">
<a href="https://github.com/liangjingkanji/spannable/releases/latest/download/spannale-sample.apk">下载体验</a>
</p>

<br>
<p align="center">
<img src="https://user-images.githubusercontent.com/21078112/163671712-0a8644b3-8875-489e-a1e5-f8f3215ff4fc.png" width="550"/>
</p>
<br>

真正让Spannable和字符串一样易用, 快速构建常见的富文本/图文混排/表情包/图标, 也是全网第一个实现**正则替换**和图文混排的工具

## 特点

- [x] 低学习成本(仅四个函数)
- [x] 首个支持替换/正则/反向捕获组Span的库
- [x] 全部使用CharSequence接口, 使用起来和字符串没有区别
- [x] 没有自定义控件/没有多余函数
- [x] 快速实现图文混排/富文本/自定义表情包/图标
- [x] 输入框富文本/表情包, 可监听剪贴板粘贴/手动输入文本渲染

## 函数

使用的函数非常简单

| 函数             | 介绍                                    |
| ---------------- | --------------------------------------- |
| setSpan          | 设置Span                                |
| addSpan          | 添加/插入Span或字符串                   |
| replaceSpan      | 替换/正则匹配Span或字符串               |
| replaceSpanFirst/replaceSpanLast | 替换第一个/最后一个匹配的Span或字符串 |

## 文本效果-Span
本框架会收集一些常用的Span效果实现, **欢迎贡献代码**

| Span | 描述 |
|-|-|
| CenterImageSpan | 垂直对齐方式/图片宽高/固定图片比例/显示文字/自适应文字宽高/Shape/.9图 |
| GlideImageSpan | 网络图片/GIF动画/垂直对齐方式/图片宽高/固定图片比例/显示文字/自适应文字宽高, Require [Glide](https://github.com/bumptech/glide) |
| MarginSpan | 文字间距 |
| ColorSpan | 快速创建文字颜色 |
| HighlightSpan | 创建字体颜色/字体样式/可点击效果 |
| ClickableMovementMethod | 等效LinkMovementMethod, 但没有点击背景色 |



本工具将保持简单和扩展性, 如果你想使用dsl构建span可以使用[SpannableX](https://github.com/TxcA/SpannableX)

## 安装

在项目根目录的 settings.gradle 添加仓库

```kotlin
dependencyResolutionManagement {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

然后在 module 的 build.gradle 添加依赖框架

```groovy
implementation 'com.github.liangjingkanji:spannable:1.2.7'
```



## License

```
MIT License

Copyright (c) 2023 劉強東

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
