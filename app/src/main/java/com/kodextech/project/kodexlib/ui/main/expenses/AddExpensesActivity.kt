package com.kodextech.project.kodexlib.ui.main.expenses

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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