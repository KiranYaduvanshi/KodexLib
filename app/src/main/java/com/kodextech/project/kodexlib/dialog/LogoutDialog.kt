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
import com.kodextech.project.kodexlib.databinding.AppDialogueBinding

class LogoutDialog : BaseDialogueFragment() {

    lateinit var binding: AppDialogueBinding
    var onClicksCallBack: ((AppAlertOption) -> Unit)? = null
    var title: String? = ""
    var description: String? = ""
    var btnYesTitle: String? = ""
    var btnNoTitle: String? = ""


    override fun onSetupArguments() {
        arguments?.let {
            this.title = it.getString("TITLE", "") ?: ""
            this.description = it.getString("DESCRIPTION", "") ?: ""
            this.btnYesTitle = it.getString("YES_TITLE", "") ?: ""
            this.btnNoTitle = it.getString("NO_TITLE", "") ?: ""
        }
    }

    override fun onBindItemListenerOrViewVariables() {

    }

    override fun setupContentViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.app_dialogue, container, false)
        isCancelable = false
        binding.btnYes.setOnClickListener {
            dismiss()
            onClicksCallBack?.invoke(AppAlertOption.YES)
        }
        binding.btnNo.setOnClickListener {
            dismiss()
            onClicksCallBack?.invoke(AppAlertOption.NO)
        }
        binding.ivCross.setOnClickListener {
            dismiss()
        }

        binding.tvTitle.text = title
        binding.tvDescription.text = description
        binding.btnYes.text = btnYesTitle
        binding.btnNo.text = btnNoTitle

        return binding.root
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
            title: String? = "LOGOUT",
            description: String? = "Are you sure you want to logout?",
            btnYesTitle: String? = "YES",
            btnNoTitle: String? = "NO",
            onClicksCallBack: ((AppAlertOption) -> Unit)? = null
        ): LogoutDialog {
            val args = Bundle()
            args.putString("TITLE", title)
            args.putString("DESCRIPTION", description)
            args.putString("YES_TITLE", btnYesTitle)
            args.putString("NO_TITLE", btnNoTitle)
            val fragment = LogoutDialog()
            fragment.onClicksCallBack = onClicksCallBack
            fragment.arguments = args
            return fragment
        }

    }
}

enum class AppAlertOption {
    YES, NO
}