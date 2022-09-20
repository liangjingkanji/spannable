<p align="center"> <strong>Spannable和创建字符串一样简单</strong> </p>

<br>
<p align="center">
<img src="https://raw.githubusercontent.com/liangjingkanji/spannable/master/preview_img.png" width="450"/><br>
<img src="https://user-images.githubusercontent.com/21078112/184396518-4022db12-0fa9-48a0-97c1-22960db9362b.png" width="350"/>
</p>

<p align="center">
<a href="https://jitpack.io/#liangjingkanji/spannable"><img src="https://jitpack.io/v/liangjingkanji/spannable.svg"/></a>
<img src="https://img.shields.io/badge/language-kotlin-orange.svg"/>
<img src="https://img.shields.io/badge/license-Apache-blue"/>
<img src="https://raw.githubusercontent.com/liangjingkanji/liangjingkanji/master/img/group.svg"/>
</p>

<p align="center">
<a href="https://github.com/liangjingkanji/spannable/releases/download/1.2.1/spannale-sample.apk">下载体验</a>
</p>

<br>
<p align="center">
<img src="https://user-images.githubusercontent.com/21078112/163671712-0a8644b3-8875-489e-a1e5-f8f3215ff4fc.png" width="550"/>
</p>
<br>

本工具真正实现让Spannable和String一样易用, 快速实现常见的图文列表/图文混排/表情包需求/文字换行, 同时也是全网第一个实现正则替换和最完美实现图文混排的Span工具, 对于MVVM架构更是如虎添翼, 数据和视图解耦

## 特点

- [x] 低学习成本
- [x] 首个支持替换/正则/捕获组Span的库
- [x] 全部使用CharSequence接口, 使用起来和字符串没有区别
- [x] 没有自定义控件/没有多余函数
- [x] 快速实现图文混排/富文本/自定义表情包
- [x] 输入框富文本/表情包

## 函数

使用的函数非常简单

| 函数             | 介绍                                    |
| ---------------- | --------------------------------------- |
| setSpan          | 设置Span                                |
| addSpan          | 添加/插入Span或字符串                   |
| replaceSpan      | 替换/正则替换Span或字符串               |
| replaceSpanFirst/replaceSpanLast | 替换/正则替换第一个/最后一个匹配项的Span或字符串 |

## Span
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

添加远程仓库根据创建项目的 Android Studio 版本有所不同

Android Studio Arctic Fox以下创建的项目 在项目根目录的 build.gradle 添加仓库

```groovy
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

Android Studio Arctic Fox以上创建的项目 在项目根目录的 settings.gradle 添加仓库

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
implementation 'com.github.liangjingkanji:spannable:1.2.1'
```



## License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
