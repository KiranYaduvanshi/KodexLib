package com.kodextech.project.kodexlib.ui.main.communication

import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.ui.main.communication.adapter.EmialCommunicationAdapter
import com.kodextech.project.kodexlib.ui.main.communication.adapter.SmsCommunicationAdapter
import com.kodextech.project.kodexlib.databinding.ActivityCommunicationBinding
import com.kodextech.project.kodexlib.model.Data
import com.kodextech.project.kodexlib.model.InvoiceModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.utils.generateList
import org.json.JSONArray
import org.json.JSONObject

class CommunicationActivity : BaseActivity() {

    private var binding: ActivityCommunicationBinding? = null
    private var emialCommunicationAdapter: EmialCommunicationAdapter? = null
    private var smsCommunicationAdapter: SmsCommunicationAdapter? = null
    private var mData = ArrayList<Data>()

    override fun onSetupViewGroup() {
        mViewGroup = binding?.rlCommunication

    }

    override fun setupContentViewWithBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_communication)
        binding?.ivBack?.setOnClickListener { onBackPressed() }
        getEmailCommunicationList()

        binding?.tvemail?.setOnClickListener {

            binding?.tvemail?.setBackgroundColor(getColor(R.color.blue))
            binding?.tvemail?.setTextColor(getColor(R.color.white))
            binding?.tvsms?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvsms?.setTextColor(getColor(R.color.cusCol))
            getEmailCommunicationList()
        }
        binding?.tvsms?.setOnClickListener {
            binding?.tvsms?.setBackgroundColor(getColor(R.color.blue))
            binding?.tvsms?.setTextColor(getColor(R.color.white))
            binding?.tvemail?.setBackgroundColor(getColor(R.color.gray))
            binding?.tvemail?.setTextColor(getColor(R.color.cusCol))
            setSmsAdapter()
        }
    }

    override fun onRecycleBeforeDestroy() {
    }

    override fun onBecameInvisibleToUser() {
    }

    override fun onBecameVisibleToUser() {
    }

    override fun setupLoader() {
    }

    fun setEmailAdapter(){
        emialCommunicationAdapter = EmialCommunicationAdapter(this,mData)
        binding?.rvMails?.adapter = emialCommunicationAdapter
    }

    fun setSmsAdapter(){
        smsCommunicationAdapter = SmsCommunicationAdapter(this)
        binding?.rvMails?.adapter = smsCommunicationAdapter
    }

    private fun getEmailCommunicationList() {
  showLoading()
        NetworkClass.callApi(URLApi.getEmailCommunationApi(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                Log.i("Check","response "+response)
                val json = JSONArray(response ?: "")
                val data = generateList(json.toString(), Array<Data>::class.java)
               // Toast.makeText(binding?.root?.context, "data "+data[0].Date, Toast.LENGTH_SHORT).show()
               // Toast.makeText(binding?.root?.context, "response"+response, Toast.LENGTH_SHORT).show()
                mData.addAll(data)
                setEmailAdapter()

            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }
}