package com.kodextech.project.kodexlib.ui.main.jobs

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityJobsListingBinding
import com.kodextech.project.kodexlib.dialog.AppAlertOption
import com.kodextech.project.kodexlib.dialog.LogoutDialog
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.network.LocalPreference
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.auth.LoginActivity
import com.kodextech.project.kodexlib.ui.main.dashboard.Dashboard
import com.kodextech.project.kodexlib.ui.main.jobs.adapter.JobListingAdapter
import com.kodextech.project.kodexlib.ui.main.jobs.adapter.JobListingAdapter.Companion.mDataSelected
import com.kodextech.project.kodexlib.utils.generateList
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.text
import com.kodextech.project.kodexlib.utils.visible
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class JobsListing : BaseActivity() {

    private var binding: ActivityJobsListingBinding? = null

    private var mData = ArrayList<JobModel>()
    private var mAdapter: JobListingAdapter? = null
    private var viewStates: String? = "today"
    private var from: String? = null
    private var listingFor: String? = null
    private var mDataDate = ArrayList<String>()
    private var searchText: String? = ""

    private var screenSate: JOBSSTATE? = JOBSSTATE.INPROGRESS
    private var isFor: String? = ""
    override fun onSetupViewGroup() {
        mViewGroup = binding?.contentJobs
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_jobs_listing)

        initTopBar()
        setJobData()

        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
                filter(s.toString())

            }

        })


        binding?.btnDelete?.setOnClickListener {
            deleteBookings()
        }

        binding?.topBar?.ivBack?.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }

        binding?.topBar?.ivLogout?.setOnClickListener {
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

        binding?.topBar?.ivSearch?.setOnClickListener {
            if (binding?.etSearch?.visibility == View.VISIBLE) {
                binding?.etSearch?.visibility = View.GONE
                searchText = ""
                viewStates = "today"
                getJobListCall()
                changeViewColor()
            } else {
                binding?.etSearch?.visibility = View.VISIBLE
            }
        }
        binding?.btnNotAssigned?.setOnClickListener {
            binding?.viewNotAssigned?.visibility = View.VISIBLE
            binding?.viewCompleted?.visibility = View.INVISIBLE
            binding?.viewInprogress?.visibility = View.INVISIBLE
            binding?.etSearch?.text("")

            listingFor = "notAssigned"
            checkAndSetData()
        }

        binding?.btnJobsInprogress?.setOnClickListener {
            binding?.viewInprogress?.visibility = View.VISIBLE
            binding?.viewCompleted?.visibility = View.INVISIBLE
            binding?.viewNotAssigned?.visibility = View.INVISIBLE
            binding?.etSearch?.text("")
            listingFor = "inprogress"
            checkAndSetData()
        }

        binding?.btnJobsCompleted?.setOnClickListener {
            binding?.viewInprogress?.visibility = View.INVISIBLE
            binding?.viewCompleted?.visibility = View.VISIBLE
            binding?.viewNotAssigned?.visibility = View.INVISIBLE
            listingFor = "completed"
            binding?.etSearch?.text("")

            checkAndSetData()

        }


        binding?.svJobs?.setOnRefreshListener {
            if (LocalPreference.shared.user?.user?.profile_type == "worker") {
                getWorkerJob(LocalPreference.shared.user?.user?.profile?.uuid)
            } else {
                getJobListCall()
            }

        }

        binding?.tvDay?.setOnClickListener {
            viewStates = "today"
            getJobListCall()
            changeViewColor()

        }
        binding?.tvMonth?.setOnClickListener {
            viewStates = "current-month"
            changeViewColor()
            getJobListCall()

        }
        binding?.tvYear?.setOnClickListener {
            viewStates = "current-year"
            changeViewColor()
            getJobListCall()

        }
        binding?.tvWeekly?.setOnClickListener {
            viewStates = "current-week"
            changeViewColor()
            getJobListCall()

        }


    }

    private fun deleteBookings() {
        val jobIds = mDataSelected.map { it.uuid }.joinToString(",")
        val newIds = mDataSelected
        showLoading()
        NetworkClass.callApi(URLApi.deleteBooking(job_uuid = jobIds), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                showToast("Your Mark Booking Delete Successfully")
                binding?.rlButton?.gone()
                mDataSelected.clear()
                checkAndSetData()
                hideLoading()
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showToast(error ?: "")
            }

        })
    }

    private fun getJobListCall() {
//        if (binding?.svJobs?.isRefreshing == false) {
//            binding?.svJobs?.isRefreshing = true
//        }
        showLoading()
        NetworkClass.callApi(
            URLApi.getJobLoistByTimeNature(
                worker_uuid = null,
                time_nature = viewStates
            ), object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    mData.clear()
                    val json = JSONObject(response ?: "")
                    var model = json.optJSONArray("models")
                    val data = generateList(model.toString() ?: "", Array<JobModel>::class.java)

                    when (listingFor) {
                        "notAssigned" -> {
                            val filterData = data.filter {
                                it.job_status?.lowercase() == "pending".lowercase()
                            }
                            mData.addAll(filterData)
                        }
                        "inprogress" -> {
                            val filterData = data.filter {
                                it.job_status?.lowercase() == "assigned".lowercase()
                                        || it.job_status?.lowercase() == "active".lowercase()
                            }
                            mData.addAll(filterData)
                        }
                        else -> {
                            val filterData = data.filter {
                                it.job_status?.lowercase() == "completed".lowercase()
                                        || it.job_status?.lowercase() == "cancelled".lowercase()
                            }
                            mData.addAll(filterData)
                        }
                    }
                    setJobData()
                  //  binding?.svJobs?.isRefreshing = false
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                  //  binding?.svJobs?.isRefreshing = false
                }

            })
    }

    private fun filter(text: String) {
        var filterlist: ArrayList<JobModel> = ArrayList()
        var fullName: String? = ""
        for (item in mData) {
            fullName = item.first_name.toString() + " " + item.last_name.toString()
            if (item.first_name?.lowercase()?.contains(text.lowercase()) == true
                || item.last_name?.lowercase()?.contains(text.lowercase()) == true
                || try {

                    item.id == text.toInt()
                } catch (e: Exception) {
                    false
                }
                || fullName?.equals(text) == true
            ) {
                filterlist.add(item)
            }
        }
        mAdapter?.filterJobList(filterlist)
    }

    private fun setJobData() {
        checkingData()
        mAdapter = JobListingAdapter(
            this,
            mData,
        ) { item, position, invoiceGenerated, isFor ->
            if (isFor == "1") {
                if (invoiceGenerated) {
                    generateInvoice(item.uuid)
                } else {
                    cancelBooking(item)
                }
            } else if (isFor == "0") {
                binding?.rlButton?.visible()
            } else if (isFor == "2") {
                binding?.rlButton?.gone()
            }
        }
        binding?.rvJobs?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding?.rvJobs?.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
    }

    private fun generateInvoice(uuid: String?) {
        showLoading()
        NetworkClass.callApi(URLApi.getInvoicePDF(job_uuid = uuid), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                showToast(message ?: "")
                getJobListCall()
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showToast(error ?: "")
            }

        })
    }

    private fun cancelBooking(item: JobModel) {
        showLoading()

        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val currentDateandTime = sdf.format(Date())

        NetworkClass.callApi(
            URLApi.updateJobStatus(item.uuid, "cancelled", currentDateandTime),
            object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    getJobListCall()
                    showBarToast("Booking Cancelled")
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                }

            })
    }

    private fun checkingData() {
        if (mData.size == 0) {
            binding?.rlNoData?.visible()
            binding?.rvJobs?.gone()
        } else {
            binding?.rlNoData?.gone()
            binding?.rvJobs?.visible()
        }
    }


    private fun initTopBar() {

        binding?.etSearch?.hint = "Type Here"

        if (LocalPreference.shared.user?.user?.profile?.worker_type?.lowercase() == "driver".lowercase()) {
            binding?.cvMain?.gone()
            binding?.topBar?.ivBack?.gone()
            binding?.topBar?.ivLogout?.visible()
            binding?.topBar?.ivSearch?.visible()
            from = intent.getStringExtra("from")
            listingFor = "notAssigned"
            binding?.btnNotAssigned?.text = "Assigned Job"

        } else {
            binding?.cvMain?.visible()
            binding?.btnNotAssigned?.visible()
            binding?.viewNotAssigned?.visible()
            binding?.topBar?.ivLogout?.gone()
            binding?.topBar?.ivSearch?.visible()
            listingFor = "notAssigned"
        }
        binding?.viewNotAssigned?.visible()
        binding?.btnNotAssigned?.visible()
        binding?.viewInprogress?.gone()
        binding?.viewCompleted?.gone()
        binding?.topBar?.tvText?.text = "Jobs"


    }

    private fun getWorkerJob(uuid: String? = null) {
        showLoading()
        NetworkClass.callApi(URLApi.getJobList(worker_uuid = uuid), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {

                hideLoading()
                mData.clear()
                val json = JSONObject(response ?: "")
                var model = json.optJSONArray("models")
                val data = generateList(model.toString() ?: "", Array<JobModel>::class.java)


                when (listingFor) {
                    "notAssigned" -> {
                        val filterData = data.filter { it.job_status?.lowercase() == "assigned" }
                        mData.addAll(filterData)
                    }
                    "inprogress" -> {
                        val filterData = data.filter {
                            it.job_status?.lowercase() == "active".lowercase()
                        }
                        mData.addAll(filterData)
                    }
                    else -> {
                        val filterData = data.filter {
                            it.job_status?.lowercase() == "completed".lowercase()
                                    || it.job_status?.lowercase() == "cancelled".lowercase()
                        }
                        mData.addAll(filterData)
                    }
                }

                setJobData()
                binding?.svJobs?.isRefreshing = false
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBackPressed() {
        if (from == "driver") {
            finish()
        } else {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {
        checkAndSetData()

    }

    private fun checkAndSetData() {
        if (LocalPreference.shared.user?.user?.profile?.worker_type?.lowercase() == "driver".lowercase()) {
            getWorkerJob(LocalPreference.shared.user?.user?.profile?.uuid)

        } else {
            if (mDataSelected.count() == 0) {
                binding?.rlButton?.gone()
            } else {
                binding?.rlButton?.visible()
            }
            getJobListCall()
        }
    }

    override fun setupLoader() {

    }


    private fun changeViewColor() {
        when (viewStates) {
            "today" -> {
                binding?.tvDay?.setBackgroundColor(getColor(R.color.blue))
                binding?.tvDay?.setTextColor(getColor(R.color.white))
                binding?.tvMonth?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvMonth?.setTextColor(getColor(R.color.cusCol))
                binding?.tvWeekly?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvWeekly?.setTextColor(getColor(R.color.cusCol))
                binding?.tvYear?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvYear?.setTextColor(getColor(R.color.cusCol))
            }
            "current-month" -> {
                binding?.tvMonth?.setBackgroundColor(getColor(R.color.blue))
                binding?.tvMonth?.setTextColor(getColor(R.color.white))
                binding?.tvDay?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvDay?.setTextColor(getColor(R.color.cusCol))
                binding?.tvWeekly?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvWeekly?.setTextColor(getColor(R.color.cusCol))
                binding?.tvYear?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvYear?.setTextColor(getColor(R.color.cusCol))
            }
            "current-year" -> {
                binding?.tvMonth?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvMonth?.setTextColor(getColor(R.color.cusCol))
                binding?.tvDay?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvDay?.setTextColor(getColor(R.color.cusCol))
                binding?.tvWeekly?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvWeekly?.setTextColor(getColor(R.color.cusCol))
                binding?.tvYear?.setBackgroundColor(getColor(R.color.blue))
                binding?.tvYear?.setTextColor(getColor(R.color.white))
            }
            "current-week" -> {
                binding?.tvMonth?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvMonth?.setTextColor(getColor(R.color.cusCol))
                binding?.tvDay?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvDay?.setTextColor(getColor(R.color.cusCol))
                binding?.tvYear?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvYear?.setTextColor(getColor(R.color.cusCol))
                binding?.tvWeekly?.setBackgroundColor(getColor(R.color.blue))
                binding?.tvWeekly?.setTextColor(getColor(R.color.white))
            }
        }


    }

    fun logout() {
        showLoading()
        NetworkClass.callApi(URLApi.signout(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                LocalPreference.shared.removeAll()
                val intent = Intent(this@JobsListing, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                ActivityCompat.finishAffinity(this@JobsListing)
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })


    }

}


enum class JOBSSTATE {
    INPROGRESS, COMPLETED
}