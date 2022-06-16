package com.kodextech.project.kodexlib.ui.main.dashboard

import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.communication.CommunicationActivity
import com.kodextech.project.kodexlib.databinding.ActivityDriverDashboardBinding
import com.kodextech.project.kodexlib.dialog.AddWorkerDialog
import com.kodextech.project.kodexlib.dialog.AppAlertOption
import com.kodextech.project.kodexlib.dialog.LogoutDialog
import com.kodextech.project.kodexlib.network.LocalPreference
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.auth.LoginActivity
import com.kodextech.project.kodexlib.ui.main.booking.AddBooking
import com.kodextech.project.kodexlib.ui.main.calendar.Calendar
import com.kodextech.project.kodexlib.ui.main.customer.CustomerListing
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.DashboardItemAdapter
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.DashboardItemModel
import com.kodextech.project.kodexlib.ui.main.expenses.ExpensesActivity
import com.kodextech.project.kodexlib.ui.main.invoice.InvoiceListing
import com.kodextech.project.kodexlib.ui.main.jobs.JobsListing
import com.kodextech.project.kodexlib.ui.main.worker.WorkerListing
import com.kodextech.project.kodexlib.utils.gone

class DriverDashboardActivity : BaseActivity() {
    private var binding: ActivityDriverDashboardBinding? = null
    private var mCardData = ArrayList<DashboardItemModel>()

    override fun onBackPressed() {
        finish()
    }


    override fun onSetupViewGroup() {
        mViewGroup = findViewById(R.id.contentDashboard)
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(this.getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_dashboard)

        initTopBar()
        setUpDashboardRecyler()


        binding?.dashboardTopBar?.ivLogout?.setOnClickListener {
            val dl = LogoutDialog.newInstance(
                "LOGOUT",
                "Do you really Want to logout?",
                "Yes",
                "No"
            ) {
                when (it) {
                    AppAlertOption.YES -> {

                        logout()
                    }
                    AppAlertOption.NO -> {
                    }
                }
            }
            dl.show(supportFragmentManager, "")
        }

        binding?.dashboardTopBar?.ivBack?.setOnClickListener {
            val intent = Intent(this, Calendar::class.java)
            startActivity(intent)
        }


    }


    fun logout() {
        showLoading()
        NetworkClass.callApi(URLApi.signout(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                LocalPreference.shared.removeAll()
                val intent = Intent(this@DriverDashboardActivity, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                ActivityCompat.finishAffinity(this@DriverDashboardActivity)
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })


    }


    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {
    }

    override fun setupLoader() {

    }

    private fun initTopBar() {
        binding?.dashboardTopBar?.ivBack?.gone()
        binding?.dashboardTopBar?.tvText?.text = "Dashboard"


    }

    private fun setUpDashboardRecyler() {

        mCardData.add(DashboardItemModel("View Jobs", R.drawable.ic_job_inprogress))
        mCardData.add(DashboardItemModel("Expenses", R.drawable.ic_expenses))
        mCardData.add(DashboardItemModel("Communication", R.drawable.ic_mails))
        mCardData.add(DashboardItemModel("Statics", R.drawable.ic_statics))



        binding?.rvDashboard?.layoutManager = GridLayoutManager(this, 2)
        binding?.rvDashboard?.adapter = DashboardItemAdapter(this, mCardData) { position: Int ->
            when (position) {
                0 -> {
                    val intent = Intent(this, JobsListing::class.java)
                        intent.putExtra("from", "driver")
           startActivity(intent)
                }

                1 -> {

                    val intent = Intent(this, ExpensesActivity::class.java)
                    startActivity(intent)
                }
                2 -> {
                    val intent = Intent(this, CommunicationActivity::class.java)
                    startActivity(intent)
                }
                3 -> {
                    val intent = Intent(this, CommunicationActivity::class.java)
                    startActivity(intent)
                }

            }
        }

    }


}