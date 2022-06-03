package com.kodextech.project.kodexlib.ui.main.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.DashboardItemBinding

class DashboardItemAdapter(
    var mContext: Context,
    var mData: ArrayList<DashboardItemModel>,
    var callBack: ((position: Int) -> Unit)
) : RecyclerView.Adapter<DashboardItemVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardItemVH {
        val view = LayoutInflater.from(mContext).inflate(R.layout.dashboard_item, parent, false)
        return DashboardItemVH(view)
    }

    override fun onBindViewHolder(holder: DashboardItemVH, position: Int) {
        val mItem = mData[position]

        holder.itemView.setOnClickListener {
            callBack(position)
        }

        holder.binding?.tvTitle?.text = mItem.title
        holder.binding?.ivCard?.setImageResource(mItem.image)

    }

    override fun getItemCount() = mData.size
}

class DashboardItemVH(view: View) : RecyclerView.ViewHolder(view) {
    val binding: DashboardItemBinding? = DataBindingUtil.bind(view)
}

class DashboardItemModel(var title: String, var image: Int)