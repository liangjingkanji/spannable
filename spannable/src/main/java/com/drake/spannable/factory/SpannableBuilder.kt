package com.drake.spannable.factory

import android.annotation.SuppressLint
import android.os.Build
import android.text.Editable
import android.text.SpanWatcher
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ImageSpan
import androidx.annotation.RestrictTo
import androidx.core.util.Preconditions
import java.util.concurrent.atomic.AtomicInteger

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
 * When setSpan functions is called on EmojiSpannableBuilder, it checks if the mObject is instance
 * of the DynamicLayout$ChangeWatcher. if so, it wraps it into another listener mObject
 * (WatcherWrapper) that implements the same interfaces.
 *
 *
 * During a span change event WatcherWrapper’s functions are fired, it checks if the span is an
 * EmojiSpan, and prevents the ChangeWatcher being fired for that span. WatcherWrapper informs
 * ChangeWatcher only once at the end of the edit. Important point is, the block operation is
 * applied only for EmojiSpans. Therefore any other span change operation works the same way as in
 * the framework.
 *
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@SuppressLint("RestrictedApi")
class SpannableBuilder : SpannableStringBuilder {
    /**
     * DynamicLayout$ChangeWatcher class.
     */
    private val mWatcherClass: Class<*>

    /**
     * All WatcherWrappers.
     */
    private val mWatchers: MutableList<WatcherWrapper> = ArrayList()

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    internal constructor(watcherClass: Class<*>) {
        Preconditions.checkNotNull(watcherClass, "watcherClass cannot be null")
        mWatcherClass = watcherClass
    }

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    internal constructor(watcherClass: Class<*>, text: CharSequence) : super(text) {
        Preconditions.checkNotNull(watcherClass, "watcherClass cannot be null")
        mWatcherClass = watcherClass
    }

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    internal constructor(
        watcherClass: Class<*>, text: CharSequence, start: Int,
        end: Int
    ) : super(text, start, end) {
        Preconditions.checkNotNull(watcherClass, "watcherClass cannot be null")
        mWatcherClass = watcherClass
    }

    /**
     * Checks whether the mObject is instance of the DynamicLayout$ChangeWatcher.
     *
     * @param object mObject to be checked
     *
     * @return true if mObject is instance of the DynamicLayout$ChangeWatcher.
     */
    private fun isWatcher(`object`: Any?): Boolean {
        return `object` != null && isWatcher(`object`.javaClass)
    }

    /**
     * Checks whether the class is DynamicLayout$ChangeWatcher.
     *
     * @param clazz class to be checked
     *
     * @return true if class is DynamicLayout$ChangeWatcher.
     */
    private fun isWatcher(clazz: Class<*>): Boolean {
        return mWatcherClass == clazz
    }

    @SuppressLint("UnknownNullness")
    override fun subSequence(start: Int, end: Int): CharSequence {
        return SpannableBuilder(mWatcherClass, this, start, end)
    }

    /**
     * If the span being added is instance of DynamicLayout$ChangeWatcher, wrap the watcher in
     * another internal watcher that will prevent EmojiSpan events to be fired to DynamicLayout. Set
     * this new mObject as the span.
     */
    override fun setSpan(what: Any?, start: Int, end: Int, flags: Int) {
        var what = what
        if (isWatcher(what)) {
            val span = WatcherWrapper(what!!)
            mWatchers.add(span)
            what = span
        }
        super.setSpan(what, start, end, flags)
    }

    /**
     * If previously a DynamicLayout$ChangeWatcher was wrapped in a WatcherWrapper, return the
     * correct Object that the client has set.
     */
    @SuppressLint("UnknownNullness")
    override fun <T : Any?> getSpans(queryStart: Int, queryEnd: Int, kind: Class<T>?): Array<T> {
        if (isWatcher(kind)) {
            val spans = super.getSpans(
                queryStart, queryEnd,
                WatcherWrapper::class.java
            )
            val result = java.lang.reflect.Array.newInstance(kind, spans.size) as Array<T>
            for (i in spans.indices) {
                result[i] = spans[i].mObject as T
            }
            return result
        }
        return super.getSpans(queryStart, queryEnd, kind)
    }

