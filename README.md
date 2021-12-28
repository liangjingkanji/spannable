<p align="center"><strong>创建SpannableString和创建字符串一样简单</strong></p>



<p align="center">
<a href="https://jitpack.io/#liangjingkanji/spannable"><img src="https://jitpack.io/v/liangjingkanji/spannable.svg"/></a>
<img src="https://img.shields.io/badge/language-kotlin-orange.svg"/>
<img src="https://img.shields.io/badge/license-Apache-blue"/>
<a href="https://jq.qq.com/?_wv=1027&k=vWsXSNBJ"><img src="https://img.shields.io/badge/QQ群-752854893-blue"/></a>
</p>


<br>

<p align="center">
<img src="https://s2.loli.net/2021/12/28/1EQMqYTvwL8Du3G.jpg" width="650"/>
</p>

<br>

<p align="center"><strong>欢迎贡献代码/问题</strong></p>

<br>



## 特点

- 首个支持替换/正则匹配Span的库
- 支持快速设置/添加/插入Span
- 支持正则捕获分组
- 全部使用CharSequence接口, 让字符串和Spanable通用

## 函数

使用的函数非常简单

| 函数             | 介绍                                    |
| ---------------- | --------------------------------------- |
| setSpan          | 设置Span                                |
| addSpan          | 添加/插入Span或字符串                   |
| replaceSpan      | 替换/正则替换Span或字符串               |
| replaceSpanFirst | 替换/正则替换第一个匹配项的Span或字符串 |

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
implementation 'com.github.liangjingkanji:spannable:1.0.1'
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
