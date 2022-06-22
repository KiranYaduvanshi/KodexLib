package com.kodextech.project.kodexlib.ui.main.invoice

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityInvoiceListing2Binding
import com.kodextech.project.kodexlib.model.InvoiceModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.invoice.adapter.InvoiceListingAdapter
import com.kodextech.project.kodexlib.utils.generateList
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible
import org.json.JSONObject

class InvoiceListing : BaseActivity() {

    private var binding: ActivityInvoiceListing2Binding? = null
    private var mData = ArrayList<InvoiceModel>()
    private var mAdapter: InvoiceListingAdapter? = null

    private var listingFor: String? = "unpaid"
    private var searchText: String? = ""

    override fun onSetupViewGroup() {
        mViewGroup = binding?.contentInvoice
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoice_listing2)
        initTopBar()
        setInvoiceData()

        
        binding?.svInvoice?.setOnRefreshListener {
            getInvoiceList()
            binding?.etSearch?.visibility = View.GONE
            binding?.btnClose?.visibility = View.GONE
            binding?.searchBtn?.visibility = View.VISIBLE
            binding?.etSearch?.setText("")
        }

        binding?.topBar?.ivBack?.setOnClickListener { onBackPressed() }
        binding?.btnUnPaidInvoice?.setOnClickListener {
            binding?.viewInprogress?.visibility = View.VISIBLE
            binding?.viewCompleted?.visibility = View.INVISIBLE
            listingFor = "unpaid"
            getInvoiceList()
        }

        binding?.btnPaidInvoices?.setOnClickListener {
            binding?.viewInprogress?.visibility = View.INVISIBLE
            binding?.viewCompleted?.visibility = View.VISIBLE
            listingFor = "paid"
            getInvoiceList()
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

        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                searchText = p0.toString()
                filter(p0.toString())
            }
        })
    }

    private fun filter(text: String) {
        var filterlist: ArrayList<InvoiceModel> = ArrayList()
        for (item in mData) {
            if (item.job?.first_name?.lowercase()?.contains(text.lowercase()) == true
                || item.job?.last_name?.lowercase()?.contains(text.lowercase()) == true
            ) {
                filterlist.add(item)
            }
        }
        mAdapter?.filterList(filterlist)
    }

    private fun setInvoiceData() {
        checkingData()
        mAdapter = InvoiceListingAdapter(this, mData) { item: InvoiceModel ->
            val intent = Intent(this@InvoiceListing, MarkInvoicePaid::class.java)
            intent.putExtra("job_id", item.job?.uuid ?: "")
            intent.putExtra("invoiceId", item?.uuid ?: "")
            startActivity(intent)

        }
        binding?.rvInvoices?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding?.rvInvoices?.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
    }

    private fun markJobPaid(item: InvoiceModel?) {
        showLoading()
        NetworkClass.callApi(
            URLApi.updateInvoicesStatus(
                invoice_uuid = item?.uuid,
                status = "paid"
            ), object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    showToast("Job Mark As Paid Successfully")
                    getInvoiceList()
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showToast(error ?: "")
                }

            })
    }

    private fun checkingData() {
        if (mData.size == 0) {
            binding?.rlNoData?.visible()
            binding?.rvInvoices?.gone()
        } else {
            binding?.rlNoData?.gone()
            binding?.rvInvoices?.visible()
        }
    }

    private fun getInvoiceList() {
//        if (binding?.svInvoice?.isRefreshing == false) {
//            binding?.svInvoice?.isRefreshing = true
//        }
        showLoading()
        NetworkClass.callApi(URLApi.getInvoicesList(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                mData.clear()
                val json = JSONObject(response ?: "")
                val jobModels = json.optJSONArray("models")
                val data = generateList(jobModels.toString(), Array<InvoiceModel>::class.java)

                if (listingFor == "paid") {
                    val filterData = data.filter {
                        it.status?.lowercase() == "paid".lowercase()
                    }
                    mData.addAll(filterData)
                } else {
                    val filterData = data.filter {
                        it.status?.lowercase() == "not-paid".lowercase()

                    }
                    mData.addAll(filterData)
                }

                setInvoiceData()
                binding?.svInvoice?.isRefreshing = false
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
                binding?.svInvoice?.isRefreshing = false
            }

        })
    }

    private fun initTopBar() {
        binding?.topBar?.ivLogout?.gone()
        binding?.topBar?.tvText?.text = "Invoices"
    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {
        getInvoiceList()
    }

    override fun setupLoader() {

    }

}