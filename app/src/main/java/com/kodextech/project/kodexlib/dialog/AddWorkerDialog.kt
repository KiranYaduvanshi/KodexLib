package com.kodextech.project.kodexlib.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseDialogueFragment
import com.kodextech.project.kodexlib.databinding.AddWorkerDialogBinding
import com.kodextech.project.kodexlib.model.User
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.worker.WorkerListing
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.isValidEmail
import com.kodextech.project.kodexlib.utils.visible
import org.json.JSONObject

class AddWorkerDialog : BaseDialogueFragment() {

    private var binding: AddWorkerDialogBinding? = null
    var workerName: String? = ""
    var workerPassword: String? = null
    var workerType: String? = ""
    var tvLabel: String? = ""
    var workerEmail: String? = ""
    var workerHourlyRate: String? = ""
    var isFor: String? = ""
    var workerUUID: String? = ""
    var dialogAW: AddWorkerDialog? = null
    var isClicked: Boolean = true

    var onClicksCallBack: ((label: String) -> Unit)? = null
    override fun onSetupArguments() {
        arguments?.let {
            this.tvLabel = it.getString("TITLE", "") ?: ""
            this.isFor = it.getString("ISFOR", "") ?: ""
            this.workerUUID = it.getString("WORKERUUID", "") ?: ""
        }
    }
    override fun onBindItemListenerOrViewVariables() {

    }
    override fun setupContentViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_worker_dialog, container, false)
        isCancelable = false

        binding?.etWrkerName?.hint = setMandatoryHintData("Worker Name")
        binding?.etWorkerEmail?.hint = setMandatoryHintData("Work Email")
        binding?.etWorkerPassword?.hint = setMandatoryHintData("Password")
        binding?.etHourlyRate?.hint = setMandatoryHintData("Hourly Rate")
        binding?.spWorkerType?.setItems("Admin", "Office", "Driver","Porter")
        binding?.spWorkerType?.text = setMandatoryHintData("Worker Type")

        workerName = binding?.etWrkerName?.text.toString()
        workerPassword = binding?.etWorkerPassword?.text.toString()
        workerEmail = binding?.etWorkerEmail?.text.toString()
        workerHourlyRate = binding?.etHourlyRate?.text.toString()

        binding?.spWorkerType?.setOnItemSelectedListener { view, position, id, item ->
            workerType = item.toString()
        }
        binding?.showPass?.setOnClickListener {
            if (isClicked) {
                binding?.etWorkerPassword?.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding?.showPass?.setImageResource(R.drawable.ic_eye)
                binding?.etWorkerPassword?.setSelection(binding?.etWorkerPassword?.text.toString().length)
                isClicked = false
            } else {
                binding?.etWorkerPassword?.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding?.showPass?.setImageResource(R.drawable.ic_eye_closed)
                binding?.etWorkerPassword?.setSelection(binding?.etWorkerPassword?.text.toString().length)

                isClicked = true
            }
        }

        binding?.btnLogin?.setOnClickListener {

            workerName = binding?.etWrkerName?.text.toString()
            workerPassword = binding?.etWorkerPassword?.text.toString()
            workerEmail = binding?.etWorkerEmail?.text.toString()
            workerHourlyRate = binding?.etHourlyRate?.text.toString()

            if (workerName.isNullOrEmpty()) {
                mActivity.showToast("Enter Name")
            } else if (workerEmail.isNullOrEmpty()) {
                mActivity.showToast("Enter Email")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(workerEmail.toString().trim())
                    .matches()
            ) {
                binding?.etWorkerEmail?.error="Invalid email format"
                mActivity.showToast("Invalid Format")
            } else if (workerType.isNullOrEmpty()) {
                mActivity.showToast("Select Worker Type")

            } else if (workerHourlyRate.isNullOrEmpty()) {
                binding?.etHourlyRate?.error = "Required"
                mActivity.showToast("Select Worker Hourly Rate")

            }
            else if(workerName.toString().length < 3){
                mActivity.showToast("The name must be at least 3 characters ")


            }
            else {
                if (tvLabel != "Update Worker") {
                    when {
                        workerPassword.isNullOrEmpty() -> {
                            mActivity.showToast("Enter Password")
                        }
                        workerPassword?.length ?: 0 < 8 -> {
                            mActivity.showToast("Password Must be 8 Digits")
                        }
                        else -> {
                            addWorkerCall(
                                workerName.toString(),
                                workerEmail.toString(),
                                workerPassword.toString(),
                                workerType.toString(),
                                workerHourlyRate.toString()
                            )
                        }
                    }
                } else {
                    updateWorker(
                        workerUUID.toString(),
                        workerName.toString(),
                        workerEmail.toString(),
                        workerType.toString(),
                        workerHourlyRate.toString()
                    )
                }


            }


        }

        binding?.ivCross?.setOnClickListener {
            dismiss()
        }


        binding?.tvLabel?.text = tvLabel

        if (tvLabel == "Update Worker") {
            binding?.rlPassword?.gone()
            getSpecificWorker(workerUUID.toString())
        } else {
            binding?.rlPassword?.visible()
        }
        return binding?.root!!
    }

    private fun updateWorker(
        uuid: String,
        name: String,
        email: String,
        type: String,
        rate: String
    ) {
        showLoading()
        NetworkClass.callApi(URLApi.addWorker(
            worker_uuid = uuid,
            email = email,
            password = null,
            first_name = name,
            device_type = "android",
            worker_type = type,
            price_amount = workerHourlyRate
        ), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                mActivity.showToast(message ?: "")
                onClicksCallBack?.invoke("updated")
                dismiss()
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                mActivity.showToast(error ?: "")
            }
        })
    }

    private fun getSpecificWorker(uuid: String) {
        showLoading()
        NetworkClass.callApi(URLApi.getUser(user_uuid = uuid), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()

                val json = JSONObject(response ?: "")
                val json2 = json.optJSONObject("user")
                val data = Gson().fromJson(json2.toString() ?: "", User::class.java)
                setWorkerData(data)


            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                mActivity.showToast(error ?: "")
            }

        })
    }

    private fun setWorkerData(data: User?) {
        workerName = data?.profile?.first_name?.trim() ?: ""
        workerType = data?.profile?.worker_type?.trim() ?: ""
        workerEmail = data?.email?.trim() ?: ""
        workerUUID = data?.profile?.uuid?.trim() ?: ""
        workerHourlyRate = data?.profile?.price_amount?.trim() ?: ""

        binding?.etWrkerName?.setText(workerName.toString())
        binding?.etWorkerEmail?.setText(workerEmail.toString())
        binding?.spWorkerType?.text = workerType.toString()
        binding?.etHourlyRate?.setText(workerHourlyRate?.toString())
    }

    private fun addWorkerCall(
        name: String? = null,
        email: String? = null,
        password: String? = null,
        type: String? = null,
        workerHourlyRate: String,
    ){
        showLoading()
        NetworkClass.callApi(URLApi.addWorker(
            worker_uuid = null,
            email = email,
            password = password,
            first_name = name,
            device_type = "android",
            worker_type = type,
            price_amount = workerHourlyRate
        ), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                mActivity.showToast(message ?: "")
//                Toast.makeText(mActivity, "resposne ---->>>>", Toast.LENGTH_SHORT).show()
                val intent = Intent(mActivity, WorkerListing::class.java)
                startActivity(intent)
                Log.i("error","resposne "+response)

                dismiss()
            }
            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
               // mActivity.showToast(error ?: "")
                Toast.makeText(mActivity, "Email already exist", Toast.LENGTH_SHORT).show()
                Log.i("error","error "+error)
                mActivity.showToast(error ?: "")
//                Toast.makeText(mActivity, "Email already exist", Toast.LENGTH_SHORT).show()
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
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private fun getScreenWidth(activity: Activity): Int {
        val size = Point()
        activity.windowManager.defaultDisplay.getSize(size)
        return size.x
    }

    companion object {
        fun newInstance(
            tvLabel: String? = "",
            isFor: String? = "",
            WORKERUUID: String? = "",
            onClicksCallBack: ((label: String) -> Unit)? = null
        ): AddWorkerDialog {
            val args = Bundle()

            args.putString("TITLE", tvLabel)
            args.putString("isFor", isFor)
            args.putString("WORKERUUID", WORKERUUID)
            val fragment = AddWorkerDialog()
            fragment.onClicksCallBack = onClicksCallBack
            fragment.arguments = args
            return fragment
        }

    }
    fun setMandatoryHintData(hintData: String): SpannableStringBuilder? {
        val colored = " *"
        val builder = SpannableStringBuilder()
        builder.append(hintData)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length
        builder.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.red)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return builder
    }

}

enum class AddWorkerOption {
    LOGIN
}