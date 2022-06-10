package com.kodextech.project.kodexlib.ui.main.expenses

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.ActivityExpensesBinding

class ExpensesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpensesBinding
    private lateinit var viewStates: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onclickListeners()
    }

    private fun onclickListeners() {
        binding.tvDay.setOnClickListener {
            viewStates = "today"
            changeViewColor()
        }
        binding.tvMonth.setOnClickListener {
            viewStates = "current-month"
            changeViewColor()
        }
        binding.tvYear.setOnClickListener {
            viewStates = "current-year"
            changeViewColor()
        }
        binding.tvWeekly.setOnClickListener {
            viewStates = "current-week"
            changeViewColor()
        }

    }

    private fun changeViewColor() {
        when (viewStates) {
            "today" -> {
                binding.tvDay.setBackgroundColor(getColor(R.color.blue))
                binding.tvDay.setTextColor(getColor(R.color.white))
                binding.tvMonth.setBackgroundColor(getColor(R.color.gray))
                binding.tvMonth.setTextColor(getColor(R.color.cusCol))
                binding.tvWeekly.setBackgroundColor(getColor(R.color.gray))
                binding.tvWeekly.setTextColor(getColor(R.color.cusCol))
                binding.tvYear.setBackgroundColor(getColor(R.color.gray))
                binding.tvYear.setTextColor(getColor(R.color.cusCol))
            }
            "current-month" -> {
                binding.tvMonth.setBackgroundColor(getColor(R.color.blue))
                binding.tvMonth.setTextColor(getColor(R.color.white))
                binding.tvDay.setBackgroundColor(getColor(R.color.gray))
                binding.tvDay.setTextColor(getColor(R.color.cusCol))
                binding.tvWeekly.setBackgroundColor(getColor(R.color.gray))
                binding.tvWeekly.setTextColor(getColor(R.color.cusCol))
                binding.tvYear.setBackgroundColor(getColor(R.color.gray))
                binding.tvYear.setTextColor(getColor(R.color.cusCol))
            }
            "current-year" -> {
                binding.tvMonth.setBackgroundColor(getColor(R.color.gray))
                binding.tvMonth.setTextColor(getColor(R.color.cusCol))
                binding.tvDay.setBackgroundColor(getColor(R.color.gray))
                binding.tvDay.setTextColor(getColor(R.color.cusCol))
                binding.tvWeekly.setBackgroundColor(getColor(R.color.gray))
                binding.tvWeekly.setTextColor(getColor(R.color.cusCol))
                binding.tvYear.setBackgroundColor(getColor(R.color.blue))
                binding.tvYear.setTextColor(getColor(R.color.white))
            }
            "current-week" -> {
                binding.tvMonth.setBackgroundColor(getColor(R.color.gray))
                binding.tvMonth.setTextColor(getColor(R.color.cusCol))
                binding.tvDay.setBackgroundColor(getColor(R.color.gray))
                binding.tvDay.setTextColor(getColor(R.color.cusCol))
                binding.tvYear.setBackgroundColor(getColor(R.color.gray))
                binding.tvYear.setTextColor(getColor(R.color.cusCol))
                binding.tvWeekly.setBackgroundColor(getColor(R.color.blue))
                binding.tvWeekly.setTextColor(getColor(R.color.white))
            }
        }
    }

}