package com.kodextech.project.kodexlib.ui.main.auth

import android.content.Intent
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityLoginBinding
import com.kodextech.project.kodexlib.model.EmailCommunicationModel
import com.kodextech.project.kodexlib.model.UserModel
import com.kodextech.project.kodexlib.network.LocalPreference
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.dashboard.Dashboard
import com.kodextech.project.kodexlib.ui.main.dashboard.DriverDashboardActivity
import org.json.JSONObject


class LoginActivity : BaseActivity() {

    private var binding: ActivityLoginBinding? = null

    private var email: String? = null
    private var password: String? = null
    var isClicked: Boolean = true

    override fun onSetupViewGroup() {
        mViewGroup = findViewById(R.id.contentLogin)
        makeLightContentStatusBar()
        val emailCommunicationModel = EmailCommunicationModel(false,"",);
        emailCommunicationModel.data[0].Email
    }

    override fun setupContentViewWithBinding() {

        statusBarColor(this.getColor(R.color.white))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding?.etEmail?.hint = setMandatoryHintData("Enter Email");
        binding?.etPassword?.hint = setMandatoryHintData("Enter Password");

        binding?.btnLogIn?.setOnClickListener {
            validation()
//            val intent = Intent(this, Dashboard::class.java)
//            startActivity(intent)
        }

        binding?.showPass?.setOnClickListener {
            if (isClicked) {
                binding?.etPassword?.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding?.showPass?.setImageResource(R.drawable.ic_eye)
                binding?.etPassword?.setSelection(binding?.etPassword?.text.toString().length)

                isClicked = false
            } else {
                binding?.etPassword?.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding?.showPass?.setImageResource(R.drawable.ic_eye_closed)
                binding?.etPassword?.setSelection(binding?.etPassword?.text.toString().length)

                isClicked = true
            }
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun validation() {
        email = binding?.etEmail?.text.toString()
        password = binding?.etPassword?.text.toString()
        if (email.isNullOrEmpty()) {
            binding?.etEmail?.error = "Required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding?.etEmail?.text.toString().trim())
                .matches()
        ) {
            binding?.etEmail?.error = "Invalid email format"
            //  showBarToast("Invalid Format")
        } else if (password.isNullOrEmpty()) {
            binding?.etPassword?.error = "Required"
        } else {
            callLogin(
                email ?: "".trim(),
                password ?: "".trim()
            )
        }
    }

    private fun callLogin(email: String, password: String) {
        showLoading()
        NetworkClass.callApi(
            URLApi.login(email = email, password = password, "0"),
            object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()

                    val json = JSONObject(response ?: "")

                    LocalPreference.shared.user =
                        Gson().fromJson(json.toString(), UserModel::class.java)
                    LocalPreference.shared.token = LocalPreference.shared.user?.access_token

                    if (LocalPreference?.shared?.user?.user?.profile?.worker_type?.lowercase() == "Driver".lowercase()) {
//                        val intent = Intent(this@LoginActivity, JobsListing::class.java)
//                        intent.putExtra("from", "driver")
//                        startActivity(intent)
//                        finish()
                        val intent = Intent(this@LoginActivity, DriverDashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@LoginActivity, Dashboard::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                }
            })
    }

 override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {

    }

    override fun setupLoader() {

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