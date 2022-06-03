package com.kodextech.project.kodexlib.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseDialogueFragment
import com.kodextech.project.kodexlib.databinding.SuccessDialogBinding
import com.kodextech.project.kodexlib.ui.main.jobs.JobsListing
import com.kodextech.project.kodexlib.utils.gone

class SuccessDialog : BaseDialogueFragment() {

    private var binding: SuccessDialogBinding? = null

    override fun onSetupArguments() {

    }

    override fun onBindItemListenerOrViewVariables() {

    }

    override fun setupContentViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.success_dialog, container, false
        )
        binding?.ivCross?.gone()
        binding?.btnOk?.setOnClickListener {
            val intent = Intent(mActivity, JobsListing::class.java)
            mActivity.startActivity(intent)
            mActivity.finish()
        }
        return binding?.root!!
    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {

    }


    companion object {
        fun newInstance(
            tvLabel: String? = "",
            isFor: String? = "",
            onClicksCallBack: ((label: String) -> Unit)? = null
        ): SuccessDialog {
            val args = Bundle()
            args.putString("TITLE", tvLabel)
            args.putString("isFor", isFor)
            val fragment = SuccessDialog()
//            fragment.onClicksCallBack = onClicksCallBack
            fragment.arguments = args
            return fragment
        }

    }
}