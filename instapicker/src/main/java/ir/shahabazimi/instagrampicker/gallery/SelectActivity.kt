package ir.shahabazimi.instagrampicker.gallery

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import ir.shahabazimi.instagrampicker.R
import ir.shahabazimi.instagrampicker.classes.BackgroundActivity.Companion.instance
import ir.shahabazimi.instagrampicker.classes.MySharedPreference.Companion.getInstance

class SelectActivity : AppCompatActivity() {
    private val CAMERA_PERMISSION_REQ = 236
    private val STORAGE_PERMISSION_REQ = 326
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select)
        val toolbar = findViewById<Toolbar>(R.id.select_toolbar)
        setSupportActionBar(toolbar)
        instance!!.setActivity(this)
        val bnv = findViewById<BottomNavigationView>(R.id.select_bnv)
        bnv.setOnNavigationItemSelectedListener { mi: MenuItem ->
            val itemId = mi.itemId
            if (itemId == R.id.bnv_gallery) {
                requestStorage()
            } else if (itemId == R.id.bnv_camera) {
                requestCamera()
            }
            true
        }
        bnv.setOnNavigationItemReselectedListener { mi: MenuItem? -> }
        requestStorage()
        bnv.setOnNavigationItemReselectedListener { mi: MenuItem -> if (mi.itemId == R.id.bnv_camera && checkPermission() != PackageManager.PERMISSION_GRANTED) requestCamera() else if (mi.itemId == R.id.bnv_gallery && checkStoragePermission() != PackageManager.PERMISSION_GRANTED) requestStorage() }
    }

    private fun openCamera() {
        supportFragmentManager.beginTransaction().replace(R.id.select_container, CameraFragment())
            .commit()
    }

    private fun openGallery() {
        supportFragmentManager.beginTransaction().replace(R.id.select_container, GalleryFragment())
            .commit()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun checkPermission(): Int {
        return ActivityCompat.checkSelfPermission(this@SelectActivity, Manifest.permission.CAMERA)
    }

    private fun checkStoragePermission(): Int {
        return ActivityCompat.checkSelfPermission(
            this@SelectActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@SelectActivity, arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQ
        )
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this@SelectActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_REQ
        )
    }

    private fun showExplanation() {
        val builder: AlertDialog.Builder
        val alertDialog: AlertDialog
        builder = AlertDialog.Builder(this@SelectActivity)
        builder.setTitle(getString(R.string.camera_permission_title))
        builder.setMessage(getString(R.string.camera_permission_message))
        builder.setPositiveButton(
            getString(R.string.camera_permission_positive)
        ) { dialog: DialogInterface?, which: Int -> requestPermission() }
        builder.setNegativeButton(
            getString(R.string.camera_permission_negative)
        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun showStorageExplanation() {
        val builder: AlertDialog.Builder
        val alertDialog: AlertDialog
        builder = AlertDialog.Builder(this@SelectActivity)
        builder.setTitle(getString(R.string.storage_permission_title))
        builder.setMessage(getString(R.string.storage_permission_message))
        builder.setPositiveButton(
            getString(R.string.storage_permission_positive)
        ) { dialog: DialogInterface?, which: Int -> requestStoragePermission() }
        builder.setNegativeButton(
            getString(R.string.storage_permission_negative)
        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun requestCamera() {
        if (checkPermission() != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@SelectActivity,
                    Manifest.permission.CAMERA
                )
            ) {
                showExplanation()
            } else if (!getInstance(this@SelectActivity)!!.cameraPermission) {
                requestPermission()
                getInstance(this@SelectActivity)!!.setCameraPermission()
            } else {
                showToast(getString(R.string.camera_permission_deny))
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        } else {
            openCamera()
        }
    }

    private fun requestStorage() {
        if (checkStoragePermission() != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@SelectActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                showStorageExplanation()
            } else if (!getInstance(this@SelectActivity)!!.storagePermission) {
                requestStoragePermission()
                getInstance(this@SelectActivity)!!.setStoragePermission()
            } else {
                showToast(getString(R.string.storage_permission_deny))
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        } else {
            openGallery()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQ) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        } else if (requestCode == STORAGE_PERMISSION_REQ) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                onBackPressed()
            }
        }
    }
}