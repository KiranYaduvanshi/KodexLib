package com.kodextech.project.kodexlib.ui.main.customer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.CustomerTypeItemBinding

class CustomerTypeAdapter(
    var mContext: Context,
    var mData: ArrayList<CustomerTypeModel>,
    var mCallBack: ((position: Int) -> Unit)
) : RecyclerView.Adapter<CustomerTypeVH>() {
    val mDataSelected: ArrayList<CustomerTypeModel> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerTypeVH {
        val view = LayoutInflater.from(mContext).inflate(R.layout.customer_type_item, parent, false)
        return CustomerTypeVH(view)
    }

    override fun onBindViewHolder(holder: CustomerTypeVH, position: Int) {
        val mItem = mData[position]

        holder.binding?.tvClientType?.text = mItem.title

        if (mItem.isClicked == true) {
            holder.binding?.tvClientType?.setTextColor(mContext.getColor(R.color.white))
            holder.binding?.cvMain?.setCardBackgroundColor(mContext.getColor(R.color.blue))
        } else {
            holder.binding?.cvMain?.setCardBackgroundColor(mContext.getColor(R.color.white))
            holder.binding?.tvClientType?.setTextColor(mContext.getColor(R.color.customerCol))
        }


        holder.itemView?.setOnClickListener {
            mData.forEachIndexed { index, customerTypeModel ->
                if(mData[index].isClicked==false){
                    mItem.isClicked = true
                }else{
                    if(index!=position){
                        customerTypeModel.isClicked = false
                    }
                }
            }
            notifyDataSetChanged()
        }


    }

    override fun getItemCount() = mData.size
}

class CustomerTypeVH(view: View) : RecyclerView.ViewHolder(view) {
    val binding: CustomerTypeItemBinding? = DataBindingUtil.bind(view)
}

class CustomerTypeModel(
    var title: String? = "",
    var isClicked: Boolean? = false
)
