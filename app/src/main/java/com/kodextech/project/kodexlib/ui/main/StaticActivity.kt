package com.kodextech.project.kodexlib.ui.main

import android.util.Log
import com.google.gson.Gson
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityStaticBinding
import com.kodextech.project.kodexlib.model.Statics
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import org.json.JSONObject

class StaticActivity : BaseActivity() {

    private var binding: ActivityStaticBinding? = null
    private var viewStates: String? = null


    override fun onSetupViewGroup() {
        mViewGroup = binding?.rlStatics
    }

    override fun setupContentViewWithBinding() {
        binding = ActivityStaticBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        onclickListeners()
        getStatics("1")
    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {
    }

    override fun onBecameVisibleToUser() {
    }

    override fun setupLoader() {
    }

    private fun getStatics(id: String) {

        showLoading()
        NetworkClass.callApi(URLApi.getStatics(id), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                val json = JSONObject(response ?: "")
                val obj = Gson().fromJson(json.toString(), Statics::class.java)
                setStatics(obj)
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                Log.i("Tester", "$error ERROR")
//                showToast(error ?: "")
            }

        })
    }

    fun setStatics(obj: Statics) {
        binding?.txtEarning?.text = "£"+obj.earnings
        binding?.txtHours?.text = "£"+obj.hours
        binding?.txtRides?.text = "£"+obj.rides
        binding?.txtLoss?.text = "£"+obj.loss
        binding?.txtProfit?.text = "£"+obj.profit
    }


    private fun onclickListeners() {
        binding?.tvDay?.setOnClickListener {
            viewStates = "today"
            changeViewColor()
            getStatics("1")
        }
        binding?.tvWeekly?.setOnClickListener {
            viewStates = "current-week"
            changeViewColor()
            getStatics("2")
        }
        binding?.tvMonth?.setOnClickListener {
            viewStates = "current-month"
            changeViewColor()
            getStatics("3")
        }
        binding?.tvYear?.setOnClickListener {
            viewStates = "current-year"
            changeViewColor()
            getStatics("4")
        }

        binding?.icBack?.setOnClickListener {
            onBackPressed()
        }
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

}