package com.kodextech.project.kodexlib.ui.main.customer

import android.net.Uri
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityCustomerProfileBinding
import com.kodextech.project.kodexlib.model.CustomerModel
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.customer.adapter.CustomerJobHistory
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.Placeholders
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.loadImage
import com.kodextech.project.kodexlib.utils.generateList
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible
import ir.shahabazimi.instagrampicker.InstagramPicker
import ir.shahabazimi.instagrampicker.classes.SingleListener
import org.json.JSONObject
import java.io.File

class CustomerProfile : BaseActivity() {

    private var binding: ActivityCustomerProfileBinding? = null
    private var mData = ArrayList<JobModel>()
    private var mHistoryData = ArrayList<JobModel>()
    private var mAdapter: CustomerJobHistory? = null
    private var customerObj: CustomerModel? = null
    private var customerEmail: String? = null
    var file: File? = null

    override fun onSetupViewGroup() {
        mViewGroup = binding?.content
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_profile)

        initTopBar()
        getCustomerProfile(customerEmail)
        binding?.topBar?.ivBack?.setOnClickListener { onBackPressed() }

        binding?.ivCustomer?.setOnClickListener {
//            launchGallery()
        }

        binding?.rating?.setOnRatingBarChangeListener { simpleRatingBar, rating, fromUser ->
            rateCustomerCall(customerEmail, rating.toString())
        }

    }

    private fun rateCustomerCall(customerEmail: String? = null, rating: String? = null) {
        showLoading()
        NetworkClass.callApi(
            URLApi.rateCustomer(
                customer_email = customerEmail,
                customer_rating = rating
            ), object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    binding?.customerRating?.text = "${rating.toString()}"
                    showBarToast("${customerObj?.first_name} ${customerObj?.last_name} Rated Successfully")
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                }

            })
    }

    private fun getCustomerProfile(customerEmail: String?) {
        showLoading()
        NetworkClass.callApi(
            URLApi.getJobList(email = customerEmail),
            object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    val json = JSONObject(response ?: "")
                    val jobHistory = json.optJSONArray("models")
                    val jobHistoryData =
                        generateList(jobHistory.toString(), Array<JobModel>::class.java)
                    val filterData = jobHistoryData.filter {
                        it.job_status?.lowercase() == "completed".lowercase()
                                || it.job_status?.lowercase() == "cancelled".lowercase()
                    }
                    mHistoryData.addAll(filterData)
                    setJobHistoryData()

                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                }

            })
    }

    private fun setJobHistoryData() {

        if (mHistoryData.isEmpty()) {
            binding?.rlNoData?.visible()
            binding?.rvJobHistory?.gone()

        } else {
            binding?.rlNoData?.gone()
            binding?.rvJobHistory?.visible()
        }

        mAdapter =
            CustomerJobHistory(
                this@CustomerProfile,
                mHistoryData
            ) { item, position ->

            }
        binding?.rvJobHistory?.layoutManager =
            LinearLayoutManager(this@CustomerProfile, RecyclerView.VERTICAL, false)
        binding?.rvJobHistory?.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()


    }


    private fun launchGallery() {
        val `in` = InstagramPicker(this)//(activity)

        `in`.show(0, 0, object : SingleListener {
            override fun selectedPic(address: String?) {
                val filePath = Uri.parse(address)//File(address)
                file = File(filePath.path ?: "")//filePath.toFile()

                binding?.ivCustomer?.loadImage(filePath, Placeholders.USER, true)
            }

        })

    }


    private fun initTopBar() {
        binding?.topBar?.ivLogout?.gone()
        binding?.topBar?.tvText?.text = "Customer"
        customerObj = intent.getSerializableExtra("customerObj") as? CustomerModel?
        customerEmail = customerObj?.email
        setProfileData(customerObj)
    }

    private fun setProfileData(customerObj: CustomerModel?) {
        binding?.tvCustomerName?.text = "${customerObj?.first_name} ${customerObj?.last_name}"
        binding?.tvAddress?.text = customerObj?.pickup_address?.address1
        binding?.tvContact?.text = "${customerObj?.phone_code} ${customerObj?.phone_numb}"
        binding?.tvEmail?.text = customerEmail
        binding?.rating?.rating = customerObj?.customer_rating?.toFloat()!!
        binding?.customerRating?.text = "${customerObj?.customer_rating}.0"
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