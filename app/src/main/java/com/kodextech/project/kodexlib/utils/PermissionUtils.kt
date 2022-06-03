package com.kodextech.project.kodexlib.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * @author Atif Arif
 */
object PermissionUtils {

    const val PERMISSIONS_REQUEST_READ_CONTACTS = 1
    const val PERMISSIONS_WRITE_EXTERNAL_STORAGE = 2

    /** camera permissions */
    val PERMISSIONS_CAMERA by lazy {
        arrayOf(Manifest.permission.CAMERA)
    }
    val PERMISSIONS_READ_CONTACTS by lazy {
        arrayOf(Manifest.permission.READ_CONTACTS)
    }

    val PERMISSIONS_EXTERNAL_STORAGE by lazy {
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    val KEYBOARD_API_PERMISSION by lazy {
        arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }


    fun checkIfPermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            )
                return false
        }
        return true
    }
}