    /**
     * If the client wants to remove the DynamicLayout$ChangeWatcher span, remove the WatcherWrapper
     * instead.
     */
    override fun removeSpan(what: Any?) {
        var what = what
        val watcher: WatcherWrapper?
        if (isWatcher(what)) {
            watcher = getWatcherFor(what)
            if (watcher != null) {
                what = watcher
            }
        } else {
            watcher = null
        }
        super.removeSpan(what)
        if (watcher != null) {
            mWatchers.remove(watcher)
        }
    }

    /**
     * Return the correct start for the DynamicLayout$ChangeWatcher span.
     */
    override fun getSpanStart(tag: Any?): Int {
        var tag = tag
        if (isWatcher(tag)) {
            val watcher = getWatcherFor(tag)
            if (watcher != null) {
                tag = watcher
            }
        }
        return super.getSpanStart(tag)
    }

    /**
     * Return the correct end for the DynamicLayout$ChangeWatcher span.
     */
    override fun getSpanEnd(tag: Any?): Int {
        var tag = tag
        if (isWatcher(tag)) {
            val watcher = getWatcherFor(tag)
            if (watcher != null) {
                tag = watcher
            }
        }
        return super.getSpanEnd(tag)
    }

    /**
     * Return the correct flags for the DynamicLayout$ChangeWatcher span.
     */
    override fun getSpanFlags(tag: Any?): Int {
        var tag = tag
        if (isWatcher(tag)) {
            val watcher = getWatcherFor(tag)
            if (watcher != null) {
                tag = watcher
            }
        }
        return super.getSpanFlags(tag)
    }

    /**
     * Return the correct transition for the DynamicLayout$ChangeWatcher span.
     */
    override fun nextSpanTransition(start: Int, limit: Int, type: Class<*>?): Int {
        var type = type
        if (type == null || isWatcher(type)) {
            type = WatcherWrapper::class.java
        }
        return super.nextSpanTransition(start, limit, type)
    }

    /**
     * Find the WatcherWrapper for a given DynamicLayout$ChangeWatcher.
     *
     * @param object DynamicLayout$ChangeWatcher mObject
     *
     * @return WatcherWrapper that wraps the mObject.
     */
    private fun getWatcherFor(`object`: Any?): WatcherWrapper? {
        for (i in mWatchers.indices) {
            val watcher = mWatchers[i]
            if (watcher.mObject === `object`) {
                return watcher
            }
        }
        return null
    }

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun beginBatchEdit() {
        blockWatchers()
    }

    /**
     * @hide
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun endBatchEdit() {
        unblockwatchers()
        fireWatchers()
    }

    /**
     * Block all watcher wrapper events.
     */
    private fun blockWatchers() {
        for (i in mWatchers.indices) {
            mWatchers[i].blockCalls()
        }
    }

    /**
     * Unblock all watcher wrapper events.
     */
    private fun unblockwatchers() {
        for (i in mWatchers.indices) {
            mWatchers[i].unblockCalls()
        }
    }

    /**
     * Unblock all watcher wrapper events. Called by editing operations, namely
     * [SpannableStringBuilder.replace].
     */
    private fun fireWatchers() {
        for (i in mWatchers.indices) {
            mWatchers[i].onTextChanged(this, 0, length, length)
        }
    }

    @SuppressLint("UnknownNullness")
    override fun replace(start: Int, end: Int, tb: CharSequence): SpannableStringBuilder {
        blockWatchers()
        super.replace(start, end, tb)
        unblockwatchers()
        return this
    }

    @SuppressLint("UnknownNullness")
    override fun replace(
        start: Int, end: Int, tb: CharSequence, tbstart: Int,
        tbend: Int
    ): SpannableStringBuilder {
        blockWatchers()
        super.replace(start, end, tb, tbstart, tbend)
        unblockwatchers()
        return this
    }

