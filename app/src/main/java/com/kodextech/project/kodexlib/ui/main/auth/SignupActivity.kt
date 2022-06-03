package com.kodextech.project.kodexlib.ui.main.auth

import android.content.Intent
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.binaryfork.spanny.Spanny
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivitySignupBinding
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.termsAndServices.TermsServices
import com.yesterselga.countrypicker.CountryPicker
import com.yesterselga.countrypicker.Theme


class SignupActivity : BaseActivity() {
    private var binding: ActivitySignupBinding? = null

    private var firstName: String? = null
    private var lastName: String? = null
    private var phoneCode: String? = null
    private var phoneNo: String? = null
    private var email: String? = null
    private var password: String? = null
    private var confirmPassword: String? = null
    private var address: String? = null
    private var number: String? = null
    private var type: String? = null

    override fun onSetupViewGroup() {
        mViewGroup = findViewById(R.id.content)
        makeLightContentStatusBar()


    }

    override fun setupContentViewWithBinding() {
        statusBarColor(this.getColor(R.color.white))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding?.ivBack?.setOnClickListener { onBackPressed() }

        val spanny = Spanny("I accept Modern Mover's", ForegroundColorSpan(Color.BLACK))
            .append(" Terms and Conditions", ForegroundColorSpan(Color.BLUE))


        setCustomerTypes()

        binding?.tvTerms?.text = spanny

        binding?.tvCountryCode?.setOnClickListener {
            val picker = CountryPicker.newInstance("Select Country", Theme.LIGHT)

            picker.setListener { name, code, dialCode, flagDrawableResID ->
                binding?.tvCountryCode?.text = "${code} ${dialCode}"
                phoneCode = dialCode
                picker.dismiss()
            }
            picker.setStyle(DialogFragment.STYLE_NORMAL, R.style.countrypicker_style)
            picker.show(supportFragmentManager, "Select Country")
        }

        binding?.spType?.setOnItemSelectedListener { view, position, id, item ->
            when {
                item.toString().lowercase() == "Admin".lowercase() -> {
                    type = "admin"
                }
                item.toString().lowercase() == "Customer".lowercase() -> {
                    type = "customer"

                }
                item.toString().lowercase() == "Driver".lowercase() -> {
                    type = "driver"
                }
                item.toString().lowercase() == "Worker".lowercase() -> {
                    type = "worker"
                }
            }
        }

        binding?.btnSignUp?.setOnClickListener {
            signUpValidation()
        }


        binding?.tvTerms?.setOnClickListener {
            val intent = Intent(this@SignupActivity, TermsServices::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setCustomerTypes() {
        val array = ArrayList<String>()

        array.clear()
        array.add("Admin")
        array.add("Customer")
        array.add("Driver")
        binding?.spType?.setItems(array)

    }

    private fun signUpValidation() {
        firstName = binding?.etFirstName?.text.toString()
        lastName = binding?.etLastName?.text.toString()
        email = binding?.etEmail?.text.toString()
        phoneNo = binding?.phoneNo?.text.toString()
        password = binding?.etPassword?.text.toString()
        confirmPassword = binding?.etConfirmPassword?.text.toString()
        address = binding?.etAddress?.text.toString()
        number = phoneCode + phoneNo

        if (firstName.isNullOrEmpty()) {
            binding?.etFirstName?.error = "Required"
        } else if (lastName.isNullOrEmpty()) {
            binding?.etLastName?.error = "Required"
        } else if (binding?.tvCountryCode?.text.isNullOrEmpty()) {
            showBarToast("Please Select Country Code")
        } else if (binding?.phoneNo?.text.isNullOrEmpty()) {
            binding?.phoneNo?.error = "Required"
        } else if (number!!.length ?: 0 != 13) {
            showBarToast("Invalid Number")
        } else if (email.isNullOrEmpty()) {
            binding?.etEmail?.error = "Required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding?.etEmail?.text.toString())
                .matches()
        ) {
            showBarToast("Invalid Format")
        } else if (password.isNullOrEmpty()) {
            binding?.etPassword?.error = "Required"
        } else if (password!!.length < 8) {
            showBarToast("Please Enter 8 Digit Password")
        } else if (confirmPassword.isNullOrEmpty()) {
            binding?.etConfirmPassword?.error = "Required"
        } else if (password != confirmPassword) {
            showBarToast("Password does not match")
        } else if (type.isNullOrEmpty()) {
            showBarToast("Password Select Customer Type")
        } else if (address.isNullOrEmpty()) {
            binding?.etAddress?.error = "Required"
        } else {
            callSignUp(
                email ?: "".trim(),
                password ?: "".trim(),
                confirmPassword ?: "".trim(),
                firstName ?: "".trim(),
                lastName ?: "".trim(),
                phoneCode ?: "".trim(),
                phoneNo ?: "".trim(),
                address ?: "".trim()
            )
        }


    }

    private fun callSignUp(
        email: String,
        password: String,
        confirmPassword: String,
        name: String,
        lastName: String,
        phoneCode: String,
        phoneNo: String?,
        address: String
    ) {
        showLoading()
        NetworkClass.callApi(URLApi.signup(
            email = email,
            password = password,
            password_confirmation = confirmPassword,
            first_name = name,
            last_name = lastName,
            device_type = "android",
            phone_code = phoneCode,
            phone_number = phoneNo,
            is_social = "0",
            profile_type = type,
            address1 = address
        ), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                showBarToast("User Successfully Created")
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
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

}