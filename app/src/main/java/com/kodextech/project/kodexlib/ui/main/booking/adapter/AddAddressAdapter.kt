package com.kodextech.project.kodexlib.ui.main.booking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.AddressItemBinding
import com.kodextech.project.kodexlib.model.PickupAddress
import com.kodextech.project.kodexlib.utils.gone

class AddAddressAdapter(
    var context: BaseActivity,
    var mData: ArrayList<PickupAddress>,
    var callBack: ((positionL: Int) -> Unit)
) : RecyclerView.Adapter<AddAddressVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddAddressVH {
        val view = LayoutInflater.from(context).inflate(R.layout.address_item, parent, false)
        return AddAddressVH(view)
    }

    override fun onBindViewHolder(holder: AddAddressVH, position: Int) {
        val mItem = mData[position]

        holder.binding?.tvAddress?.text = mItem.address1

        if (mItem.has_lift == "1") {
            holder.binding?.tvLifr?.text = "Yes"

        } else if (mItem.has_lift == "0") {
            holder.binding?.tvLifr?.text = "No"
        } else {
            holder.binding?.llLift?.gone()
        }

        if (mItem?.pickup_flat_meta?.isNotEmpty() == true) {
            when (mItem.pickup_flat_meta?.get(0)?.floor_no) {
                "-1" -> {
                    holder.binding?.tvFloor?.text = "Basement"

                }
                "0" -> {
                    holder.binding?.tvFloor?.text = "Ground Floor"
                }
                "-2" -> {
                    holder.binding?.llFloor?.gone()
                    holder.binding?.tvFloor?.text =""




                }
                else -> {
                    holder.binding?.tvFloor?.text =
                        mItem.pickup_flat_meta?.get(0)?.floor_no + " Floor"
                }
            }
        } else {
            holder.binding?.llFloor?.gone()
        }

        holder.binding?.ivClose?.setOnClickListener {
            callBack(position)
//            mData.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeRemoved(position, mData.size)
//            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}

class AddAddressVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: AddressItemBinding? = DataBindingUtil.bind(itemView)
}
