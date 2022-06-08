package com.kodextech.project.kodexlib.ui.main.customer

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityCustomerListingBinding
import com.kodextech.project.kodexlib.dialog.GenerateEmailDialog
import com.kodextech.project.kodexlib.model.CustomerModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.customer.adapter.CustomerTypeAdapter
import com.kodextech.project.kodexlib.ui.main.customer.adapter.CustomerTypeModel
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.CustomerListingAdapter
import com.kodextech.project.kodexlib.utils.generateList
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible
import org.json.JSONObject
import java.util.*


class CustomerListing : BaseActivity() {

    private var binding: ActivityCustomerListingBinding? = null
    private var mAdapter: CustomerListingAdapter? = null
    private var mData = ArrayList<CustomerModel>()

    private var mCustomerType = ArrayList<CustomerTypeModel>()
    private var mCustomerTypeAdapter: CustomerTypeAdapter? = null

    private var customerTypePosition: Int? = 0

    override fun onSetupViewGroup() {
        mViewGroup = binding?.contentCustomer

    }


    override fun setupContentViewWithBinding() {
        statusBarColor(this.getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer_listing)

        initTopBar()

        setCustomerTypeData()
        binding?.topBar?.ivBack?.setOnClickListener { onBackPressed() }


        binding?.btnGenerateEmail?.setOnClickListener {
            val dialog = GenerateEmailDialog.newInstance()
            dialog.show(supportFragmentManager, "")
        }

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
        mAdapter?.filterList(filterlist)
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


    private fun initTopBar() {
        binding?.topBar?.tvText?.text = "Customer"
        binding?.topBar?.ivLogout?.gone()

        binding?.spFilter?.setItems(
            "All Customer",
            "House Move Customer",
            "Event Move Customer",
            "Office Move Customer",
            "Flat Move Customer"
        )


    }

    private fun setData() {

        if (mData.count() == 0) {
            binding?.rlNoData?.visible()
            binding?.rvCustomer?.gone()
        } else {
            binding?.rlNoData?.gone()
            binding?.rvCustomer?.visible()
        }

        val sortedList = mData.sortedBy {
            it.first_name
        }

        mAdapter = CustomerListingAdapter(this, ArrayList(sortedList)) { position -> }
        binding?.rvCustomer?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding?.rvCustomer?.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()

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

}