package com.kodextech.project.kodexlib.utils

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseDialogueFragment
import com.kodextech.project.kodexlib.databinding.DialogDatetimepickerBinding

class DateTimepickerDialog : BaseDialogueFragment() {

    var binding: DialogDatetimepickerBinding? = null
    var onClicksCallBack: ((DatePickerOptions, dateString: String, timeString: String, timeStamp: String) -> Unit)? =
        null
    private var finalDate: String? = ""
    private var finalTime: String? = ""
    private var dateStamp: String? = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_datetimepicker, container, false)
        mView = binding!!.root
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initIds(view)

        binding?.btnOkone?.setOnClickListener {
//            finalDate = binding?.datetime?.date.toString().substring(0, 10)
//            finalTime = binding?.datetime?.date.toString().substring(11, 16)
//            dateStamp = binding?.datetime?.date?.getDateCustom("yyyy-MM-dd HH:mm:ss")
            dismiss()
            onClicksCallBack?.invoke(
                DatePickerOptions.OK,
                finalDate.toString(),
                finalTime.toString(),
                dateStamp.toString()
            )
        }
        binding?.btnCancelone?.setOnClickListener {
            dismiss()
            onClicksCallBack?.invoke(DatePickerOptions.CANCEL, "", "", "")
        }

    }

    override fun onSetupArguments() {

    }

    override fun onBindItemListenerOrViewVariables() {

    }

    override fun setupContentViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_datetimepicker, container, false)



        return binding?.root!!
    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {

    }


    private fun initIds(view: View?) {


//        binding?.datetime?.setMustBeOnFuture(true)

//        dateStamp = binding?.datetime?.date?.getDateCustom("yyyy-MM-dd HH:mm:ss")
//        binding?.datetime?.addOnDateChangedListener(object : SingleDateAndTimePickerDialog.Listener,
//            SingleDateAndTimePicker.OnDateChangedListener {
//            override fun onDateSelected(date: Date) {
//                finalDate = date.toString().substring(0, 10)
//                finalTime = date.toString().substring(11, 16)
//                dateStamp = date?.getDateCustom("yyyy-MM-dd HH:mm:ss")
//                Log.d("getDateData", "onDateSelected: " + date)
//            }
//
//            override fun onDateChanged(displayed: String?, date: Date?) {
//
//
//                finalDate = date.toString().substring(0, 10)
//                finalTime = date.toString().substring(11, 16)
//                dateStamp = date?.getDateCustom("yyyy-MM-dd HH:mm:ss")
//                Log.d("getDateData", "onDateChanged: " + finalDate)
//                Log.d("getDateData", "onDateChanged: " + finalTime)
//
//
//            }
//        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SlidingDialogAnimationOptions)
        isCancelable = true
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.setCanceledOnTouchOutside(true)
            dialog.setCancelable(true)
            dialog.window?.setLayout(width, height)
            dialog.window?.setGravity(Gravity.BOTTOM)
            dialog.window?.attributes?.windowAnimations = R.style.SlidingDialogAnimation
            dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }


    companion object {
        fun newInstance(
            onClicksCallBack: ((DatePickerOptions, dateString: String, timeString: String, timeStamp: String) -> Unit)? = null
        ): DateTimepickerDialog {
            val frag = DateTimepickerDialog()
            val bundle = Bundle()
            frag.arguments = bundle
            frag.onClicksCallBack = onClicksCallBack
            return frag
        }
    }

}

enum class DatePickerOptions {
    OK, CANCEL
}