    @SuppressLint("UnknownNullness")
    override fun insert(where: Int, tb: CharSequence): SpannableStringBuilder {
        super.insert(where, tb)
        return this
    }

    @SuppressLint("UnknownNullness")
    override fun insert(
        where: Int,
        tb: CharSequence,
        start: Int,
        end: Int
    ): SpannableStringBuilder {
        super.insert(where, tb, start, end)
        return this
    }

    @SuppressLint("UnknownNullness")
    override fun delete(start: Int, end: Int): SpannableStringBuilder {
        super.delete(start, end)
        return this
    }

    override fun append(@SuppressLint("UnknownNullness") text: CharSequence): SpannableStringBuilder {
        super.append(text)
        return this
    }

    override fun append(text: Char): SpannableStringBuilder {
        super.append(text)
        return this
    }

    override fun append(
        @SuppressLint("UnknownNullness") text: CharSequence,
        start: Int,
        end: Int
    ): SpannableStringBuilder {
        super.append(text, start, end)
        return this
    }

    @SuppressLint("UnknownNullness")
    override fun append(text: CharSequence, what: Any, flags: Int): SpannableStringBuilder {
        super.append(text, what, flags)
        return this
    }

    /**
     * Wraps a DynamicLayout$ChangeWatcher in order to prevent firing of events to DynamicLayout.
     */
    private class WatcherWrapper internal constructor( /* synthetic access */val mObject: Any) :
        TextWatcher, SpanWatcher {
        private val mBlockCalls = AtomicInteger(0)
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            (mObject as TextWatcher).beforeTextChanged(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            (mObject as TextWatcher).onTextChanged(s, start, before, count)
        }

        override fun afterTextChanged(s: Editable) {
            (mObject as TextWatcher).afterTextChanged(s)
        }

        /**
         * Prevent the onSpanAdded calls to DynamicLayout$ChangeWatcher if in a replace operation
         * (mBlockCalls is set) and the span that is added is an EmojiSpan.
         */
        override fun onSpanAdded(text: Spannable, what: Any, start: Int, end: Int) {

            (mObject as SpanWatcher).onSpanAdded(text, what, start, end)
        }

        /**
         * Prevent the onSpanRemoved calls to DynamicLayout$ChangeWatcher if in a replace operation
         * (mBlockCalls is set) and the span that is added is an EmojiSpan.
         */
        override fun onSpanRemoved(text: Spannable, what: Any, start: Int, end: Int) {
            if (mBlockCalls.get() > 0 && isImageSpan(what)) {
                return
            }
            (mObject as SpanWatcher).onSpanRemoved(text, what, start, end)
        }

        /**
         * Prevent the onSpanChanged calls to DynamicLayout$ChangeWatcher if in a replace operation
         * (mBlockCalls is set) and the span that is added is an EmojiSpan.
         */
        override fun onSpanChanged(
            text: Spannable, what: Any, ostart: Int, oend: Int, nstart: Int,
            nend: Int
        ) {
            var ostart = ostart
            var nstart = nstart
            if (mBlockCalls.get() > 0 && isImageSpan(what)) {
                return
            }
            // workaround for platform bug fixed in Android P
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                // b/67926915 start cannot be determined, fallback to reflow from start instead
                // of causing an exception.

                // emoji2 bug b/216891011
                if (ostart > oend) {
                    ostart = 0
                }
                if (nstart > nend) {
                    nstart = 0
                }
            }
            (mObject as SpanWatcher).onSpanChanged(text, what, ostart, oend, nstart, nend)
        }

        fun blockCalls() {
            mBlockCalls.incrementAndGet()
        }

        fun unblockCalls() {
            mBlockCalls.decrementAndGet()
        }

        private fun isImageSpan(span: Any): Boolean {
            return span is ImageSpan
        }
    }

    companion object {
        /**
         * @hide
         */
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        fun create(clazz: Class<*>, text: CharSequence): SpannableBuilder {
            return SpannableBuilder(clazz, text)
        }
    }
}