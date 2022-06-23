package com.kodextech.project.kodexlib.ui.main.communication

import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityViewEmailBinding
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.model.ViewEmialModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi

import org.json.JSONObject

class ViewEmailActivity : BaseActivity() {
    private var binding: ActivityViewEmailBinding? = null
    private  var orderId=""


    override fun onSetupViewGroup() {
        mViewGroup = binding?.llWebView


    }

    override fun setupContentViewWithBinding() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_email)
        binding?.ivBack?.setOnClickListener {
            onBackPressed()
        }
        binding?.webView?.settings?.javaScriptEnabled =true
        binding?.webView?.settings?.builtInZoomControls =true

        binding?.webView?.settings?.javaScriptCanOpenWindowsAutomatically =true
        binding?.webView?.settings?.loadWithOverviewMode =true

        orderId = intent.getStringExtra("orderId").toString()
        getViewEmailApi()
    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {
    }

    override fun setupLoader() {
    }

    fun getViewEmailApi() {

        showLoading()
        NetworkClass.callApi(URLApi.viewEmail(orderId), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                Log.i("Check", "response " + response)
                binding?.webView?.loadUrl("${response}")


                //Toast.makeText(binding?.root?.context, "response"+response, Toast.LENGTH_SHORT).show()
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }
        })
    }

}