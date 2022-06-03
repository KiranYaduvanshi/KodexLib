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

import android.graphics.Bitmap
import android.util.SparseIntArray
import ir.shahabazimi.instagrampicker.ultrapger.UltraViewPager.ScrollDirection
import ir.shahabazimi.instagrampicker.ultrapger.UltraViewPager.ScrollMode

/**
 * Created by mikeafc on 15/11/30.
 */
internal interface IUltraViewPagerFeature {
    /**
     * Constructs a indicator with no options. this indicator support set-Method in chained mode.
     * meanwhile focusColor and normalColor are necessary,or the indicator won't be show.
     */
    fun initIndicator(): IUltraIndicatorBuilder?

    /**
     * Set options for indicator
     *
     * @param focusColor    defines the color when indicator is focused.
     * @param normalColor   defines the color when indicator is in the default state (not focused).
     * @param radiusInPixel defines the radius of indicator item.
     * @param gravity       specifies how to align the indicator. for example, using Gravity.BOTTOM | Gravity.RIGHT
     */
    fun initIndicator(
        focusColor: Int,
        normalColor: Int,
        radiusInPixel: Int,
        gravity: Int
    ): IUltraIndicatorBuilder?

    /**
     * Set options for indicator
     *
     * @param focusColor    defines the color when indicator is focused.
     * @param normalColor   defines the color when indicator is in the default state (not focused).
     * @param strokeColor   stroke color
     * @param strokeWidth   stroke width
     * @param radiusInPixel the radius of indicator item.
     * @param gravity       specifies how to align the indicator. for example, using Gravity.BOTTOM | Gravity.RIGHT
     */
    fun initIndicator(
        focusColor: Int,
        normalColor: Int,
        strokeColor: Int,
        strokeWidth: Int,
        radiusInPixel: Int,
        gravity: Int
    ): IUltraIndicatorBuilder?

    /**
     * Set options for indicator
     *
     * @param focusResId  defines the resource id when indicator is focused.
     * @param normalResId defines the resource id  when indicator is in the default state (not focused).
     * @param gravity     specifies how to align the indicator. for example, using Gravity.BOTTOM | Gravity.RIGHT
     */
    fun initIndicator(focusResId: Int, normalResId: Int, gravity: Int): IUltraIndicatorBuilder?

    /**
     * @param focusBitmap  defines the bitmap when indicator is focused
     * @param normalBitmap defines the bitmap when indicator is in the default state (not focused).
     * @param gravity      specifies how to align the indicator. for example, using Gravity.BOTTOM | Gravity.RIGHT
     * @return
     */
    fun initIndicator(
        focusBitmap: Bitmap?,
        normalBitmap: Bitmap?,
        gravity: Int
    ): IUltraIndicatorBuilder?

    /**
     * Remove indicator
     */
    fun disableIndicator()

    /**
     * Enable auto-scroll mode
     *
     * @param intervalInMillis The interval time to scroll in milliseconds.
     */
    fun setAutoScroll(intervalInMillis: Int)

    /**
     * Enable auto-scroll mode with special interval times
     * @param intervalInMillis The default time to scroll
     * @param intervalArray The special interval to scroll, in responding to each frame
     */
    fun setAutoScroll(intervalInMillis: Int, intervalArray: SparseIntArray?)

    /**
     * Disable auto-scroll mode
     */
    fun disableAutoScroll()

    /**
     * Set an infinite loop
     *
     * @param enable enable or disable
     */
    fun setInfiniteLoop(enable: Boolean)

    /**
     * Supply a maximum width for this ViewPager.
     *
     * @param width width
     */
    fun setMaxWidth(width: Int)

    /**
     * Supply a maximum height for this ViewPager.
     *
     * @param height height
     */
    fun setMaxHeight(height: Int)

    /**
     * Set the aspect ratio for UltraViewPager.
     *
     * @param ratio
     */
    fun setRatio(ratio: Float)

    /**
     * Set scroll mode for UltraViewPager.
     *
     * @param scrollMode UltraViewPager.ScrollMode.HORIZONTAL or UltraViewPager.ScrollMode.VERTICAL
     */
    fun setScrollMode(scrollMode: ScrollMode?)

    /**
     * Disable scroll direction. the default value is ScrollDirection.NONE
     *
     * @param direction NONE, BACKWARD, FORWARD
     */
    fun disableScrollDirection(direction: ScrollDirection?)

    /**
     * Scroll to the last page, and return to the first page when the last page is reached.
     */
    fun scrollLastPage(): Boolean

    /**
     * Scroll to the next page, and return to the first page when the last page is reached.
     */
    fun scrollNextPage(): Boolean

    /**
     * Set multi-screen mode , the aspect ratio of PageViewer should less than or equal to 1.0f
     */
    fun setMultiScreen(ratio: Float)

    /**
     * Adjust the height of the ViewPager to the height of child automatically.
     */
    fun setAutoMeasureHeight(status: Boolean)

    /**
     * Adjust the height of child item view with aspect ratio.
     *
     * @param ratio aspect ratio
     */
    fun setItemRatio(ratio: Double)

    /**
     * Set the gap between two pages in pixel
     *
     * @param pixel
     */
    fun setHGap(pixel: Int)

    /**
     * Set item margin
     *
     * @param left   the left margin in pixels
     * @param top    the top margin in pixels
     * @param right  the right margin in pixels
     * @param bottom the bottom margin in pixels
     */
    fun setItemMargin(left: Int, top: Int, right: Int, bottom: Int)

    /**
     * Set margins for this ViewPager
     *
     * @param left  the left margin in pixels
     * @param right the right margin in pixels
     */
    fun setScrollMargin(left: Int, right: Int)

    /**
     * The items.size() would be scale to item.size()*infiniteRatio in fact
     *
     * @param infiniteRatio
     */
    fun setInfiniteRatio(infiniteRatio: Int)
}