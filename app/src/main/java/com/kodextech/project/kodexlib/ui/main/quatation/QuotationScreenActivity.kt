package com.kodextech.project.kodexlib.ui.main.quatation

import android.content.Intent
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityQuotationScreenBinding
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.customer.CustomerListing
import com.kodextech.project.kodexlib.utils.generateList
import org.json.JSONArray

class QuotationScreenActivity : BaseActivity() {

    private var binding: ActivityQuotationScreenBinding? = null
    var name:String=""
    var hourlyRate:String=""
    var email:String=""
    var menCount:String=""
    var minimumHours:String=""

    override fun onSetupViewGroup() {
        mViewGroup = binding?.qutationCustomer

    }

    override fun setupContentViewWithBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quotation_screen)

        binding?.tvCustomerName?.setOnClickListener {
            val intent = Intent(this, SearchCustomerActivity::class.java)
            startActivity(intent)


        }
        binding?.btnSentQuotation?.setOnClickListener {
                validateFields()
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


    private  fun validateFields(){

        name=binding?.tvCustomerName?.text.toString()
        hourlyRate=binding?.etPrice?.text.toString()
        minimumHours=binding?.minimumBookingTv?.text.toString()
        menCount=binding?.etMenCount?.text.toString()

        if (name?.isNullOrEmpty() == true) {
            showBarToast("Please Select Customer Name")
        } else if (hourlyRate?.isNullOrEmpty() == true) {
            binding?.etPrice?.error = "Required"
        }
        else if (minimumHours?.isNullOrEmpty() == true) {
            binding?.minimumBookingTv?.error = "Required"
        }
        else if (menCount?.isNullOrEmpty() == true) {
            binding?.etMenCount?.error = "Required"
        }
        else {


           sendQuotation(name,hourlyRate,minimumHours,email,menCount)


        }
    }



    private fun sendQuotation(name:String,hourlyRate:String,minimumHours:String,email:String,menCount:String) {


        showLoading()
        NetworkClass.callApi(URLApi.sendQuotation(name = name, hourly_rate = hourlyRate, minimum_hours = minimumHours,
            email = email, men_count = menCount), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {

                hideLoading()
                  Toast.makeText(binding?.root?.context, "response--- "+response, Toast.LENGTH_SHORT).show()

           }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                Toast.makeText(binding?.root?.context, "Error--- "+response, Toast.LENGTH_SHORT).show()
                Log.i("Search", "onErrorResponse: $error")

            }
        })
    }

}