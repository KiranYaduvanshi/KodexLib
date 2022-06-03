package com.kodextech.project.kodexlib.ui.main.calendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.HistoryDateHeaderBinding
import com.kodextech.project.kodexlib.databinding.InvoiceItemBinding
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.ui.main.calendar.adapter.CalendarJobListing.Constants.TYPE_CELL
import com.kodextech.project.kodexlib.ui.main.calendar.adapter.CalendarJobListing.Constants.TYPE_HEADER

class CalendarJobListing(
    var mContext: BaseActivity,
    var mData: ArrayList<JobModel>,
) : RecyclerView.Adapter<CalendarJobListingVH<*>>() {

    object Constants {
        const val TYPE_HEADER = 0
        const val TYPE_CELL = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarJobListingVH<*> {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.history_date_header, parent, false)
                return HeaderViewHolder(view)
            }
            TYPE_CELL -> {
                val view =
                    LayoutInflater.from(mContext).inflate(R.layout.invoice_item, parent, false)
                return ItemViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")

        }
    }

    override fun onBindViewHolder(holder: CalendarJobListingVH<*>, position: Int) {
        val mItem = mData[position]

        when (mItem.cellType) {
            TypeJobs.HEADER -> {
                if (holder is HeaderViewHolder) {
                    holder.bind(mItem, mContext as AppCompatActivity)

                }
            }
            TypeJobs.CELL -> {
                if (holder is ItemViewHolder) {
                    holder.bind(mItem, mContext as AppCompatActivity)


                }
            }

        }

    }

    override fun getItemViewType(position: Int): Int {
        if (mData[position].cellType == TypeJobs.HEADER) {
            return TYPE_HEADER
        } else if (mData[position].cellType == TypeJobs.CELL) {
            return TYPE_CELL
        } else {
            return TYPE_CELL//throw IllegalArgumentException("Invalid type of data")
        }
    }

    override fun getItemCount() = mData.size
}

class HeaderViewHolder(itemView: View) : CalendarJobListingVH<JobModel>(itemView) {
    val binding: HistoryDateHeaderBinding? = DataBindingUtil.bind(itemView)
    override fun bind(item: JobModel, context: AppCompatActivity) {

        binding?.tvDateHeader?.text = item.separaterDate

    }

}

class ItemViewHolder(itemView: View) : CalendarJobListingVH<JobModel>(itemView) {
    val binding: InvoiceItemBinding? = DataBindingUtil.bind(itemView)
    override fun bind(item: JobModel, context: AppCompatActivity) {
//        binding?.tvCustomerName?.text = item.customer?.full_name
//        binding?.tvCustomerAddress?.text = item.drop_address?.address1

    }
}

abstract class CalendarJobListingVH<T>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: T, context: AppCompatActivity)
}

enum class TypeJobs {
    HEADER, CELL
}
