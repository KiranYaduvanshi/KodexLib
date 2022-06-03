package com.kodextech.project.kodexlib.network

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.google.gson.Gson
import com.kodextech.project.kodexlib.base.BaseApplication
import com.kodextech.project.kodexlib.model.UserModel


class LocalPreference(private val mContext: Context?) {
    fun removeAll() {
        editor?.apply {
            clear()
            apply()
        }

    }


    var user: UserModel?
        get() {
            val stUser = preferences?.getString("UserObje", "") ?: ""
            if (stUser.isEmpty()) {
                return null
            }
            return Gson().fromJson(stUser, UserModel::class.java)
        }
        set(newValue) {
            val userString = Gson().toJson(newValue)
            editor?.apply {
                putString("UserObje", userString)
                apply()
            }
        }


//    var product: ArrayList<Product>?
//        get() {
//            val stUser = preferences?.getString("productOBJ", "") ?: ""
//            if (stUser.isEmpty()) {
//                return null
//            }
//            return generateList(stUser.toString(), Array<Product>::class.java)
//        }
//        set(newValue) {
//            val userString = Gson().toJson(newValue)
//            editor?.apply {
//                putString("productOBJ", userString)
//                apply()
//            }
//        }


    var token: String?
        get() = preferences?.getString("AuthToken", "") ?: ""
        set(token) {
            editor?.apply {
                putString("AuthToken", token)
                apply()
            }
        }
    var currency: String?
        get() = preferences?.getString("currency", "") ?: ""
        set(token) {
            editor?.apply {
                putString("currency", token)
                apply()
            }
        }
    var subs: String?
        get() = preferences?.getString("subs", "") ?: ""
        set(subs) {
            editor?.apply {
                putString("subs", subs)
                apply()
            }
        }

    var emergencyNumber: String?
        get() = preferences?.getString("emergencyNumber", "") ?: ""
        set(token) {
            editor?.apply {
                putString("emergencyNumber", token)
                apply()
            }
        }
    var name: String?
        get() = preferences?.getString("name", "") ?: ""
        set(token) {
            editor?.apply {
                putString("name", token)
                apply()
            }
        }
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    var portNumber: Int?
        get() = preferences?.getInt("PORT_NUMBER", 0)
        set(number) {
            editor?.putInt("PORT_NUMBER", number ?: 0)
            editor?.apply()
        }

    companion object {
        @JvmField
        var shared = LocalPreference(BaseApplication.instance)
    }

    init {
        preferences = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mContext?.getSharedPreferences(
                BaseApplication.instance?.packageName, Context.MODE_PRIVATE
            )
        } else {
            mContext?.getSharedPreferences(
                BaseApplication.instance?.packageName, Context.MODE_PRIVATE
            )
        }
        editor = preferences?.edit()
        editor?.apply()
    }
}