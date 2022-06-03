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


import android.view.View
import androidx.viewpager.widget.ViewPager

/**
 * Created by mikeafc on 15/12/3.
 */
class UltraDepthScaleTransformer : ViewPager.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        val scale = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position))
        val rotation = MAX_ROTATION * Math.abs(position)
        if (position <= 0f) {
            view.translationX = view.width * -position * 0.19f
            view.pivotY = 0.5f * view.height
            view.pivotX = 0.5f * view.width
            view.scaleX = scale
            view.scaleY = scale
            view.rotationY = rotation
        } else if (position <= 1f) {
            view.translationX = view.width * -position * 0.19f
            view.pivotY = 0.5f * view.height
            view.pivotX = 0.5f * view.width
            view.scaleX = scale
            view.scaleY = scale
            view.rotationY = -rotation
        }
    }

    companion object {
        private const val MIN_SCALE = 0.5f
        private const val MAX_ROTATION = 30f
    }
}