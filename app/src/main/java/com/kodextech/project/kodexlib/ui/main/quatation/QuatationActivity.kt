package com.kodextech.project.kodexlib.ui.main.quatation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityCustomerListingBinding
import com.kodextech.project.kodexlib.databinding.ActivityQuatationBinding

class QuatationActivity : BaseActivity() {

    private var binding: ActivityQuatationBinding? = null
    private var quatationAdapter:QuatationAdapter? = null


    override fun onSetupViewGroup() {
        mViewGroup = binding?.qutationCustomer
    }

    override fun setupContentViewWithBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quatation)
        setAdapter()
    }

    override fun onRecycleBeforeDestroy() {
    }

    override fun onBecameInvisibleToUser() {
    }

    override fun onBecameVisibleToUser() {
    }

    override fun setupLoader() {
    }
    fun setAdapter(){

        quatationAdapter = QuatationAdapter(this )
        binding?.rvQuatation?.adapter =quatationAdapter



    }
}