package com.kodextech.project.kodexlib.ui.main.booking.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.AddVanViewBinding
import com.kodextech.project.kodexlib.model.FloorAndFlatModel

class AddVanAdapter(
    var context: BaseActivity,
    var mData: ArrayList<FloorAndFlatModel>
) : RecyclerView.Adapter<AddQuantityVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddQuantityVH {
        val view = LayoutInflater.from(context).inflate(R.layout.add_van_view, parent, false)
        return AddQuantityVH(view)
    }

    override fun onBindViewHolder(holder: AddQuantityVH, position: Int) {
        val mItem = mData[position]
        holder.binding?.tvVansDetail?.text = mItem.floorNumber + " " + mItem.floorName + " Vans"


        holder.binding?.ivClose?.setOnClickListener {
            mData.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeRemoved(position, mData.size)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = mData.size
}

class AddQuantityVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: AddVanViewBinding? = DataBindingUtil.bind(itemView)
}