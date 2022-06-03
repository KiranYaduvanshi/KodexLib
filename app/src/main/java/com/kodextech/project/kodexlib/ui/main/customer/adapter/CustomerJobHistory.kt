package com.kodextech.project.kodexlib.ui.main.customer.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.JobHistoryBinding
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.Placeholders
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.loadImage
import com.kodextech.project.kodexlib.ui.main.termsAndServices.TermsServices
import com.kodextech.project.kodexlib.utils.zDateConvertor

class CustomerJobHistory(
    var mContext: BaseActivity,
    var mData: ArrayList<JobModel>,
    var callBack: ((item: JobModel, position: Int) -> Unit)
) : RecyclerView.Adapter<CustomerJobHistoryVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerJobHistoryVH {
        val view = LayoutInflater.from(mContext).inflate(R.layout.job_history, parent, false)
        return CustomerJobHistoryVH(view)
    }


    override fun onBindViewHolder(holder: CustomerJobHistoryVH, position: Int) {
        val mItem = mData[position]


        if (mItem.job_status?.lowercase() == "completed".lowercase()) {
            holder.binding?.tvStatus?.text = "Completed"
            holder.binding?.tvStatus?.setTextColor(mContext.getColor(R.color.green))
        } else if (mItem.job_status?.lowercase() == "cencelled".lowercase()) {
            holder.binding?.tvStatus?.text = "Cancelled"
            holder.binding?.tvStatus?.setTextColor(mContext.getColor(R.color.red))
        }

        when (mItem.priority) {
            "normal" -> {
                holder.binding?.tvType?.setTextColor(mContext.getColor(R.color.orange))
                holder.binding?.tvType?.text = "Normal"
            }
            "high" -> {
                holder.binding?.tvType?.setTextColor(mContext.getColor(R.color.red))
                holder.binding?.tvType?.text = "Important"
            }
            "low" -> {
                holder.binding?.tvType?.setTextColor(mContext.getColor(R.color.seaGreen))
                holder.binding?.tvType?.text = "Not Important"
            }
        }



        holder.binding?.tvCustomerName?.text = mItem.first_name + " " + mItem.last_name
        holder.binding?.tvDriverName?.text = mItem.worker?.full_name

        if(mItem.invoice?.status=="paid".lowercase()){
            holder.binding?.tvPaidStatus?.text = "Paid"
        }else{
            holder.binding?.tvPaidStatus?.text = "Not Paid"
        }

        holder.binding?.tvCustomerAddress?.text = mItem.pickup_address?.address1

        val number = mItem.price_amount?.toDoubleOrNull() ?: 0.0
        val rounded = String.format("%.2f", number)
        holder.binding?.tvPrice?.text = "£ $rounded"
        holder.binding?.tvPhone?.text = mItem.phone_code + " " + mItem.phone_numb
        holder.binding?.tvStartTime?.text = mItem.start_time?.zDateConvertor(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MMMM dd, YYYY", mItem.start_time
        )
        holder.binding?.tvEBookedBy?.text = mItem.booker?.full_name

        holder.binding?.tvCopy?.setOnClickListener {
            val textToCopy = "Your Job Information \n" +
                    "Job ID: ${mItem.uuid} \n" +
                    "Booked By: ${mItem.booker?.full_name} \n" +
                    "Customer Name: ${mItem.first_name} ${mItem.last_name} \n" +
                    "Customer Address: ${mItem.pickup_address?.address1} \n" +
                    "Customer Phone: ${mItem.phone_code} ${mItem.phone_numb} \n" +
                    "Date : ${
                        mItem.start_time?.zDateConvertor(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MMMM dd, YYYY", mItem.start_time
                        )
                    } \n" +
                    "Status: Completed"

            val clipboardManager =
                mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            mContext.showToast("Detail Copied to Clipboard")
        }

        holder.binding?.ivWorker?.loadImage(
            "", Placeholders.USER, true
        )

        holder.binding?.btnDetail?.setOnClickListener {
            val intent = Intent(mContext, TermsServices::class.java)
            intent.putExtra("job_id", mItem.uuid)
            intent.putExtra("isFor", "history")
            mContext.startActivity(intent)
        }

    }


    override fun getItemCount() = mData.size
}


class CustomerJobHistoryVH(view: View) : RecyclerView.ViewHolder(view) {
    val binding: JobHistoryBinding? = DataBindingUtil.bind(view)
}
