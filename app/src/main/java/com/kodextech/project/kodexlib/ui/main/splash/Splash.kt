package com.kodextech.project.kodexlib.ui.main.splash

import android.Manifest
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.network.LocalPreference
import com.kodextech.project.kodexlib.ui.main.auth.LoginActivity
import com.kodextech.project.kodexlib.ui.main.dashboard.Dashboard
import com.kodextech.project.kodexlib.ui.main.jobs.JobsListing


class Splash : BaseActivity() {


    override fun onSetupViewGroup() {
        mViewGroup = findViewById(R.id.content)


    }


    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.white))
        setContentView(R.layout.activity_splash)

        makeTopBottomTransparent()
        makeLightContentStatusBar()

        checkPermissionCall()


    }

    private fun checkPermissionCall() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { /* ... */
                    Handler(Looper.getMainLooper()).postDelayed({
                        checkUser()
                    }, 2000L)


                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken?
                ) { /* ... */
                }
            }).check()
    }

    private fun checkUser() {
        if (LocalPreference.shared.user != null) {
            if (LocalPreference.shared.user?.user?.profile_type?.lowercase() == "admin".lowercase()
                || LocalPreference.shared.user?.user?.profile?.worker_type?.lowercase() == "office".lowercase()
                || LocalPreference.shared.user?.user?.profile?.worker_type?.lowercase() == "office-worker".lowercase()
            ) {
                val intent = Intent(this@Splash, Dashboard::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@Splash, JobsListing::class.java)
                intent.putExtra("from", "driver")
                startActivity(intent)
            }
            finish()
        } else {
            startActivity(Intent(this@Splash, LoginActivity::class.java))
            finish()
        }
    }


    override fun onRecycleBeforeDestroy() {
    }

    override fun onBecameInvisibleToUser() {
    }

    override fun onBecameVisibleToUser() {
    }

    override fun setupLoader() {
    }


}