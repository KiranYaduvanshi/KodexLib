package com.kodextech.project.kodexlib.ui.main.dashboard.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.DetailCustoemrItemBinding
import com.kodextech.project.kodexlib.model.CustomerModel
import com.kodextech.project.kodexlib.ui.main.customer.CustomerProfile

class CustomerListingAdapter(
    var context: Context,
    var mData: ArrayList<CustomerModel>,
    var callBack: ((position: Int) -> Unit)
) : RecyclerView.Adapter<CustomerListingAVH>() {

    private var customerObj: CustomerModel? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerListingAVH {
        val view =
            LayoutInflater.from(context).inflate(R.layout.detail_custoemr_item, parent, false)
        return CustomerListingAVH(view)
    }

    override fun onBindViewHolder(holder: CustomerListingAVH, position: Int) {
        val mItem = mData[position]

        holder.binding?.customerName?.text = "${mItem?.first_name} ${mItem?.last_name}"
        holder.binding?.tvEmail?.text = mItem.email
        holder.binding?.tvPhone?.text = "${mItem?.phone_code} ${mItem?.phone_numb}"
        holder.binding?.clientAddress?.text = mItem.pickup_address?.address1
        holder.binding?.customerRating?.text = "${mItem.customer_rating.toString()}.0"
        holder.binding?.rating?.rating = mItem.customer_rating?.toFloat()!!


        holder.binding?.ivCustomer?.loadImage(
            "",
            Placeholders.USER, true
        )


        holder.binding?.btnDetail?.setOnClickListener {
            val intent = Intent(context, CustomerProfile::class.java)
            intent.putExtra("customerObj", mItem)
            context.startActivity(intent)
        }

    }

    override fun getItemCount() = mData.size
    fun filterList(filteredList: ArrayList<CustomerModel>){
        mData = filteredList
        notifyDataSetChanged()
    }
}

class CustomerListingAVH(view: View) : RecyclerView.ViewHolder(view) {
    val binding: DetailCustoemrItemBinding? = DataBindingUtil.bind(view)
}



