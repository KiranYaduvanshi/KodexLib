package com.kodextech.project.kodexlib.ui.main.expenses

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityDriverExpenseBinding
import com.kodextech.project.kodexlib.databinding.ActivityStaticBinding

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
    }

    private fun onclickListeners() {
        binding?.tvDay?.setOnClickListener {
            viewStates = "today"
            changeViewColor()
        }
        binding?.tvWeekly?.setOnClickListener {
            viewStates = "current-week"
            changeViewColor()
        }
        binding?.tvMonth?.setOnClickListener {
            viewStates = "current-month"
            changeViewColor()
        }
        binding?.tvYear?.setOnClickListener {
            viewStates = "current-year"
            changeViewColor()

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

}