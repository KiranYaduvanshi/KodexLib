package com.kodextech.project.kodexlib.ui.main.jobs

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.kodextech.project.kodexlib.ui.main.dashboard.DriverDashboardActivity
import com.kodextech.project.kodexlib.ui.main.jobs.adapter.PorterDetailAdapter
import com.kodextech.project.kodexlib.ui.main.worker.adapter.PortingListingAdapter
import com.kodextech.project.kodexlib.ui.main.worker.adapter.WorkerListingAdapter
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.zDateConvertor
import org.json.JSONObject

class JobDetail : BaseActivity() {

    private var binding: ActivityJobDetailBinding? = null
    private var driverInfo: User? = null
    private var jobId: String? = null
    private var driverId: String? = null
    var isfor: String? = ""
    var list: ArrayList<User> = ArrayList()
    private var mAdapter: PorterDetailAdapter? = null



    override fun onSetupViewGroup() {
        mViewGroup = binding?.contentDetail
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_detail)

        initTopBar()
        setData()

        binding?.detailTopBar?.ivBack?.setOnClickListener {
            if (intent.getStringExtra("check") == "0"){
                val intent = Intent(this, Dashboard::class.java)
                startActivity(intent)
                finish()


            }
            else{
                val intent = Intent(this, DriverDashboardActivity::class.java)
                startActivity(intent)
                finish()

            }
        }

        binding?.detailTopBar?.ivLogout?.setOnClickListener {
            val intent = Intent(this@JobDetail, AddBooking::class.java)
            intent.putExtra("jobID", jobId)
            intent.putExtra("isF", "edit")
            startActivity(intent)
        }

    }

    override fun onBackPressed() {
        if (intent.getStringExtra("check") == "0"){
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()


        }
        else{
            val intent = Intent(this, DriverDashboardActivity::class.java)
            startActivity(intent)
            finish()

        }

    }

    private fun setData() {

    }
    private fun  setPorterAdapter(){

        mAdapter = PorterDetailAdapter(this,list)

        binding?.porterListRv?.adapter = mAdapter

        mAdapter?.notifyDataSetChanged()


    }

    private fun initTopBar() {

        binding?.detailTopBar?.ivLogout?.gone()
        isfor = intent.getStringExtra("for")

        if (isfor == "finishJob") {
            binding?.btnAssignJob?.text = "Finish Job"
        } else {
            driverInfo = intent.getSerializableExtra("driverInfo") as? User?
            list = intent.getSerializableExtra("list") as ArrayList<User>
            if (list !=null){
                binding?.porterListRv?.visibility=View.VISIBLE
                setPorterAdapter()

            }
            else{
                binding?.porterListRv?.visibility=View.GONE
            }
        }
        jobId = intent.getStringExtra("id")
     //   Toast.makeText(this@JobDetail, "job idd --- "+jobId, Toast.LENGTH_SHORT).show()
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
                            var porterList = ArrayList<String>();
                            for( id  in list){
                                Log.i("id","iddddddd1 --"+id.profile?.uuid)
                                Log.i("id","iddddddd2 --"+driverId)
                                Log.i("id","iddddddd3 --"+jobId)
                              //  Toast.makeText(this@JobDetail, "iddd --- "+ id.profile?.uuid, Toast.LENGTH_SHORT).show()
                                porterList.add(id.profile?.uuid.toString())
                            }
                            Log.i("id","idddddddList ---"+porterList.toString())

                            assignJobCall(driverId, jobId , porterList)
                        }

                    }
                }
            }
        }

        binding?.detailTopBar?.tvText?.text = "Detail Page"
    }

    private fun assignJobCall(workerId: String? = null, jobId: String? = null, porterId: ArrayList<String>) {
        showLoading()
        NetworkClass.callApi(
            URLApi.assignJob(job_uuid = jobId, worker_uuid = workerId,porter_uuid =  porterId),
            object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    Toast.makeText(this@JobDetail
                        , "Job Assigned successfully-- "+response.toString(), Toast.LENGTH_SHORT).show()
                    finish()

                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                    Log.i("id","idddddd  "+error.toString())

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