package com.kodextech.project.kodexlib.ui.main.expenses

import android.util.Log
import com.google.gson.Gson
import com.kodextech.glitterupsapp.utils.loader.ProgressDialogue
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityExpensesBinding
import com.kodextech.project.kodexlib.model.Expensis
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import org.json.JSONObject

class ExpensesActivity : BaseActivity() {
    private var binding: ActivityExpensesBinding? =null
    private  var viewStates: String? = null


    override fun onSetupViewGroup() {
        mViewGroup = binding?.rlExpensis
    }

    override fun setupContentViewWithBinding() {
        binding = ActivityExpensesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        onclickListeners()
        getExpenses("1")
    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {
    }

    override fun onBecameVisibleToUser() {
    }

    override fun setupLoader() {
    }

    private fun getExpenses(id : String) {

        showLoading()
        NetworkClass.callApi(URLApi.getExpensis(id), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                val json = JSONObject(response ?: "")
                val obj = Gson().fromJson(json.toString(), Expensis::class.java)
                print(obj.Fuel)
                setExpensis(obj)
                Log.i("Tester", "${obj.Fuel} RESPONSE")
                Log.i("Tester", "${message} RESPONSE")
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                Log.i("Tester", "$error ERROR")
//                showToast(error ?: "")
            }

        })
    }

    fun setExpensis(obj : Expensis){
     binding?.txtFuel?.text = "$"+obj.Fuel
     binding?.txtLabour?.text = "$"+obj.Labour
     binding?.txtVehicleMaintainance?.text = "$"+obj.VehicleMaintainence
     binding?.txtAdvertising?.text = "$ No variable "
     binding?.txtEquipment?.text = "$"+obj.Equipment
     binding?.txtOther?.text = "$"+obj.Other
    }

//    fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//    }
//
//
//    fun showLoading(text: String = "Please wait...") {
//        runOnUiThread {
//            try {
//                if (!pd.isVisible && !pd.isAdded) {
//                    pd.show(supportFragmentManager, "pd")
//                }
//                android.os.Handler(Looper.getMainLooper()).postDelayed({
//                    pd.txtProgress?.text = text
//                }, 200)
//            } catch (e: IllegalStateException) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//
//    fun hideLoading() {
//        try {
//            if (pd.isAdded || pd.isVisible) pd.dismiss()
//        } catch (e: IllegalStateException) {
//            e.printStackTrace()
//        }
//    }
//

    private fun onclickListeners() {
        binding?.tvDay?.setOnClickListener {
            viewStates = "today"
            changeViewColor()
            getExpenses("1")
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
        binding?.tvWeekly?.setOnClickListener {
            viewStates = "current-week"
            changeViewColor()
            getExpenses("2")
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