package com.kodextech.project.kodexlib.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Point
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
import com.kodextech.project.kodexlib.databinding.SelectWorkerDialogBinding
import com.kodextech.project.kodexlib.model.User
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.jobs.JobDetail
import com.kodextech.project.kodexlib.ui.main.worker.adapter.PortingListingAdapter
import com.kodextech.project.kodexlib.ui.main.worker.adapter.WorkerListingAdapter
import com.kodextech.project.kodexlib.utils.generateList
import org.json.JSONObject

class SelectWorkerDialog : BaseDialogueFragment() {

    private var binding: SelectWorkerDialogBinding? = null
    private var mData = ArrayList<User>()
    private var mAdapter: WorkerListingAdapter? = null
    private var driverInfo: User? = null
    var menCount=""
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
        binding = DataBindingUtil.inflate(inflater, R.layout.select_worker_dialog, container, false)
        isCancelable = false

         id = arguments?.getString("jobId")
         menCount = arguments?.getString("menCount").toString()
        Toast.makeText(activity, "Men Count ---- "+menCount, Toast.LENGTH_SHORT).show()
        getWorkerList()

        binding?.ivCross?.setOnClickListener {
            dismiss()
        }

        binding?.btnSelect?.setOnClickListener {

            if(driverInfo==null){
                mActivity.showToast("Please Select Worker")

            }else{
                if (menCount>"0"){
                    val dialog = SelectPorterDialog.newInstance()
                    val bundle = Bundle()
                    bundle.putString("jobId", id)
                    bundle.putString("menCount", menCount)
                    bundle.putSerializable("driverInfo", driverInfo)

                    dialog.arguments = bundle
                    dialog.show(this.childFragmentManager, "")
                }
                else{
                    val intent = Intent(mActivity, JobDetail::class.java)
                    intent.putExtra("driverInfo", driverInfo)
                    intent.putExtra("id", id)
                    intent.putExtra("check","0")

                    intent.putExtra("list", PortingListingAdapter.list)
                    mActivity.startActivity(intent)
                    dismiss()
                }
            }
        }
        return binding?.root!!
    }

    private fun getWorkerList() {
        showLoading()
        NetworkClass.callApi(URLApi.getUser(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                mActivity.hideLoading()
                val json = JSONObject(response ?: "")
                val json2 = json.optJSONArray("user")
                var data = generateList(json2.toString(), Array<User>::class.java)
                val worker =
                    data.filter { it.profile?.worker_type?.lowercase() == "driver".lowercase() }
                mData.addAll(worker)

                setList()
            }

            override fun onErrorResponse(error: String?, response: String?) {
                mActivity.hideLoading()
                mActivity.showToast(error ?: "")
            }

        })
    }

    private fun setList() {
        mAdapter = WorkerListingAdapter(mActivity, mData, "0") { user, position, isFor ->
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

        ): SelectWorkerDialog {
            val args = Bundle()

            val fragment = SelectWorkerDialog()
            fragment.arguments = args
            return fragment
        }
    }
}