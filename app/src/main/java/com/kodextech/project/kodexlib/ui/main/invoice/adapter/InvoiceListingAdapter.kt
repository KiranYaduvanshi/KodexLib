package com.kodextech.project.kodexlib.ui.main.invoice.adapter

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.NewInvoiceItemBinding
import com.kodextech.project.kodexlib.model.InvoiceModel
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.Placeholders
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.loadImage
import com.kodextech.project.kodexlib.ui.main.termsAndServices.TermsServices
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible
import com.kodextech.project.kodexlib.utils.zDateConvertor
import java.util.*


class InvoiceListingAdapter(
    var mContext: BaseActivity,
    var mData: ArrayList<InvoiceModel>,
    var callBack: ((item: InvoiceModel) -> Unit)

) : RecyclerView.Adapter<InvoiceVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceVH {
        val view = LayoutInflater.from(mContext).inflate(R.layout.new_invoice_item, parent, false)
        return InvoiceVH(view)
    }

    override fun onBindViewHolder(holder: InvoiceVH, position: Int) {
        val mItem = mData[position]

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, TermsServices::class.java)
            intent.putExtra("job_id", mItem.job?.uuid)
            intent.putExtra("isForView", "viewOnly")
            intent.putExtra("isFor", "history")
            mContext.startActivity(intent)
        }

        holder.binding?.tvCustomerName?.text = "${mItem.job?.first_name} ${mItem.job?.last_name}"
        holder.binding?.tvCustomerAddress?.text = "${mItem.job?.pickup_address?.address1}"
        holder.binding?.tvWorkerName?.text = "${mItem.job?.worker?.full_name}"
        holder.binding?.tvBookedBy?.text = mItem.job?.booker?.full_name
        holder.binding?.tvPhone?.text = "${mItem.job?.phone_code} ${mItem.job?.phone_numb}"
        holder.binding?.tvDate?.text = mItem.job?.start_time?.zDateConvertor(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MMMM dd, YYYY", mItem.job.start_time
        )

        when (mItem.job?.priority) {
            "normal" -> {
                holder.binding?.tvPriority?.setTextColor(mContext.getColor(R.color.orange))
                holder.binding?.tvPriority?.text = "Normal"
            }
            "high" -> {
                holder.binding?.tvPriority?.setTextColor(mContext.getColor(R.color.red))
                holder.binding?.tvPriority?.text = "Important"
            }
            "low" -> {
                holder.binding?.tvPriority?.setTextColor(mContext.getColor(R.color.seaGreen))
                holder.binding?.tvPriority?.text = "Not Important"
            }
        }

        holder.binding?.ivWorker?.loadImage("", Placeholders.USER, true)

        if (mItem.status?.lowercase() == "paid".lowercase()) {
            holder.binding?.llPaid?.gone()
            holder.binding?.btnDownload?.visible()
            holder.binding?.tvStatus?.text = "Paid"

            val number = mItem.job?.charged_amount?.toDoubleOrNull() ?: 0.0
            val rounded = String.format("%.2f", number)
            holder.binding?.tvBalanceDue?.text = "£ $rounded"
            holder.binding?.tvLabelBalanceDue?.text = "Charged Amount"
        } else if (mItem.status?.lowercase() == "not-paid".lowercase()) {
            holder.binding?.llPaid?.visible()
            holder.binding?.btnDownload?.gone()
            holder.binding?.tvStatus?.text = "Not Paid"
            var totalPrice: Double? = mItem.price_amount?.toDoubleOrNull()
            var advance: Double? = mItem.job?.advance_amount?.toDoubleOrNull()
            var balanceDue: Double? = null
            if (advance ?: 0.0 > totalPrice ?: 0.0) {
                balanceDue = 0.0
            } else {
                balanceDue = totalPrice?.minus(advance ?: 0.0)
            }
            holder.binding?.tvBalanceDue?.text = "£ $balanceDue"
        } else {

        }
        val number = mItem.price_amount?.toDoubleOrNull() ?: 0.0
        val rounded = String.format("%.2f", number)
        holder.binding?.tvPrice?.text = "£ $rounded"

        val numberAdvance = mItem.job?.advance_amount?.toDoubleOrNull() ?: 0.0
        val roundedAdvance = String.format("%.2f", numberAdvance)
        holder.binding?.tvAdvance?.text = "£ $roundedAdvance"

        holder.binding?.cbPaid?.setOnClickListener {
            callBack(mItem)
        }

        holder.binding?.btnDownload?.setOnClickListener {
            Toast.makeText(mContext, "Downloading file.....", Toast.LENGTH_SHORT).show()
            Log.i("PDF PATH", "" + mItem.invoice_file?.path)
            dowloadPdf(mItem.invoice_file?.path)
        }
    }

    private fun dowloadPdf(path: String?) {

//        val basePath = "http://45.33.19.125/modern-movers/public/uploads/"
        val basePath = "http://13.58.42.125/public/uploads/"

        val invoicePath = basePath + path
        val uri: Uri =
            Uri.parse(invoicePath)

// Create request for android download manager

        val downloadManager: DownloadManager =
            mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request: DownloadManager.Request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or
                    DownloadManager.Request.NETWORK_MOBILE
        )
// set title and description
        request.setTitle("Invoice Downloaded")
        request.setDescription("Invoice Downloaded Successfully")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

//set the local destination for download file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "invoice.pdf"
        )
        request.setMimeType("*/*")
        downloadManager?.enqueue(request)

    }


    override fun getItemCount() = mData.size

    fun filterList(filteredList: ArrayList<InvoiceModel>) {
        mData = filteredList
        notifyDataSetChanged()
    }
}

class InvoiceVH(view: View) : RecyclerView.ViewHolder(view) {
    val binding: NewInvoiceItemBinding? = DataBindingUtil.bind(view)
}
