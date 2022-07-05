package com.kodextech.project.kodexlib.ui.main.quatation

import android.content.Intent
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityQuotationScreenBinding
import com.kodextech.project.kodexlib.ui.main.customer.CustomerListing

class QuotationScreenActivity : BaseActivity() {

    private var binding: ActivityQuotationScreenBinding? = null
    override fun onSetupViewGroup() {
        mViewGroup = binding?.qutationCustomer

    }

    override fun setupContentViewWithBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quotation_screen)

        binding?.tvCustomerName?.setOnClickListener {
            val intent = Intent(this, SearchCustomerActivity::class.java)
            startActivity(intent)
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