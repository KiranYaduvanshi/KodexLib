package com.kodextech.project.kodexlib.ui.main.booking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.DashboardCustomerItemBinding
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.Placeholders
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.loadImage

class DashboardBookingAdapter(
    var mContext: BaseActivity,
    var mData: ArrayList<JobModel>,
    var callBack: ((positionL: Int) -> Unit)
) : RecyclerView.Adapter<DashboardBookingVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardBookingVH {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.dashboard_customer_item, parent, false)
        return DashboardBookingVH(view)
    }

    override fun onBindViewHolder(holder: DashboardBookingVH, position: Int) {
        val mItem = mData[position]

        holder.binding?.tvAddress?.text = mItem.pickup_address?.address1
        holder.binding?.tvName?.text = mItem.first_name + " " + mItem.last_name

        holder.binding?.ivCustomer?.loadImage("", Placeholders.USER, true)
    }

    override fun getItemCount() = mData.size

}

class DummyBooking(
    val name: String? = null,
    val address: String? = null,
)

class DashboardBookingVH(view: View) : RecyclerView.ViewHolder(view) {
    val binding: DashboardCustomerItemBinding? = DataBindingUtil.bind(view)
}

