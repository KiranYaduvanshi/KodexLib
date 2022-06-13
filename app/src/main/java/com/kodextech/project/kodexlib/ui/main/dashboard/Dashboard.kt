package com.kodextech.project.kodexlib.ui.main.dashboard

import android.content.Intent
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.communication.CommunicationActivity
import com.kodextech.project.kodexlib.databinding.ActivityDashboardBinding
import com.kodextech.project.kodexlib.dialog.AddWorkerDialog
import com.kodextech.project.kodexlib.dialog.AppAlertOption
import com.kodextech.project.kodexlib.dialog.LogoutDialog
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.model.User
import com.kodextech.project.kodexlib.network.LocalPreference
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.auth.LoginActivity
import com.kodextech.project.kodexlib.ui.main.booking.AddBooking
import com.kodextech.project.kodexlib.ui.main.booking.adapter.DashboardBookingAdapter
import com.kodextech.project.kodexlib.ui.main.calendar.Calendar
import com.kodextech.project.kodexlib.ui.main.customer.CustomerListing
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.*
import com.kodextech.project.kodexlib.ui.main.expenses.ExpensesActivity
import com.kodextech.project.kodexlib.ui.main.invoice.InvoiceListing
import com.kodextech.project.kodexlib.ui.main.jobs.JobsListing
import com.kodextech.project.kodexlib.ui.main.worker.WorkerListing
import com.kodextech.project.kodexlib.ui.main.worker.adapter.WorkerListingAdapter
import com.kodextech.project.kodexlib.utils.generateList
import com.kodextech.project.kodexlib.utils.gone
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class Dashboard : BaseActivity() {

    private var binding: ActivityDashboardBinding? = null

    private var mBookingData = ArrayList<JobModel>()
    private var mABookingAdapter: DashboardBookingAdapter? = null


    private var mWorkerData = ArrayList<User>()
    private var mWorkerAdapter: WorkerListingAdapter? = null
    private var mCardData = ArrayList<DashboardItemModel>()

    override fun onBackPressed() {
        finish()
    }


    override fun onSetupViewGroup() {
        mViewGroup = findViewById(R.id.contentDashboard)
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(this.getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)

        initTopBar()
        setUpDashboardRecyler()
        setBookingRecycler()
        setWorkerRecycler()
        getWorker()
        getJobListCall()


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
            val intent = Intent(this@Dashboard, Calendar::class.java)
            startActivity(intent)
        }

        binding?.tvSeeAll?.setOnClickListener {
//            val intent = Intent(this, BookingListing::class.java)
//            startActivity(intent)
        }
    }

    fun getWorker() {
        showLoading()
        NetworkClass.callApi(URLApi.getUser(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                mWorkerData.clear()
                val json = JSONObject(response ?: "")
                val json2 = json.optJSONArray("user")
                var data = generateList(json2.toString(), Array<User>::class.java)
                val worker = data.filter { it.profile_type?.lowercase() == "worker".lowercase() }
                val limited = worker.take(2)
                mWorkerData.addAll(limited)
                mWorkerAdapter?.notifyDataSetChanged()

            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }

    private fun setWorkerRecycler() {

        if (mWorkerData.size == 0) {
        }

        mWorkerAdapter = WorkerListingAdapter(this, mWorkerData, "1") { user, positionL, isFor ->
            if (isFor == "updated") {
                getWorker()
            }
        }
        binding?.rvWorker?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding?.rvWorker?.adapter = mWorkerAdapter

    }

    private fun setBookingRecycler() {

        mABookingAdapter = DashboardBookingAdapter(this, mBookingData) { positionL -> }
        binding?.rvBooking?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding?.rvBooking?.adapter = mABookingAdapter
        mABookingAdapter?.notifyDataSetChanged()
    }


    fun logout() {
        showLoading()
        NetworkClass.callApi(URLApi.signout(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                LocalPreference.shared.removeAll()
                val intent = Intent(this@Dashboard, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                ActivityCompat.finishAffinity(this@Dashboard)
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

        mCardData.add(DashboardItemModel("Create Booking", R.drawable.ic_booking))
        mCardData.add(DashboardItemModel("View Jobs", R.drawable.ic_job_inprogress))
        mCardData.add(DashboardItemModel("Customers", R.drawable.ic_customers))
        mCardData.add(DashboardItemModel("Invoices", R.drawable.ic_invoice))
        mCardData.add(DashboardItemModel("Create Worker", R.drawable.ic_add_workers))
        mCardData.add(DashboardItemModel("List of All Workers", R.drawable.ic_job_completed))
        mCardData.add(DashboardItemModel("Expenses", R.drawable.ic_expenses))
        mCardData.add(DashboardItemModel("Communication", R.drawable.ic_mails))
        mCardData.add(DashboardItemModel("Statics", R.drawable.ic_statics))



        binding?.rvDashboard?.layoutManager = GridLayoutManager(this, 2)
        binding?.rvDashboard?.adapter = DashboardItemAdapter(this, mCardData) { position: Int ->
            when (position) {
                0 -> {
                    val intent = Intent(this, AddBooking::class.java)
                    startActivity(intent)
                }

                1 -> {
                    val intent = Intent(this, JobsListing::class.java)
                    startActivity(intent)
                }
                2 -> {
                    val intent = Intent(this, CustomerListing::class.java)
                    startActivity(intent)
                }
                3 -> {
                    val intent = Intent(this, InvoiceListing::class.java)
                    startActivity(intent)
                }
                4 -> {
                    val dialog = AddWorkerDialog.newInstance("Create Worker") {
                        //Worker Add in AddWorkerDialog
                    }

                    dialog.show(supportFragmentManager, "")
                }
                5 -> {
                    val intent = Intent(this, WorkerListing::class.java)
                    startActivity(intent)
                }
                6 -> {
                    val intent = Intent(this, ExpensesActivity::class.java)
                    startActivity(intent)
                }
                7 -> {
                    val intent = Intent(this, CommunicationActivity::class.java)
                    startActivity(intent)
                }   8 -> {
                    val intent = Intent(this, CommunicationActivity::class.java)
                    startActivity(intent)
                }

            }
        }

    }

    private fun getJobListCall() {


        NetworkClass.callApi(
            URLApi.getJobList(
            ), object : Response {
                override fun onSuccessResponse(response: String?, message: String) {

                    mBookingData.clear()
                    val json = JSONObject(response ?: "")
                    var model = json.optJSONArray("models")
                    val data = generateList(model.toString() ?: "", Array<JobModel>::class.java)
                    val limited = data.take(3)
                    mBookingData.addAll(limited)
                    setBookingRecycler()
                }

                override fun onErrorResponse(error: String?, response: String?) {

                    showBarToast(error ?: "")
                }

            })
    }


}

