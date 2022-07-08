package com.kodextech.project.kodexlib.ui.main.expenses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityAddExpensesBinding
import com.kodextech.project.kodexlib.databinding.ActivityQuotationScreenBinding
import com.kodextech.project.kodexlib.network.LocalPreference
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.auth.LoginActivity

class AddExpensesActivity : BaseActivity() {

    private var binding: ActivityAddExpensesBinding? = null
    var fuel:String=""
    var advertising:String =""
    var equipment:String =""
    var vehicleManagment:String=""
    var otherPrice:String =""


    override fun onSetupViewGroup() {
        mViewGroup = binding?.expenseLL


    }

    override fun setupContentViewWithBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_expenses)

        binding?.ivBack?.setOnClickListener {
            onBackPressed()
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

        fuel=binding?.etFuel?.text.toString()
        advertising=binding?.etAdvertising?.text.toString()
        vehicleManagment=binding?.etMaintainanence?.text.toString()
        otherPrice=binding?.etMaintainanence?.text.toString()
        equipment=binding?.etEquipment?.text.toString()


        if (fuel?.isNullOrEmpty() == true) {
            binding?.etFuel?.error = "Required"
        } else if (vehicleManagment?.isNullOrEmpty() == true) {
            binding?.etMaintainanence?.error = "Required"
        }
        else if (advertising?.isNullOrEmpty() == true) {
            binding?.etAdvertising?.error = "Required"
        }
        else if (equipment?.isNullOrEmpty() == true) {
            binding?.etEquipment?.error = "Required"
        }
        else if (otherPrice?.isNullOrEmpty() == true) {
            binding?.etOther?.error = "Required"
        }
        else {


            addExpense(fuel,vehicleManagment,advertising,equipment,otherPrice)


        }
    }


    private fun addExpense(fuel:String,vehical_maintenance:String,advertising:String,equipment:String,other:String) {

        showLoading()
        NetworkClass.callApi(URLApi.addExpense(fuel = fuel, vehical_maintenance = vehical_maintenance, advertising = advertising,
            equipment = equipment, other = other), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {

                hideLoading()
                showBarToast("Expenses Added Successfully")
                finish()


            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                Toast.makeText(binding?.root?.context, "Error--- "+error, Toast.LENGTH_SHORT).show()
                Log.i("Search", "onErrorResponse: $error")

            }
        })
    }



}