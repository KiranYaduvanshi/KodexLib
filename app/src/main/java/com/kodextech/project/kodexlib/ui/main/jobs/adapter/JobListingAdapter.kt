package com.kodextech.project.kodexlib.ui.main.jobs.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.JobItemBinding
import com.kodextech.project.kodexlib.dialog.SelectWorkerDialog
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.Placeholders
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.loadImage
import com.kodextech.project.kodexlib.ui.main.jobs.JobDetail
import com.kodextech.project.kodexlib.ui.main.termsAndServices.TermsServices
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible
import com.kodextech.project.kodexlib.utils.zDateConvertor

class JobListingAdapter(
    var mContext: BaseActivity,
    var mData: ArrayList<JobModel>,
    var callBack: ((item: JobModel, position: Int, invoiceGenerated: Boolean, isFor: String) -> Unit)
) : RecyclerView.Adapter<JobListingVH>() {
    companion object {
        var mDataSelected: ArrayList<JobModel> = ArrayList()
        var isButtonState: Boolean = false
    }

    var longclicked = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobListingVH {
        val view = LayoutInflater.from(mContext).inflate(R.layout.job_item, parent, false)
        return JobListingVH(view)
    }


    override fun onBindViewHolder(holder: JobListingVH, position: Int) {
        val mItem = mData[position]
        checkState(mItem, position, holder)


        if (mItem.has_requested_insurance == "1" || mItem.priority == "high") {
            holder.binding?.cvJob?.setCardBackgroundColor(mContext.getColor(R.color.red))
            holder.binding?.tvCustomerName?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvCustomerAddress?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvDriverTitle?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvDriverName?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvPhoneTitle?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvPhone?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvPriceTitle?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvPrice?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvStartTitle?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvStartTime?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvBookedByTitle?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvEBookedBy?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvType?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvTitleTitle?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.tvBookingNo?.setTextColor(mContext.getColor(R.color.white))

        }else{
            holder.binding?.tvType?.setTextColor(mContext.getColor(R.color.textColor))
            holder.binding?.tvBookingNo?.setTextColor(mContext.getColor(R.color.textColor))
        }

        holder.itemView.setOnLongClickListener {
            if (mDataSelected.count() > 0) {
            } else {
                callBack(mItem, position, true, "0")
                mDataSelected.add(mItem)
            }
            notifyDataSetChanged()
            true
        }



        holder.itemView?.setOnClickListener()
        {
            if (mDataSelected.count() > 0) {
                if (mDataSelected.contains(mItem)) {
                    mDataSelected.remove(mItem)
                } else {
                    mDataSelected.add(mItem)
                }
                notifyDataSetChanged()

            } else {
                val intent = Intent(mContext, TermsServices::class.java)
                intent.putExtra("job_id", mItem.uuid)
                intent.putExtra("isForView", "viewOnly")
                if (mItem.job_status == "completed") {
                    intent.putExtra("isFor", "history")
                } else if (mItem.job_status == "active") {
                    intent.putExtra("isFor", "active")
                } else if (mItem.job_status == "cancelled" || mItem.job_status == "pending" || mItem.job_status == "assigned") {
                    intent.putExtra("isFor", mItem.job_status)
                }
                mContext.startActivity(intent)
            }
        }

        when (mItem.priority) {
            "normal" -> {

                holder.binding?.tvType?.text = "Normal"
            }
            "high" -> {
                holder.binding?.tvType?.text = "Important"
            }
            "low" -> {
                holder.binding?.tvType?.setTextColor(mContext.getColor(R.color.seaGreen))
                holder.binding?.tvType?.text = "Not Important"
            }
        }


        if (mItem.worker_id == null) {
            holder.binding?.tvCancel?.visible()
            checkJobStatus(mItem, holder)
        } else {
            holder.binding?.tvDriverName?.text = mItem.worker?.full_name
            checkJobStatus(mItem, holder)

        }

        if (mItem.invoice?.status == "paid") {
            holder.binding?.tvAlreadyGenerated?.text = "Invoice Has Been Generated (Paid)"
        } else if (mItem.invoice?.status == "not-paid") {
            holder.binding?.tvAlreadyGenerated?.text = "Invoice Has Been Generated (Not Paid)"
        }

        holder.binding?.tvCustomerName?.text = mItem.first_name + " " + mItem.last_name
        holder.binding?.tvCustomerAddress?.text = mItem.pickup_address?.address1

        val number = mItem.price_amount?.toDoubleOrNull() ?: 0.0
        val rounded = String.format("%.2f", number)
        holder.binding?.tvPrice?.text = "£ $rounded"
        holder.binding?.tvPhone?.text = mItem.phone_code + " " + mItem.phone_numb
        holder.binding?.tvStartTime?.text = mItem.start_time?.zDateConvertor(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "MMMM dd, YYYY", mItem.start_time
        )
        holder.binding?.tvEBookedBy?.text = mItem.booker?.full_name



        holder.binding?.ivWorker?.loadImage(
            "", Placeholders.USER, true
        )

        holder.binding?.tvCancel?.setOnClickListener()
        {
            callBack(mItem, position, false, "1")
        }

        holder.binding?.tvBookingNo?.text = "Booking # ${mItem.id}"

        holder.binding?.tvCopy?.setOnClickListener()
        {
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

        holder.binding?.btnAssign?.setOnClickListener()
        {

            if (mItem.worker_id == null) {
                val dialog = SelectWorkerDialog.newInstance()
                val bundle = Bundle()
                bundle.putString("jobId", mItem.uuid)
                dialog.arguments = bundle
                dialog.show(mContext.supportFragmentManager, "")
            } else if (mItem.worker_id != null) {
                if (mItem.job_status?.lowercase() == "assigned".lowercase()) {
                    val intent = Intent(mContext, TermsServices::class.java)
                    intent.putExtra("job_id", mItem.uuid)
                    intent.putExtra("isFor", mItem.job_status)
                    mContext.startActivity(intent)
                } else if (mItem.job_status?.lowercase() == "active".lowercase()) {
                    val intent = Intent(mContext, JobDetail::class.java)
                    intent.putExtra("id", mItem.uuid)
                    intent.putExtra("for", "finishJob")
                    mContext.startActivity(intent)
                }
            }

        }

        holder.binding?.btnGenerateInvoice?.setOnClickListener()
        {
            callBack(mItem, position, true, "1")
        }
        holder.binding?.ivWorker?.loadImage(
            "", Placeholders.USER, true
        )
    }

    private fun checkState(mItem: JobModel, position: Int, holder: JobListingVH) {
        if (mDataSelected.contains(mItem)) {
            holder.binding?.ivSelected?.visible()
        } else {
            holder.binding?.ivSelected?.gone()
        }

        if (mDataSelected.count() == 0) {
            callBack(mItem, position, true, "2")
        } else {
            callBack(mItem, position, true, "0")
        }
    }


    private fun checkJobStatus(mItem: JobModel, holder: JobListingVH) {
        if (mItem.job_status?.lowercase() == "completed".lowercase()) {
            holder.binding?.tvStatus?.visible()
            holder.binding?.tvStatus?.text = "Completed"
            holder.binding?.tvStatus?.setTextColor(mContext.getColor(R.color.green))
            holder.binding?.tvCancel?.gone()
            holder.binding?.tvCopy?.visible()
            holder.binding?.btnAssign?.gone()

            if (mItem.invoice_count == "0") {
                holder.binding?.btnGenerateInvoice?.visible()
                holder.binding?.tvAlreadyGenerated?.gone()

            } else {
                holder.binding?.btnGenerateInvoice?.gone()
                holder.binding?.tvAlreadyGenerated?.visible()

            }

        } else if (mItem.job_status?.lowercase() == "cancelled".lowercase()) {
            holder.binding?.tvStatus?.visible()
            holder.binding?.tvStatus?.text = "Cancelled"
            holder.binding?.tvStatus?.setTextColor(mContext.getColor(R.color.red))
            holder.binding?.tvCancel?.gone()
            holder.binding?.btnGenerateInvoice?.gone()
            holder.binding?.btnAssign?.gone()
            holder.binding?.tvCopy?.gone()
        } else if (mItem.job_status?.lowercase() == "assigned".lowercase()) {
            holder.binding?.tvStatus?.visible()
            holder.binding?.tvStatus?.text = "Driver Assigned"
            holder.binding?.tvStatus?.setTextColor(mContext.getColor(R.color.orange))
            holder.binding?.btnAssign?.text = "Start Job"
            holder.binding?.tvDriverName?.text = mItem.worker?.full_name
            holder.binding?.tvCancel?.gone()
        } else if (mItem.job_status?.lowercase() == "active".lowercase()) {
            holder.binding?.tvStatus?.visible()
            holder.binding?.tvStatus?.text = "Inprogress"
            holder.binding?.tvStatus?.setTextColor(mContext.getColor(R.color.blue))
            holder.binding?.btnAssign?.visible()
            holder.binding?.tvCancel?.gone()
            holder.binding?.btnGenerateInvoice?.gone()
            holder.binding?.tvCopy?.gone()
            holder.binding?.btnAssign?.text = "Finish Job"
        } else if (mItem.job_status?.lowercase() == "pending".lowercase()) {
            if (mItem.worker == null) {
                holder.binding?.btnAssign?.visible()
                holder.binding?.tvCancel?.visible()
                holder.binding?.btnGenerateInvoice?.gone()
                holder.binding?.tvCopy?.gone()
                holder.binding?.tvStatus?.gone()

                holder.binding?.btnAssign?.text = "Assign Job"
            }
        }
    }


    fun calculateHours(mItem: JobModel, holder: JobListingVH) {
        //For Calculate hours ..
        val startTime = mItem.start_time?.zDateConvertor(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "dd-MM-yyyy HH:mm:ss",
            mItem.start_time
        )

    }

    fun filterJobList(filteredList: ArrayList<JobModel>) {
        mData = filteredList
        notifyDataSetChanged()
    }

    override fun getItemCount() = mData.size
}


class JobListingVH(view: View) : RecyclerView.ViewHolder(view) {
    val binding: JobItemBinding? = DataBindingUtil.bind(view)
}
