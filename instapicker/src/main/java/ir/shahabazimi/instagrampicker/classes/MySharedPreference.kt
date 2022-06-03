package ir.shahabazimi.instagrampicker.classes

import android.content.Context
import android.content.SharedPreferences

class MySharedPreference private constructor(context: Context) {
    private val sp: SharedPreferences
    val cameraPermission: Boolean
        get() = sp.getBoolean("camera", false)

    fun setCameraPermission() {
        val editor = sp.edit()
        editor.putBoolean("camera", true)
        editor.apply()
    }

    val storagePermission: Boolean
        get() = sp.getBoolean("external", false)

    fun setStoragePermission() {
        val editor = sp.edit()
        editor.putBoolean("external", true)
        editor.apply()
    }

    companion object {
        private var instance: MySharedPreference? = null
        fun getInstance(context: Context): MySharedPreference? {
            if (instance == null) instance = MySharedPreference(context)
            return instance
        }
    }

    init {
        sp = context.getSharedPreferences("InstagramPickerSharedPreference", 0)
    }
}