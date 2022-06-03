package com.kodextech.project.kodexlib.ui.main.dashboard.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.DashboardCustomerItemBinding
import com.kodextech.project.kodexlib.model.User
import com.kodextech.project.kodexlib.network.LocalPreference
import com.kodextech.project.kodexlib.ui.main.customer.CustomerProfile
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible

class CustomerListingDashboard(
    var mContext: Context,
    var mData: ArrayList<User>,
    var callBack: ((position: Int) -> Unit)
) : RecyclerView.Adapter<CustomerListingDashboardVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerListingDashboardVH {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.dashboard_customer_item, parent, false)
        return CustomerListingDashboardVH(view)
    }

    override fun onBindViewHolder(holder: CustomerListingDashboardVH, position: Int) {
        val mItem = mData[position]

        if (position == 0) {
            holder.binding?.view?.gone()
        } else {
            holder.binding?.view?.visible()
        }

        holder.binding?.tvName?.text = mItem.profile?.first_name
        holder.binding?.tvAddress?.text = mItem.profile?.address?.address1

//        holder.binding?.tvAddress?.isSelected = true;
//        holder.binding?.tvAddress?.ellipsize = TextUtils.TruncateAt.MARQUEE;

        holder.binding?.ivCustomer?.loadImage(
            mItem.profile?.profile_image,
            Placeholders.USER,
            true
        )

        holder.itemView?.setOnClickListener {
            if (LocalPreference.shared.user?.user?.profile_type == "customer") {
                val intent = Intent(mContext, CustomerProfile::class.java)
                intent.putExtra("customerUUID", mItem.profile?.uuid)
                mContext.startActivity(intent)
            }

        }

    }

    override fun getItemCount() = mData.size
}

class CustomerListingDashboardVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: DashboardCustomerItemBinding? = DataBindingUtil.bind(itemView)
}

fun ImageView.loadImage(
    link: Any?,
    drawable: Placeholders = Placeholders.USER,
    isForCircle: Boolean = false,
) {
    try {
        if (isForCircle) {
            Glide.with(this)
                .asBitmap()
                .load(link)
                .circleCrop()
                .placeholder(drawable.intValue)//circularProgressDrawable
                .error(drawable.intValue)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(this)
        } else {
            Glide.with(this)
                .load(link)
                .centerCrop()
                .placeholder(drawable.intValue)//circularProgressDrawable
                .error(drawable.intValue)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(this)
        }
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
    }


}


enum class Placeholders(var intValue: Int) {
    USER(R.drawable.ic_place_holder),
    DEFAULT(R.drawable.ic_place_holder),
    ROOM_DEFAULT(R.drawable.ic_place_holder),
    PROFILE_PLACE_HOLDER(R.drawable.ic_place_holder)
}

