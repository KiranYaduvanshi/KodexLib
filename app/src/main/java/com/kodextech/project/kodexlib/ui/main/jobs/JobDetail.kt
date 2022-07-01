package com.kodextech.project.kodexlib.ui.main.jobs

import android.content.Intent
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityJobDetailBinding
import com.kodextech.project.kodexlib.dialog.ConfirmationDialog
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.model.User
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.booking.AddBooking
import com.kodextech.project.kodexlib.ui.main.dashboard.Dashboard
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.zDateConvertor
import org.json.JSONObject

class JobDetail : BaseActivity() {

    private var binding: ActivityJobDetailBinding? = null
    private var driverInfo: User? = null
    private var jobId: String? = null
    private var driverId: String? = null
    var isfor: String? = ""
    override fun onSetupViewGroup() {
        mViewGroup = binding?.contentDetail
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_detail)

        initTopBar()
        setData()

        binding?.detailTopBar?.ivBack?.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
           // finish()
        }

        binding?.detailTopBar?.ivLogout?.setOnClickListener {
            val intent = Intent(this@JobDetail, AddBooking::class.java)
            intent.putExtra("jobID", jobId)
            intent.putExtra("isF", "edit")
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
        finish()
    }

    private fun setData() {

    }

    private fun initTopBar() {

        binding?.detailTopBar?.ivLogout?.gone()
        isfor = intent.getStringExtra("for")

        if (isfor == "finishJob") {
            binding?.btnAssignJob?.text = "Finish Job"
        } else {
            driverInfo = intent.getSerializableExtra("driverInfo") as? User?
        }
        jobId = intent.getStringExtra("id")
        getJob(jobId)


        binding?.btnAssignJob?.setOnClickListener {
            when (isfor) {
                "finishJob" -> {
                    val dialog = ConfirmationDialog.newInstance(jobId) {
                        //Worker Add in AddWorkerDialog
                    }

                    dialog.show(supportFragmentManager, "")
                }
                else -> {

                    driverId = driverInfo?.profile?.uuid
                    if (driverId.isNullOrEmpty() || jobId.isNullOrEmpty()) {
                        showBarToast("Some of Detail Null")
                    } else {
                        if (driverInfo == null) {
                        } else {
                            assignJobCall(driverId, jobId)
                        }

                    }
                }
            }
        }

        binding?.detailTopBar?.tvText?.text = "Detail Page"
    }

    private fun assignJobCall(workerId: String? = null, jobId: String? = null) {
        showLoading()
        NetworkClass.callApi(
            URLApi.assignJob(job_uuid = jobId, worker_uuid = workerId),
            object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    finish()
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                }

            })
    }

    private fun getJob(jobId: String? = null) {
        showLoading()
        NetworkClass.callApi(URLApi.getSpecificJob(job_uuid = jobId.toString()), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                val json = JSONObject(response ?: "")
                val obj = Gson().fromJson(json.toString(), JobModel::class.java)
                setJobDetail(obj)
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }

    private fun setJobDetail(obj: JobModel?) {

        if (obj?.job_status == "pending" || obj?.job_status == "assigned") {
            binding?.detailTopBar?.ivLogout?.setImageResource(R.drawable.ic_edit)
        } else {
            binding?.detailTopBar?.ivLogout?.gone()
        }

        binding?.tvPhone?.text = obj?.phone_code + " " + obj?.phone_numb
        binding?.tvLocation?.text = obj?.pickup_address?.address1
        binding?.tvCustomerName?.text = obj?.first_name + " " + obj?.last_name
        binding?.tvEmail?.text = obj?.email
        binding?.tvType?.text = obj?.service

        val number = obj?.price_amount?.toDoubleOrNull() ?: 0.0
        val rounded = String.format("%.2f", number)
        binding?.tvPrice?.text = "£ $rounded"

        val advance_amount = obj?.advance_amount?.toDoubleOrNull() ?: 0.0
        val advance_amount_rounded = String.format("%.2f", advance_amount)
        binding?.tvAdvancePayment?.text = "£ $advance_amount_rounded"

        binding?.tvDate?.text = obj?.start_time?.zDateConvertor(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "dd MMM yyyy",
            obj?.start_time
        )

        binding?.tvJobStart?.text = obj?.job_start_time
        binding?.tvJobEnd?.text = obj?.job_end_time


        when (obj?.price_nature) {
            "fixed-price" -> {
                binding?.tvPriceType?.text = "Fixed"
                binding?.llHourly?.visibility = View.GONE
            }
            "hourly-price" -> {
                binding?.tvPriceType?.text = "Hourly"

                binding?.llHourly?.visibility = View.VISIBLE

                binding?.tvHours?.text = obj?.actual_hours
            }
        }

        when (obj?.priority) {
            "high" -> {
                binding?.tvStatus?.text = "Important"

            }
            "low" -> {
                binding?.tvStatus?.text = "Not Important"
            }
            "normal" -> {
                binding?.tvStatus?.text = "Normal"
            }
        }

        if (driverInfo == null) {
            binding?.tvDriverName?.text = obj?.worker?.full_name
        } else {
            binding?.tvDriverName?.text = driverInfo?.profile?.full_name
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