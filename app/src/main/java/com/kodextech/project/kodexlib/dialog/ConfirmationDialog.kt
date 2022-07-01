package com.kodextech.project.kodexlib.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseDialogueFragment
import com.kodextech.project.kodexlib.databinding.ConfirmationDialogBinding
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import org.json.JSONObject

class ConfirmationDialog : BaseDialogueFragment() {

    private var binding: ConfirmationDialogBinding? = null

    private var chargeAmount: String? = null
    private var workerRemarks: String? = null
    private var jobId: String? = null
    private var payment: String? = null
    var onClicksCallBack: ((label: String) -> Unit)? = null

    override fun onSetupArguments() {
        arguments?.let {
            this.jobId = it.getString("JOBID", "") ?: ""

        }
    }

    override fun onBindItemListenerOrViewVariables() {

    }

    override fun setupContentViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.confirmation_dialog, container, false)

        getJobDetail(jobId)

        binding?.btnCash?.setOnClickListener {
            payment = "paid"
            changeColorState()
        }
        binding?.btnCancel?.setOnClickListener {
            dismiss()
        }
        binding?.btnInvoice?.setOnClickListener {
            payment = "un_paid"
            changeColorState()
        }

        binding?.btnOk?.setOnClickListener {
            workerRemarks = binding?.etcomments?.text.toString()
            chargeAmount = binding?.etRecievedAmount?.text.toString()
//            Log.i("id ---",""+)
            if (binding?.btnInvoice?.isChecked == true || binding?.btnCash?.isChecked == true) {

                finishJobCall(jobId, workerRemarks, chargeAmount, payment)

            } else {
                Toast.makeText(context, "Please select a payment method", Toast.LENGTH_SHORT).show()
            }

        }

        binding?.ivCross?.setOnClickListener {
            dismiss()
        }

        return binding?.root!!
    }

    private fun changeColorState() {
        if (payment == "un_paid") {
            binding?.btnInvoice?.isChecked = true
            binding?.btnCash?.isChecked = false
            binding?.btnCash?.buttonTintList = mActivity.getColorStateList(R.color.locationCol)
            binding?.btnInvoice?.buttonTintList = mActivity.getColorStateList(R.color.blue)
        } else {
            binding?.btnCash?.isChecked = true
            binding?.btnInvoice?.isChecked = false
            binding?.btnInvoice?.buttonTintList = mActivity.getColorStateList(R.color.locationCol)
            binding?.btnCash?.buttonTintList = mActivity.getColorStateList(R.color.blue)
        }
    }

    private fun finishJobCall(
        jobId: String? = null,
        workerRemarks: String? = null,
        chargeAmount: String? = null,
        payment: String?
    ) {
        showLoading()
        NetworkClass.callApi(URLApi.finishJob(
            job_uuid = jobId, worker_remarks = workerRemarks,
            charged_amount = chargeAmount, is_paid = payment
        ), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog?.dismiss()
                    generateInvoice(jobId)
                    dismiss()

                }, 100)

            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                mActivity.showToast(error ?: "")
            }

        })
    }

    private fun generateInvoice(jobId: String?) {
        showLoading()
        NetworkClass.callApi(URLApi.getInvoicePDF(job_uuid = jobId), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                mActivity.showToast(message ?: "")
                val dialog = SuccessDialog.newInstance() {
                }

                dialog.show(mActivity.supportFragmentManager, "")
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                mActivity.showToast(error ?: "")
            }

        })
    }

    private fun getJobDetail(jobId: String? = null) {
        showLoading()
        NetworkClass.callApi(URLApi.getSpecificJob(job_uuid = jobId.toString()), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                val json = JSONObject(response ?: "")
                val obj = Gson().fromJson(json.toString(), JobModel::class.java)
                setJobDetail(obj)
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                mActivity.showToast(error ?: "")
            }

        })
    }

    private fun setJobDetail(obj: JobModel? = null) {
        binding?.tvCustoemrName?.text = "Customer Name: " + obj?.first_name + " " + obj?.last_name
        binding?.tvCustomerAddress?.text = "Customer Address: " + obj?.pickup_address?.address1

        val price_amount = obj?.price_amount?.toDoubleOrNull() ?: 0.0
        val price_amount_rounded = String.format("%.2f", price_amount)

        binding?.tvPrice?.text = "Price: £ $price_amount_rounded"
        if(obj?.price_nature == "hourly-price"){        binding?.tvHours?.text = "Hours: ${obj?.actual_hours}"}else{
            binding?.tvHours?.visibility = View.GONE
        }

        binding?.etTotalAmount?.setText(price_amount_rounded)


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
            id: String? = "",
            onClicksCallBack: ((label: String) -> Unit)? = null
        ): ConfirmationDialog {
            val args = Bundle()
            args.putString("JOBID", id)
            val fragment = ConfirmationDialog()
            fragment.onClicksCallBack = onClicksCallBack
            fragment.arguments = args
            return fragment
        }

    }
}