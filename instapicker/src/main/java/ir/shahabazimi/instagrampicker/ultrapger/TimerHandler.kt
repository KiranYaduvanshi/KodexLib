/*
 *
 *  MIT License
 *
 *  Copyright (c) 2017 Alibaba Group
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */
package ir.shahabazimi.instagrampicker.ultrapger

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.SparseIntArray

/**
 * Created by mikeafc on 15/11/25.
 */
class TimerHandler(var mListener: TimerHandlerListener, var interval: Long) : Handler(Looper.getMainLooper()) {
    interface TimerHandlerListener {
        val nextItem: Int
        fun callBack()
    }

    @JvmField
    var specialInterval: SparseIntArray? = null
    var isStopped = true
    override fun handleMessage(msg: Message) {
        if (MSG_TIMER_ID == msg.what) {
            val nextIndex = mListener.nextItem
            mListener.callBack()
            tick(nextIndex)
        }
    }

    fun tick(index: Int) {
        sendEmptyMessageDelayed(MSG_TIMER_ID, getNextInterval(index))
    }

    private fun getNextInterval(index: Int): Long {
        var next = interval
        if (specialInterval != null) {
            val has = specialInterval!![index, -1].toLong()
            if (has > 0) {
                next = has
            }
        }
        return next
    }

    fun setListener(listener: TimerHandlerListener) {
        this.mListener = listener
    }

    fun setSpecialInterval(specialInterval: SparseIntArray?) {
        this.specialInterval = specialInterval
    }

    companion object {
        const val MSG_TIMER_ID = 87108
    }
}