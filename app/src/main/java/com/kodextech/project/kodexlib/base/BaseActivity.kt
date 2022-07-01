@file:Suppress("unused")

package com.kodextech.project.kodexlib.base


import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.kodextech.glitterupsapp.utils.loader.ProgressDialogue
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.utils.HideUtil
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*


@Suppress("unused")
abstract class BaseActivity : AppCompatActivity() {

    //    var progressItem:ProgressDialog
    var mViewGroup: ViewGroup? = null
    private val pd = ProgressDialogue()

    /*For Facebook KeyHash GettingPermissionUtils*/
    @Suppress("DEPRECATION")
    @SuppressLint("PackageManagerGetSignatures")
    fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.packageManager
                .getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
//                Timber.i("printHashKey() Hash Key: $hashKey")
                Log.e("printHashKey()", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("printHashKey()", e.localizedMessage, e)
        } catch (e: java.lang.Exception) {
            Log.e("printHashKey()", e.localizedMessage, e)
        }
    }

    fun View.snack(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun Context.showBarToast(message: String) {
        val toast = Toast(this)
        val contex = this
        toast.apply {
            val layout = LayoutInflater.from(contex).inflate(R.layout.custom_toast, null, false)
            layout?.findViewById<TextView>(R.id.tvMessage)?.text = message
            setGravity(Gravity.BOTTOM, 0, 0)
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }

    fun statusBarColor(color: Int) {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }

    //
    fun makeTopBottomTransparent() {
        val w: Window = window // in Activity's onCreate() for instance
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,// or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )// For Display Image View On Status Bar Background
    }

    fun makeLightContentStatusBar(value: Boolean = true) {
        setSystemUiLightStatusBar(value)
    }

    @Suppress("DEPRECATION")
    private fun setSystemUiLightStatusBar(isLightStatusBar: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val systemUiAppearance = if (isLightStatusBar) {
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                } else {
                    0
                }
                window.insetsController?.setSystemBarsAppearance(
                    systemUiAppearance,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                val systemUiVisibilityFlags = if (isLightStatusBar) {
                    window.decorView.systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.decorView.systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                window.decorView.systemUiVisibility = systemUiVisibilityFlags
            }
        }
    }

/* For Getting Screen Size From Window On Context
    @Suppress("DEPRECATION")
    fun setupScreenWidthHeight():Point {

        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display?.getRealMetrics(displayMetrics)
        } else {
            this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        }
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display
        } else {
            windowManager.defaultDisplay
        }
        val size = Point()
        display?.getRealSize(size)
        return size
        //        DataManager.setDeviceHeight(size.y)
//        DataManager.setDeviceWidth(size.x)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onSetupViewGroup()
        setupContentViewWithBinding()
        setupLoader()
        if (mViewGroup != null) {
            HideUtil.init(this, mViewGroup)
        }
    }

    abstract fun onSetupViewGroup()
    abstract fun setupContentViewWithBinding()
    abstract fun onRecycleBeforeDestroy()
    abstract fun onBecameInvisibleToUser()
    abstract fun onBecameVisibleToUser()
    abstract fun setupLoader()

    override fun onDestroy() {
        onRecycleBeforeDestroy()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        onBecameVisibleToUser()
    }

    override fun onPause() {
        onBecameInvisibleToUser()
        super.onPause()
    }

    fun showLoading(text: String = "Please wait...") {
        runOnUiThread {
            try {
                if (!pd.isVisible && !pd.isAdded) {
                    pd.show(supportFragmentManager, "pd")
                }
                android.os.Handler(Looper.getMainLooper()).postDelayed({
                    pd.txtProgress?.text = text
                }, 200)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    fun hideLoading() {
        try {
            if (pd.isAdded || pd.isVisible) pd.dismiss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    fun String.getFormated(input: String = "yyyy-MM-dd'T'HH:mm:ss"): Date {
        val utcFormat = SimpleDateFormat(input, Locale.getDefault())
//    utcFormat.timeZone = TimeZone.getTimeZone("UTC")
        return utcFormat.parse(this.split(".").first()) ?: Date()
    }

    fun Date.getFormated(
        output: String = "yyyy-MM-dd'T'HH:mm:ss"
    ): String {
        val utcFormat = SimpleDateFormat(output, Locale.getDefault())
        // utcFormat.timeZone = timeZone
        return utcFormat.format(this)
    }
}





