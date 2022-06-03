package com.kodextech.project.kodexlib.ui.main.calendar

import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityCalendarBinding
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.calendar.adapter.CalendarBookingListing
import com.kodextech.project.kodexlib.ui.main.calendar.adapter.CalendarJobListing
import com.kodextech.project.kodexlib.ui.main.calendar.adapter.TypeJobs
import com.kodextech.project.kodexlib.utils.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Calendar : BaseActivity() {

    private var binding: ActivityCalendarBinding? = null
    private var screenSate: ListState? = ListState.JOBS
    private var viewState: ViewState? = ViewState.DAYS

    private var mJobData = ArrayList<JobModel>()
    private var mBookingData = ArrayList<JobModel>()
    private var mDataDate = ArrayList<String>()

    private var viewStates: String? = "day"

    private var mJobAdapter: CalendarJobListing? = null
    private var mBookingAdapter: CalendarBookingListing? = null


    override fun onSetupViewGroup() {
        mViewGroup = binding?.content
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar)

        initTopBar()
        setJobData()
        setBookingData()

        binding?.topBar?.ivBack?.setOnClickListener { onBackPressed() }
        binding?.tvDay?.setOnClickListener {
            viewStates = "day"
            changeViewColor()

        }
        binding?.tvMonth?.setOnClickListener {
            viewStates = "month"
            changeViewColor()

        }
        binding?.tvYear?.setOnClickListener {
            viewStates = "year"
            changeViewColor()

        }
        binding?.tvWeekly?.setOnClickListener {
            viewStates = "week"
            changeViewColor()

        }

    }


    private fun changeViewColor() {
        if (viewStates == "day") {
            binding?.tvDay?.setBackgroundColor(getColor(R.color.blue))
            binding?.tvDay?.setTextColor(getColor(R.color.white))
            binding?.tvMonth?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvMonth?.setTextColor(getColor(R.color.cusCol))
            binding?.tvWeekly?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvWeekly?.setTextColor(getColor(R.color.cusCol))
            binding?.tvYear?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvYear?.setTextColor(getColor(R.color.cusCol))
        } else if (viewStates == "month") {
            binding?.tvMonth?.setBackgroundColor(getColor(R.color.blue))
            binding?.tvMonth?.setTextColor(getColor(R.color.white))
            binding?.tvDay?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvDay?.setTextColor(getColor(R.color.cusCol))
            binding?.tvWeekly?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvWeekly?.setTextColor(getColor(R.color.cusCol))
            binding?.tvYear?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvYear?.setTextColor(getColor(R.color.cusCol))
        } else if (viewStates == "year") {
            binding?.tvMonth?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvMonth?.setTextColor(getColor(R.color.cusCol))
            binding?.tvDay?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvDay?.setTextColor(getColor(R.color.cusCol))
            binding?.tvWeekly?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvWeekly?.setTextColor(getColor(R.color.cusCol))
            binding?.tvYear?.setBackgroundColor(getColor(R.color.blue))
            binding?.tvYear?.setTextColor(getColor(R.color.white))
        } else if (viewStates == "week") {
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


    private fun getBooking(uuid: String? = null) {
        showLoading()
        NetworkClass.callApi(URLApi.getBookingList(booked_by_uuid = uuid), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                mBookingData.clear()
                val json = JSONObject(response ?: "")
                val model = json.optJSONArray("models" ?: "")
                val data = generateList(model.toString(), Array<JobModel>::class.java)
                val filterData = data.filter { it.job_status?.lowercase() == "completed".lowercase() }

                when (viewStates) {
                    "day" -> {
                        dayWiseBooking(filterData = filterData)
                    }
                    "week" -> {
//                        weekyWiseBooking(filterData = filterData)
                    }
                    "month" -> {
                        monthWiseBooking(filterData = filterData)
                    }
                    "year" -> {
                        yearWiseBooking(filterData = filterData)
                    }
                }

            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }

    private fun yearWiseBooking(filterData: List<JobModel>) {
        val filterBookingData = ArrayList<JobModel>()
        filterBookingData.addAll(filterData)
        mDataDate.clear()
        val dates = filterBookingData.map {
            it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
                ?.getFormated("yyyy") ?: ""
        }

        val arr = ArrayList<String>()
        arr.addAll(arr.distinct())
        mDataDate.addAll(arr.sorted())
//        mDataDate.forEach { item ->
//            mBookingData.add(JobModel(cellType = TypeBooking.HEADER, item))
//
//            mBookingData.addAll(filterBookingData.filter {
//                (it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
//                    ?.getFormated("yyyy") ?: "" == item)
//            })
//
//        }
        checkDataEmptyOrNot()
        mBookingAdapter?.notifyDataSetChanged()

    }

    private fun monthWiseBooking(filterData: List<JobModel>) {
        val filterBookingData = ArrayList<JobModel>()
        filterBookingData.addAll(filterData)
        mDataDate.clear()
        val dates = filterBookingData.map {
            it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
                ?.getFormated("MMMM") ?: ""
        }

        val arr = ArrayList<String>()
        arr.addAll(dates.distinct())
        mDataDate.addAll(arr.sorted())
//        mDataDate.forEach { item ->
//            mBookingData.add(JobModel(cellType = TypeBooking.HEADER, item))
//
//            mBookingData.addAll(filterBookingData.filter {
//                (it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
//                    ?.getFormated("MMMM") ?: "" == item)
//            })
//
//        }
        checkDataEmptyOrNot()
        mBookingAdapter?.notifyDataSetChanged()

    }


    private fun dayWiseBooking(filterData: List<JobModel>) {
        val filterBookingData = ArrayList<JobModel>()
        filterBookingData.addAll(filterData)
        mDataDate.clear()
        val dates = filterBookingData.map {
            it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
                ?.getFormated("MMMM d yyyy") ?: ""
        }

        val arr = ArrayList<String>()
        arr.addAll(dates.distinct())
        mDataDate.addAll(arr.sorted())
//        mDataDate.forEach { item ->
//            mBookingData.add(JobModel(cellType = TypeBooking.HEADER, item))
//
//            mBookingData.addAll(filterBookingData.filter {
//                (it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
//                    ?.getFormated("MMMM d yyyy") ?: "" == item)
//            })
//
//        }
        checkDataEmptyOrNot()
        mBookingAdapter?.notifyDataSetChanged()

    }


    private fun getJobs(customerUUId: String? = null) {
        showLoading()
        NetworkClass.callApi(URLApi.getJobList(customerUUId), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                mJobData.clear()
                val json = JSONObject(response ?: "")
                val model = json.optJSONArray("models" ?: "")
                val data = generateList(model.toString(), Array<JobModel>::class.java)

                val filterData =
                    data.filter { it.job_status?.lowercase() == "completed".lowercase() }
                when (viewStates) {
                    "day" -> {
                        dayWiseJob(filterData = filterData)
                    }
                    "week" -> {
                        weekyWiseJob(filterData = filterData)
                    }
                    "month" -> {
                        monthWiseJob(filterData = filterData)
                    }
                    "year" -> {
                        yearWiseJob(filterData = filterData)
                    }
                }
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }

    private fun weekyWiseJob(filterData: List<JobModel>) {
        val filterJobData = ArrayList<JobModel>()

        filterJobData.addAll(filterData)
        mDataDate.clear()

        val a = printDatesInMonth(
            java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
            java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
        )

        val rra = mDataDate.chunked(7)
        rra.forEachIndexed { index, list ->
            mJobData.add(
                JobModel(
                    cellType = TypeJobs.HEADER,
                    list.first().getFormated("yyyy MM dd")
                        .getFormated("MMMM dd") + " To " + list.last()
                        .getFormated("yyyy MM dd").getFormated("MMMM dd")
                )
            )
            list.forEachIndexed { index, s ->
                mJobData.addAll(filterJobData.filter {
                    (it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
                        ?.getFormated("yyyy MM dd") ?: "" == s)
                })
            }


        }
        Log.d("getList", rra.toString())

        checkDataEmptyOrNot()
        mJobAdapter?.notifyDataSetChanged()
    }

    private fun yearWiseJob(filterData: List<JobModel>) {
        val filterJobData = ArrayList<JobModel>()

        filterJobData.addAll(filterData)
        mDataDate.clear()
        val dates = filterJobData.map {
            it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
                ?.getFormated("yyyy") ?: ""
        }

        val arr = ArrayList<String>()
        arr.addAll(dates.distinct())
        mDataDate.addAll(arr.sorted())
        mDataDate.forEach { item ->
            mJobData.add(JobModel(cellType = TypeJobs.HEADER, item))
            mJobData.addAll(filterJobData.filter {
                (it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
                    ?.getFormated("yyyy") ?: "" == item)
            })

        }
        checkDataEmptyOrNot()
        mJobAdapter?.notifyDataSetChanged()
    }


    private fun monthWiseJob(filterData: List<JobModel>) {
        val filterJobData = ArrayList<JobModel>()

        filterJobData.addAll(filterData)
        mDataDate.clear()
        val dates = filterJobData.map {
            it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
                ?.getFormated("MMMM") ?: ""
        }

        val arr = ArrayList<String>()
        arr.addAll(dates.distinct())
        mDataDate.addAll(arr.sorted())
        mDataDate.forEach { item ->
            mJobData.add(JobModel(cellType = TypeJobs.HEADER, item))
            mJobData.addAll(filterJobData.filter {
                (it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
                    ?.getFormated("MMMM") ?: "" == item)
            })

        }
        checkDataEmptyOrNot()
        mJobAdapter?.notifyDataSetChanged()
    }

    private fun dayWiseJob(filterData: List<JobModel>) {
        val filterJobData = ArrayList<JobModel>()
        filterJobData.addAll(filterData)
        mDataDate.clear()
        val dates = filterJobData.map {
            it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
                ?.getFormated("MMMM d yyyy") ?: ""
        }

        val arr = ArrayList<String>()
        arr.addAll(dates.distinct())
        mDataDate.addAll(dates.sorted())
        mDataDate.forEach { item ->
            mJobData.add(JobModel(cellType = TypeJobs.HEADER, item))
            mJobData.addAll(filterJobData.filter {
                (it.start_time?.getFormated("yyyy-MM-dd'T'HH:mm:ss")
                    ?.getFormated("MMMM d yyyy") ?: "" == item)
            })

        }
        checkDataEmptyOrNot()
        mJobAdapter?.notifyDataSetChanged()

    }

    private fun checkDataEmptyOrNot() {
//        if (mJobData.isNullOrEmpty()) {
//            binding?.rlNoData?.visible()
//            binding?.rvJobs?.gone()
//        } else {
//            binding?.rlNoData?.gone()
//            binding?.rvJobs?.visible()
//        }
    }

    private fun setJobData() {

        mJobAdapter = CalendarJobListing(this, mJobData)
        binding?.rvJobs?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding?.rvJobs?.adapter = mJobAdapter

    }

    private fun setBookingData() {
        mBookingAdapter = CalendarBookingListing(this, mBookingData)
        binding?.rvBookings?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding?.rvBookings?.adapter = mBookingAdapter
    }

    private fun initTopBar() {
        binding?.topBar?.tvText?.text = "Calendar View"
        binding?.topBar?.ivLogout?.gone()
    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {
    }

    override fun setupLoader() {

    }

    fun printDatesInMonth(year: Int, month: Int) {
        val fmt = SimpleDateFormat("dd/MM/yyyy")
        val cal: java.util.Calendar = java.util.Calendar.getInstance()
        cal.clear()
        cal.set(year, month, 1)
        val daysInMonth: Int = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        for (i in 0 until daysInMonth) {
            showBarToast(fmt.format(cal.time))
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
            val objDate: Date = cal.time

            val formateDate = SimpleDateFormat("yyyy MM dd").format(objDate)
            mDataDate.add(formateDate)
        }
    }

}


enum class ListState {
    JOBS, BOOKINGS
}

enum class ViewState {
    DAYS, WEEKS, MONTHLY
}