package com.kodextech.project.kodexlib.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Patterns
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


fun String.doubleToStringNoDecimal(d: Double): String {
    val formatter = NumberFormat.getInstance(Locale.getDefault()) as DecimalFormat
    formatter.applyPattern("#,###")

    return formatter.format(d)
}

fun String.openMessenger(mContext: Context) {
    val openURL =
        Intent(Intent.ACTION_VIEW, Uri.parse("https://m.me/$this"))
    mContext.startActivity(Intent.createChooser(openURL, "Open"))
}

fun String.openDialer(mContext: Context) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:${this}")
    mContext.startActivity(Intent.createChooser(intent, "Dialer"))
}

fun AppCompatEditText.text(value: String) {
    this.setText(value)
}

fun AppCompatTextView.text(value: String) {
    this.text = value
}

fun Any?.toString() = this?.toString() ?: ""

fun Any?.toStringHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this?.toString() ?: "", Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(this?.toString() ?: "")
    }
}

fun AppCompatTextView.setTextValue(value: CharSequence?) {
    val txt = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(value?.toString() ?: "", Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(value?.toString() ?: "")
    }
    this.text = txt
}

fun TextView.setTextValue(value: CharSequence?) {
    val txt = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(value?.toString() ?: "", Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(value?.toString() ?: "")
    }
    this.text = txt
}

fun String?.getStringWhole(): String {
    return this?.trim() ?: ""
}

fun Editable?.getStringWhole(): String {
    return this?.toString()?.trim() ?: ""
}

fun String?.capitalized(): String {
    return this?.substring(0, 1)?.uppercase(Locale.getDefault()) + this?.substring(1)
}

fun Double.roundOff(d: Float, decimalPlace: Int): Float {
    var bd = BigDecimal(d.toString())
    bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP)
    return bd.toFloat()
}

fun String?.isValidEmail(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this ?: "").matches()
}

fun String?.isValidPassword(): Boolean {
    this?.let {
        val passwordPattern =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
        val passwordMatcher = Regex(passwordPattern)

        return passwordMatcher.find(this ?: "") != null
    } ?: return false
}

fun String?.isValidPhone(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.PHONE.matcher(this ?: "").matches()
}

//
//fun Context.getCountryDialCode(): String? {
//    var contryId: String? = null
//    var contryDialCode: String? = "1"
//    val telephonyMngr = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//    contryId = telephonyMngr.simCountryIso.toUpperCase(Locale.getDefault())
//    val arrContryCode: Array<String> =
//        this.resources.getStringArray(R.array.DialingCountryCode)
//    for (i in arrContryCode.indices) {
//        val arrDial = arrContryCode[i].split(",".toRegex()).toTypedArray()
//        if (arrDial[1].trim { it <= ' ' } == contryId.trim()) {
//            contryDialCode = arrDial[0]
//            break
//        }
//    }
//    return contryDialCode ?: "1"
//}

// Function to validate image file extension .
fun String?.imageFile(): Boolean {
    // Regex to check valid image file extension.
    val regex = "([^\\s]+(\\.(?i)(jpe?g|png|gif|bmp|svg))$)"
    // Compile the ReGex
    val p: Pattern = Pattern.compile(regex)
    // If the string is empty
    // return false
    if (this == null) {
        return false
    }

    // Pattern class contains matcher() method
    // to find matching between given string
    // and regular expression.
    val m: Matcher = p.matcher(this)

    // Return if the string
    // matched the ReGex
    return m.matches()
}