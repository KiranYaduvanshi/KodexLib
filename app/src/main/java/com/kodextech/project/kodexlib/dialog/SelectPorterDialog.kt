package com.kodextech.project.kodexlib.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseDialogueFragment
import com.kodextech.project.kodexlib.databinding.ActivitySelectPorterDialogBinding
import com.kodextech.project.kodexlib.model.User
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.jobs.JobDetail
import com.kodextech.project.kodexlib.ui.main.worker.adapter.PortingListingAdapter
import com.kodextech.project.kodexlib.ui.main.worker.adapter.WorkerListingAdapter
import com.kodextech.project.kodexlib.utils.generateList
import org.json.JSONObject

class SelectPorterDialog : BaseDialogueFragment() {
    private var binding: ActivitySelectPorterDialogBinding? = null
    private var mData = ArrayList<User>()
    private var mAdapter: PortingListingAdapter? = null
    private var driverInfo: User? = null
    private  var menCount:String =""
    private  var id:String? = null



    override fun onSetupArguments() {
    }

    override fun onBindItemListenerOrViewVariables() {
    }

    override fun setupContentViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_select_porter_dialog, container, false)
        isCancelable = false


         id = arguments?.getString("jobId")
         menCount = arguments?.getString("menCount").toString()
        driverInfo = arguments?.getSerializable("driverInfo") as User?




        getWorkerList(menCount)

        binding?.ivCross?.setOnClickListener {
            dismiss()
        }

        binding?.btnSelect?.setOnClickListener {
            if(PortingListingAdapter.list.size ==  0){
                mActivity.showToast("Please Select Porter")
            }else{
                 if (PortingListingAdapter.list.size == Integer.parseInt(menCount)){
                     val intent = Intent(mActivity, JobDetail::class.java)
                     intent.putExtra("driverInfo", driverInfo)
                     intent.putExtra("id", id)
                     intent.putExtra("check","0")
                     intent.putExtra("list", PortingListingAdapter.list)

                     mActivity.startActivity(intent)
                     dismiss()

                 }
                else if (PortingListingAdapter.list.size < Integer.parseInt(menCount)){
                     Toast.makeText(binding?.root?.context, "Please select more Porter", Toast.LENGTH_SHORT).show()
                }
                else{

                }
            }
        }

        return binding?.root!!
    }

    override fun onDestroy() {
        PortingListingAdapter.list.clear()
        super.onDestroy()
    }
    private fun getWorkerList( menCount:String) {
        showLoading()
        NetworkClass.callApi(URLApi.getUser(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                mActivity.hideLoading()
                val json = JSONObject(response ?: "")
                val json2 = json.optJSONArray("user")
                var data = generateList(json2.toString(), Array<User>::class.java)
                val worker =
                    data.filter { it.profile?.worker_type?.lowercase() == "porter".lowercase() }
                mData.addAll(worker)

                setList(menCount = menCount)
            }

            override fun onErrorResponse(error: String?, response: String?) {
                mActivity.hideLoading()
                mActivity.showToast(error ?: "")
            }

        })
    }

    private fun setList( menCount:String) {
        mAdapter = PortingListingAdapter(mActivity, mData, "0",menCount) { user, position, isFor ->
            if (isFor == "0") {
                driverInfo = user
            }
        }
        binding?.rvWorkers?.layoutManager =
            LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false)
        binding?.rvWorkers?.adapter = mAdapter

        mAdapter?.notifyDataSetChanged()
    }



    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {
    }

    override fun onBecameVisibleToUser() {
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        dialog?.window?.setLayout(
            (getScreenWidth(mActivity) * .9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }

    private fun getScreenWidth(activity: Activity): Int {
        val size = Point()
        activity.windowManager.defaultDisplay.getSize(size)
        return size.x
    }

    companion object {
        fun newInstance(

        ): SelectPorterDialog {
            val args = Bundle()

            val fragment =SelectPorterDialog ()
            fragment.arguments = args
            return fragment
        }

    }

}