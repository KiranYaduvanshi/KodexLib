package com.kodextech.project.kodexlib.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseDialogueFragment
import com.kodextech.project.kodexlib.databinding.SendConfirmationDialogBinding
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi

class SendConfirmationDialog : BaseDialogueFragment() {

    private var binding: SendConfirmationDialogBinding? = null
    var onClicksCallBack: ((ConfirmationOption) -> Unit)? = null

    var btnSMS: String? = ""
    var btnEmail: String? = ""

    override fun onSetupArguments() {
        arguments?.let {
            this.btnSMS = it.getString("btnSMS", "") ?: ""
            this.btnEmail = it.getString("btnEmail", "") ?: ""
        }
    }

    override fun onBindItemListenerOrViewVariables() {

    }

    override fun setupContentViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.send_confirmation_dialog, container, false)
        isCancelable = false

//        getListWorkers()

        binding?.btnSms?.setOnClickListener {
            onClicksCallBack?.invoke(ConfirmationOption.SMS)
        }

        binding?.ivCross?.setOnClickListener {
            dismiss()
            onClicksCallBack?.invoke(ConfirmationOption.CANCEL)

        }

        binding?.btnEmail?.setOnClickListener {
            onClicksCallBack?.invoke(ConfirmationOption.EMAIL)
        }

        return binding?.root!!
    }

    private fun getListWorkers() {
        showLoading()
        NetworkClass.callApi(URLApi.getListWorker(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()

            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                mActivity.showToast(error ?: "")
            }

        })
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
            onClicksCallBack: ((ConfirmationOption) -> Unit)? = null
        ): SendConfirmationDialog {
            val args = Bundle()
            val fragment = SendConfirmationDialog()
            fragment.onClicksCallBack = onClicksCallBack
            fragment.arguments = args
            return fragment
        }

    }

}

enum class ConfirmationOption {
    SMS, EMAIL, CANCEL
}