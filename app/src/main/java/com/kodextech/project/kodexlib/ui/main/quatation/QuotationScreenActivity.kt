package com.kodextech.project.kodexlib.ui.main.quatation

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityQuotationScreenBinding
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi

class QuotationScreenActivity : BaseActivity() {

    private var binding: ActivityQuotationScreenBinding? = null
    var name: String = ""
    var hourlyRate: String = ""
    var email: String = ""
    var menCount: String = ""
    var minimumHours: String = ""
    private val MY_REQUEST_CODE = 1


    override fun onSetupViewGroup() {
        mViewGroup = binding?.qutationCustomer

    }

    override fun setupContentViewWithBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quotation_screen)

        binding?.tvCustomerName?.setOnClickListener {
            val intent = Intent(this, SearchCustomerActivity::class.java)
            startActivityForResult(intent, MY_REQUEST_CODE)

        }
        binding?.btnSentQuotation?.setOnClickListener {
            validateFields()
        }

        binding?.ivBack?.setOnClickListener {
            onBackPressed()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MY_REQUEST_CODE) {
                if (data != null) {
                    name = data.getStringExtra("name").toString()
                    email = data.getStringExtra("email").toString()
                    binding?.tvCustomerName?.text = name
                    // Toast.makeText(binding?.root?.context, "name --- "+name+"-- email --"+email, Toast.LENGTH_SHORT).show()

                }
                // textView.setText(data.getStringExtra("value"));
            }
        }

    }

    private fun validateFields() {

        hourlyRate = binding?.etPrice?.text.toString()
        minimumHours = binding?.minimumBookingTv?.text.toString()
        menCount = binding?.etMenCount?.text.toString()

        if (name?.isNullOrEmpty() == true) {
            showBarToast("Please Select Customer Name")
        } else if (hourlyRate?.isNullOrEmpty() == true) {
            binding?.etPrice?.error = "Required"
        } else if (minimumHours?.isNullOrEmpty() == true) {
            binding?.minimumBookingTv?.error = "Required"
        } else if (menCount?.isNullOrEmpty() == true) {
            binding?.etMenCount?.error = "Required"
        } else {


            sendQuotation(name, hourlyRate, minimumHours, email, menCount)


        }
    }


    private fun sendQuotation(
        name: String,
        hourlyRate: String,
        minimumHours: String,
        email: String,
        menCount: String
    ) {

        Toast.makeText(
            binding?.root?.context,
            "--name--" + name + "--hour--" + minimumHours + "--rate--" + hourlyRate + "--email--" + email + "--men count--" + menCount,
            Toast.LENGTH_SHORT
        ).show()


        showLoading()
        NetworkClass.callApi(URLApi.sendQuotation(
            name = name, hourly_rate = hourlyRate, minimum_hours = minimumHours,
            email = email, men_count = menCount
        ), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {

                hideLoading()
                showBarToast("Email sent Successfully")
                finish()


            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                Toast.makeText(binding?.root?.context, "Error--- " + error, Toast.LENGTH_SHORT)
                    .show()
                Log.i("Search", "onErrorResponse: $error")

            }
        })
    }

}