package com.kodextech.project.kodexlib.ui.main.quatation

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.MultipleSelectionActivity
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityCustomerListingBinding
import com.kodextech.project.kodexlib.databinding.ActivityQuatationBinding
import com.kodextech.project.kodexlib.model.CustomerModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.customer.adapter.CustomerTypeAdapter
import com.kodextech.project.kodexlib.ui.main.customer.adapter.CustomerTypeModel
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.CustomerListingAdapter
import com.kodextech.project.kodexlib.ui.main.jobs.adapter.JobListingAdapter
import com.kodextech.project.kodexlib.utils.generateList
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible
import org.json.JSONObject
import java.util.ArrayList

class QuatationActivity : BaseActivity() {

    private var binding: ActivityQuatationBinding? = null
    private var quatationAdapter:QuatationAdapter? = null

    private var mData = ArrayList<CustomerModel>()


    private var mCustomerType = ArrayList<CustomerTypeModel>()
    private var mCustomerTypeAdapter: CustomerTypeAdapter? = null


    private var customerTypePosition: Int? = 0



    override fun onSetupViewGroup() {
        mViewGroup = binding?.qutationCustomer
    }

    override fun setupContentViewWithBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_quatation)
        initTopBar()
        setCustomerTypeData()


        binding?.ivBack?.setOnClickListener {
            onBackPressed()
        }

        binding?.btnSentQuotation?.setOnClickListener {
          //  MultipleSelection not needed
//            Log.i("Quotation ", QuatationAdapter.list.toString() )
//            QuatationAdapter.list.clear()
//            setAdapter(mData)
            sendQuotatuon();

        }
        //setAdapter()


        binding?.searchBtn?.setOnClickListener {
            binding?.etSearch?.visibility = View.VISIBLE
            binding?.btnClose?.visibility = View.VISIBLE
            binding?.searchBtn?.visibility = View.GONE
        }

        binding?.btnClose?.setOnClickListener {
            binding?.etSearch?.visibility = View.GONE
            binding?.btnClose?.visibility = View.GONE
            binding?.searchBtn?.visibility = View.VISIBLE
            binding?.etSearch?.setText("")
        }

        binding?.spFilter?.setOnItemSelectedListener { view, position, id, item ->

            binding?.etSearch?.visibility = View.GONE
            binding?.btnClose?.visibility = View.GONE
            binding?.searchBtn?.visibility = View.VISIBLE
            binding?.etSearch?.setText("")

            when (position) {
                0 -> {
                    customerTypePosition = 0
                    getAllCustomer()
                }
                1 -> {
                    customerTypePosition = 1
                    getAllCustomer()
                }
                2 -> {
                    customerTypePosition = 2
                    getAllCustomer()
                }
                3 -> {
                    customerTypePosition = 3
                    getAllCustomer()
                }
                4 -> {
                    customerTypePosition = 4
                    getAllCustomer()
                }
            }
        }

        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                filter(p0.toString())
            }

        })


    }

    private fun filter(text: String) {
        var filterlist: ArrayList<CustomerModel> = ArrayList()
        for (item in mData) {
            if (item.first_name?.lowercase()?.contains(text.lowercase()) == true
                || item.last_name?.lowercase()?.contains(text.lowercase()) == true
            ) {
                filterlist.add(item)
            }


        }
        quatationAdapter?.filterList(filterlist)
    }


    override fun onRecycleBeforeDestroy() {
    }

    override fun onBecameInvisibleToUser() {
    }

    override fun onBecameVisibleToUser() {
        getAllCustomer()
    }

    override fun setupLoader() {
    }

    private fun initTopBar() {

        binding?.spFilter?.setItems(
            "All Customer",
            "House Move Customer",
            "Event Move Customer",
            "Office Move Customer",
            "Flat Move Customer"
        )


    }

    fun setAdapter(list: ArrayList<CustomerModel>){

        quatationAdapter = QuatationAdapter(this, ArrayList(list)) { position -> }
        binding?.rvQuatation?.adapter = quatationAdapter



    }

    private fun setCustomerTypeData() {
        mCustomerType.add(CustomerTypeModel("All", true))
        mCustomerType.add(CustomerTypeModel("House Move \nCustomer", false))
        mCustomerType.add(CustomerTypeModel("Event Move \nCustomer", false))
        mCustomerType.add(CustomerTypeModel("Office Move \nCustomer", false))
        mCustomerType.add(CustomerTypeModel("Flat Move \nCustomer", false))

        mCustomerTypeAdapter = CustomerTypeAdapter(this, mCustomerType) { position ->
            customerTypePosition = position
            getAllCustomer()
        }
        binding?.rvCustomerType?.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        binding?.rvCustomerType?.adapter = mCustomerTypeAdapter
        mCustomerTypeAdapter?.notifyDataSetChanged()
    }

    private fun getAllCustomer() {
        showLoading()
        NetworkClass.callApi(URLApi.getCustomerList(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                mData.clear()
                val json = JSONObject(response ?: "")
                val json2 = json.optJSONArray("models")
                var data = generateList(json2.toString(), Array<CustomerModel>::class.java)
                mData.addAll(data)
                when (customerTypePosition) {
                    0 -> {
                        mData.clear()
                        mData.addAll(data)
                    }
                    1 -> {

                        val data = mData.filter { it.service == "House Move" }
                        mData.clear()
                        mData.addAll(data)
                    }
                    2 -> {
                        val data = mData.filter { it.service == "Event Move" }
                        mData.clear()
                        mData.addAll(data)
                    }
                    3 -> {
                        val data = mData.filter { it.service == "Office Move" }
                        mData.clear()
                        mData.addAll(data)
                    }
                    4 -> {
                        val data = mData.filter { it.service == "Flat Move" }
                        mData.clear()
                        mData.addAll(data)
                    }
                }


                setData()
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }
    private fun setData() {

        if (mData.count() == 0) {
            binding?.rlNoData?.visible()
            binding?.rvQuatation?.gone()
        } else {
            binding?.rlNoData?.gone()
            binding?.rvQuatation?.visible()
        }

        val sortedList = mData.sortedBy {
            it.first_name
        }
        setAdapter(ArrayList(sortedList))

        quatationAdapter?.notifyDataSetChanged()

    }

//
//    private fun sendQuotatuon() {
//        var list: ArrayList<String> = ArrayList();
//        val jobIds = QuatationAdapter.list.map { it.email }.joinToString(",")
//        val newIds = JobListingAdapter.mDataSelected
//        showLoading()
////        JobListingAdapter.mDataSelected.map { list.add(it.id!!) }
////        Toast.makeText(binding?.root?.context, list.toString(), Toast.LENGTH_SHORT).show()
//        NetworkClass.callApi(URLApi.sendToMultipleUser(emails = list), object : Response {
//            override fun onSuccessResponse(response: String?, message: String) {
//                //showToast("Tester" +response.toString())
//                val uri: Uri =
//                    Uri.parse(response)
//                val downloadManager: DownloadManager =
//                    baseContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//
//                val request: DownloadManager.Request = DownloadManager.Request(uri)
//                request.setAllowedNetworkTypes(
//                    DownloadManager.Request.NETWORK_WIFI or
//                            DownloadManager.Request.NETWORK_MOBILE
//                )
//// set title and description
//                request.setTitle("CSV Downloaded")
//                request.setDescription("CSV Downloaded Successfully")
//
//                request.allowScanningByMediaScanner()
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//
////set the local destination for download file to a path within the application's external files directory
//                request.setDestinationInExternalPublicDir(
//                    Environment.DIRECTORY_DOWNLOADS,
//                    "CDV.pdf"
//                )
//                request.setMimeType("*/*")
//                downloadManager?.enqueue(request)
//            }
//
//            override fun onErrorResponse(error: String?, response: String?) {
//                hideLoading()
//                showToast(error ?: "")
//            }
//        })
//    }

    private fun sendQuotatuon() {
        showLoading()
//        JobListingAdapter.mDataSelected.map { list.add(it.id!!) }
//        Toast.makeText(binding?.root?.context, list.toString(), Toast.LENGTH_SHORT).show()
        NetworkClass.callApi(URLApi.sendEmailToAll(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                showBarToast(message)
                 Toast.makeText(binding?.root?.context, "response"+response, Toast.LENGTH_SHORT).show()
                Log.i("Res", "reposne " + response)

            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showToast(error ?: "")
            }
        })
    }




}