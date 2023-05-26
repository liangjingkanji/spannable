package com.drake.spannable.sample

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import com.drake.engine.utils.dp
import com.drake.spannable.addSpan
import com.drake.spannable.clearSpans
import com.drake.spannable.listener.ModifyTextWatcher
import com.drake.spannable.replaceSpan
import com.drake.spannable.sample.base.BaseMenuActivity
import com.drake.spannable.sample.databinding.ActivityRichInputBinding
import com.drake.spannable.span.CenterImageSpan
import com.drake.spannable.span.HighlightSpan
import kotlin.concurrent.thread

class RichInputActivity : BaseMenuActivity(), OnLongClickListener, OnClickListener {

    private val binding by lazy { ActivityRichInputBinding.inflate(layoutInflater) }

    private val inputContent // 输入框内容
        get() = binding.etInput.text

    // 匹配规则, 因为同一个Span对象重复设置仅最后一个有效故每次都得创建新的对象
    private val matchRules = mapOf<Regex, (MatchResult) -> Any?>(
        "@[^@]+?(?=\\s|\$)".toRegex() to { HighlightSpan("#ed6a2c") },
        "#[^@]+?(?=\\s|\$)".toRegex() to { HighlightSpan("#4a70d2", Typeface.defaultFromStyle(Typeface.BOLD)) },
        "蚂蚁".toRegex() to { CenterImageSpan(this, R.drawable.ic_ant).setDrawableSize(50.dp) },
        "生气|angry".toRegex() to { CenterImageSpan(this, R.drawable.ic_angry).setDrawableSize(50.dp) },
        "开心|happy".toRegex() to { CenterImageSpan(this, R.drawable.ic_happy).setDrawableSize(50.dp) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 包含 @用户 #标签 表情包 等自动替换规则
        binding.etInput.addTextChangedListener(object : ModifyTextWatcher() {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // 替换文本时清除span
                s.clearSpans<HighlightSpan>()
                s.clearSpans<CenterImageSpan>()
            }

            override fun onModify(s: Editable) {
                matchRules.forEach { rule ->
                    s.replaceSpan(rule.key, replacement = rule.value)
                }
            }
        })

        // 点击插入表情
        binding.ivAngry.setOnClickListener(this)
        binding.ivHappy.setOnClickListener(this)

        // 长按表情连续添加表情(间隔100毫秒)
        binding.ivAngry.setOnLongClickListener(this)
        binding.ivHappy.setOnLongClickListener(this)
    }

    override fun onLongClick(v: View): Boolean {
        thread {
            while (v.isPressed) {
                Thread.sleep(100)
                runOnUiThread { v.performClick() }
            }
        }
        return true
    }

    override fun onClick(v: View?) {
        val faceStr = when (v) {
            binding.ivAngry -> "生气"
            binding.ivHappy -> "开心"
            else -> return
        }
        binding.etInput.setText(inputContent addSpan faceStr)
        binding.etInput.setSelection(inputContent.length)
    }
}