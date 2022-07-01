package com.kodextech.project.kodexlib.ui.main.worker

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityWorkerListingBinding
import com.kodextech.project.kodexlib.dialog.AddWorkerDialog
import com.kodextech.project.kodexlib.model.User
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.worker.adapter.WorkerListingAdapter
import com.kodextech.project.kodexlib.utils.generateList
import com.kodextech.project.kodexlib.utils.gone
import org.json.JSONObject

class WorkerListing : BaseActivity() {

    private var binding: ActivityWorkerListingBinding? = null

    private var mData = ArrayList<User>()
    private var mAdapter: WorkerListingAdapter? = null


    override fun onSetupViewGroup() {
        mViewGroup = binding?.contentWorker
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_worker_listing)
        initTopBar()
        setWorkerRecycler()
        getWorkerList()

        binding?.workerTopBar?.ivBack?.setOnClickListener { onBackPressed() }

        binding?.ivAddWorker?.setOnClickListener {
            val dialog = AddWorkerDialog.newInstance("Create Worker") {
                //Worker Add in AddWorkerDialog
            }

            dialog.show(supportFragmentManager, "")
        }

    }

    private fun getWorkerList() {
        showLoading()
        NetworkClass.callApi(URLApi.getUser(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                mData.clear()
                val json = JSONObject(response ?: "")
                val json2 = json.optJSONArray("user")
                var data = generateList(json2.toString(), Array<User>::class.java)
//                val worker =
//                    data.filter { it.profile_type?.lowercase() == "worker".lowercase() }
                mData.addAll(data)
                setWorkerRecycler()

            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }
        })
    }

    private fun setWorkerRecycler() {

        val sortedList = mData.sortedByDescending {
            it.profile?.id
        }

        mAdapter = WorkerListingAdapter(this, ArrayList(sortedList), "1") { user, position, isFor ->
            if (isFor == "updated") {
                getWorkerList()
            }
        }
        binding?.rvWorker?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding?.rvWorker?.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
    }

    private fun initTopBar() {
        binding?.workerTopBar?.tvText?.text = "Worker List"
        binding?.workerTopBar?.ivLogout?.gone()
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