package com.kodextech.project.kodexlib.ui.main.expenses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityDriverExpenseBinding
import com.kodextech.project.kodexlib.databinding.ActivityStaticBinding
import com.kodextech.project.kodexlib.model.DriverExpenseModel
import com.kodextech.project.kodexlib.model.Expensis
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import org.json.JSONObject

class DriverExpenseActivity : BaseActivity() {


    private var binding: ActivityDriverExpenseBinding? = null
    private var viewStates: String? = "today"


    override fun onBackPressed() {
        finish()
    }
    override fun onSetupViewGroup() {
        mViewGroup = binding?.rlExpenses
    }

    override fun setupContentViewWithBinding() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_expense)

        onclickListeners()
        getExpenses("1")

    }

    private fun onclickListeners() {
        binding?.tvDay?.setOnClickListener {
            viewStates = "today"
            changeViewColor()
            getExpenses("1")

        }
        binding?.tvWeekly?.setOnClickListener {
            viewStates = "current-week"
            changeViewColor()
            getExpenses("2")

        }
        binding?.tvMonth?.setOnClickListener {
            viewStates = "current-month"
            changeViewColor()
            getExpenses("3")

        }
        binding?.tvYear?.setOnClickListener {
            viewStates = "current-year"
            changeViewColor()
            getExpenses("4")


        }

        binding?.icBack?.setOnClickListener {
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

    private fun getExpenses(id : String) {

        showLoading()
        NetworkClass.callApi(URLApi.getDriverExpense(id), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                val json = JSONObject(response ?: "")
                val obj = Gson().fromJson(json.toString(), DriverExpenseModel::class.java)
                setExpensis(obj)
                Log.i("Tester", "${message} RESPONSE")
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                Log.i("Tester", "$error ERROR")
//                showToast(e
            //
            //
            //
            //
            //                rror ?: "")

            }

        })
    }

    fun setExpensis(obj : DriverExpenseModel){
        binding?.txtRides?.text = "£"+obj.rides
        binding?.txtEarning?.text = "£"+obj.earnings
        binding?.txtHours?.text = "£"+obj.hours
    }



}