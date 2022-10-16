package com.drake.spannable.factory

import android.annotation.SuppressLint
import android.text.Editable
import androidx.annotation.GuardedBy
import com.drake.spannable.factory.SpannableBuilder.Companion.create

/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ /**
 * EditableFactory used to improve editing operations on an EditText.
 *
 *
 * EditText uses DynamicLayout, which attaches to the Spannable instance that is being edited using
 * ChangeWatcher. ChangeWatcher implements SpanWatcher and Textwatcher. Currently every delete/add
 * operation is reported to DynamicLayout, for every span that has changed. For each change,
 * DynamicLayout performs some expensive computations. i.e. if there is 100 EmojiSpans and the first
 * span is deleted, DynamicLayout gets 99 calls about the change of position occurred in the
 * remaining spans. This causes a huge delay in response time.
 *
 *
 * Since "android.text.DynamicLayout$ChangeWatcher" class is not a public class,
 * ImageEditableFactory checks if the watcher is in the classpath, and if so uses the modified
 * Spannable which reduces the total number of calls to DynamicLayout for operations that affect
 * EmojiSpans.
 *
 * @see SpannableBuilder
 */
class ImageEditableFactory @SuppressLint("PrivateApi") private constructor() :
    Editable.Factory() {
    init {
        try {
            val className = "android.text.DynamicLayout\$ChangeWatcher"
            sWatcherClass = Class.forName(className, false, javaClass.classLoader)
        } catch (t: Throwable) {
            // ignore
        }
    }

    override fun newEditable(source: CharSequence): Editable {
        return if (sWatcherClass != null) {
            create(
                sWatcherClass!!,
                source
            )
        } else super.newEditable(
            source
        )
    }

    companion object {
        private val INSTANCE_LOCK = Any()

        @GuardedBy("INSTANCE_LOCK")
        @Volatile
        private var sInstance: Editable.Factory? = null
        private var sWatcherClass: Class<*>? = null
        val instance: Editable.Factory?
            get() {
                if (sInstance == null) {
                    synchronized(INSTANCE_LOCK) {
                        if (sInstance == null) {
                            sInstance = ImageEditableFactory()
                        }
                    }
                }
                return sInstance
            }
    }
}