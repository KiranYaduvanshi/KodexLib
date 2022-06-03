package com.kodextech.project.kodexlib.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import com.kodextech.project.kodexlib.R

fun Context.print(message: Any?, tag: String = "Log Application") {
    Log.d("${tag}_${getString(R.string.app_name)}", message?.toString() ?: "")
}

fun print(message: Any?, tag: String = "Log Application") {
    Log.d(tag, message?.toString() ?: "")
}

fun Context.drawableToBitmap(drawable: Drawable): Bitmap {
    val size = (60 * resources.displayMetrics.density).toInt()
    val mutableBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(mutableBitmap)
    drawable.setBounds(0, 0, size, size)
    drawable.draw(canvas)
    return mutableBitmap
}

fun Float.toPx(context: Context) = (this * context.resources.displayMetrics.scaledDensity + 0.5F)

const val kSuccess = "success"
const val kLocality = "locality"
const val kAdministrativeAreaLevel3 = "administrative_area_level_3"
const val kAdministrativeAreaLevel1 = "administrative_area_level_1"// MARK: For Country Name
const val kAdministrativeAreaLevel2 = "administrative_area_level_2"// MARK: For State
const val kPostalCode = "postal_code"// MARK: For Postal Code
const val kPhoneValidError = "Please enter valid phone number."
const val kAllFieldError = "Please fill the field and check the confirmation"
const val kAllFieldErrors = "Please fill the fields"
const val kSplashTimer: Long = 7000
