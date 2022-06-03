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
import com.kodextech.project.kodexlib.databinding.AddVanSheetBinding

class AddVanDialog : BaseDialogueFragment() {

    private var binding: AddVanSheetBinding? = null
    var title: String? = ""
    var floorNo: String? = ""
    var vanCountState: String? = ""
    var floorName: String? = ""
    var btnSubmitTitle: String? = ""

    var onClicksCallBack: ((AddVanOption, itemName: String, itemQuantity: String) -> Unit)? =
        null

    override fun onSetupArguments() {
        arguments.let {
            this.title = it?.getString("TITLE", "") ?: ""
            this.btnSubmitTitle = it?.getString("YES_TITLE", "") ?: ""
        }
        isCancelable = true
    }

    override fun onBindItemListenerOrViewVariables() {

    }


    override fun setupContentViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_van_sheet, container, false)


        binding?.btnDone?.setOnClickListener {

            floorName = binding?.tvFlatName?.text.toString()
            floorNo = binding?.tvFloorName?.text.toString()

            when {
                floorName.isNullOrEmpty() -> {
                    mActivity.showToast("Please Enter Falt Name")
                }
                floorNo.isNullOrEmpty() -> {
                    mActivity.showToast("Please Enter Floor Number")
                }
                else -> {
                    onClicksCallBack?.invoke(
                        AddVanOption.DONE,
                        floorName ?: "", floorNo ?: ""
                    )
                    dismiss()
                }
            }
        }

        return binding?.root!!
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
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setLayout(
            (getScreenWidth(context as Activity) * .9).toInt(),
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
            title: String? = "",
            btnSubmitTitle: String? = "Done",
            onClicksCallBack: ((AddVanOption, itemName: String, itemQuantity: String) -> Unit)? = null
        ): AddVanDialog {
            val args = Bundle()
            args.putString("TITLE", title)
            args.putString("YES_TITLE", btnSubmitTitle)
            val fragment = AddVanDialog()
            fragment.onClicksCallBack = onClicksCallBack
            fragment.arguments = args
            return fragment
        }


    }
}


enum class AddVanOption {
    DONE
}