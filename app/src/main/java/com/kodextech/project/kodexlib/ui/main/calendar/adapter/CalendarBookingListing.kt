package com.kodextech.project.kodexlib.ui.main.calendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.BookingItemBinding
import com.kodextech.project.kodexlib.databinding.HistoryDateHeaderBinding
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.ui.main.calendar.adapter.CalendarBookingListing.Constants.TYPE_CELL
import com.kodextech.project.kodexlib.ui.main.calendar.adapter.CalendarBookingListing.Constants.TYPE_HEADER
import com.kodextech.project.kodexlib.utils.zDateConvertor

class CalendarBookingListing(
    var mContext: BaseActivity,
    var mData: ArrayList<JobModel>,
) : RecyclerView.Adapter<CalendarBookingListingVH<*>>() {

    object Constants {
        const val TYPE_HEADER = 0
        const val TYPE_CELL = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarBookingListingVH<*> {
        return when (viewType) {
            TYPE_HEADER -> {
                val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.history_date_header, parent, false)
                return HeaderBookingViewHolder(view)
            }
            TYPE_CELL -> {
                val view =
                    LayoutInflater.from(mContext).inflate(R.layout.booking_item, parent, false)
                return ItemBookingViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")

        }
    }

    override fun onBindViewHolder(holder: CalendarBookingListingVH<*>, position: Int) {
        val mItem = mData[position]

        when (mItem.cellType) {
            TypeJobs.HEADER -> {
                if (holder is HeaderBookingViewHolder) {
                    holder.bind(mItem, mContext as AppCompatActivity)

                }
            }
            TypeJobs.CELL -> {
                if (holder is ItemBookingViewHolder) {
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

class HeaderBookingViewHolder(itemView: View) : CalendarBookingListingVH<JobModel>(itemView) {

    val binding: HistoryDateHeaderBinding? = DataBindingUtil.bind(itemView)
    override fun bind(item: JobModel, context: AppCompatActivity) {
        binding?.tvDateHeader?.text = item.separaterDate

    }


}

class ItemBookingViewHolder(itemView: View) : CalendarBookingListingVH<JobModel>(itemView) {
    val binding: BookingItemBinding? = DataBindingUtil.bind(itemView)
    override fun bind(item: JobModel, context: AppCompatActivity) {
        binding?.clientName?.text = item?.first_name + " " + item?.last_name
        binding?.clientAddress?.text = item.pickup_address?.address1.toString()
        binding?.tvDate?.text = item.start_time?.zDateConvertor(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "dd/MM/YYYY",
            item.start_time
        )
//        binding?.tvPrice?.text = "$ " + item.price_total
//        binding?.tvStatus?.text = item.booking_status
//
//        if (item.booking_status.equals("placed")) {
//            binding?.tvStatus?.setTextColor(context.getColor(R.color.red))
//            binding?.tvStatus?.text = "Pending"
//        } else if (item.booking_status.equals("completed")) {
//            binding?.tvStatus?.setTextColor(context.getColor(R.color.green))
//            binding?.tvStatus?.text = "Completed"
//        } else if (item.booking_status.equals("aborted")) {
//            binding?.tvStatus?.setTextColor(context.getColor(R.color.red))
//            binding?.tvStatus?.text = "Cancelled"
//        }
//
//        binding?.ivCustomer?.loadImage(
//            item.booker?.profile_image,
//            Placeholders.USER,
//            true
//        )


    }

}

abstract class CalendarBookingListingVH<T>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: T, context: AppCompatActivity)
}

enum class TypeBooking {
    HEADER, CELL
}
