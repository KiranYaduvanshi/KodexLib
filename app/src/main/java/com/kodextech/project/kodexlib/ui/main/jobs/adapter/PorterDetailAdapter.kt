package com.kodextech.project.kodexlib.ui.main.jobs.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.WorkerItemBinding
import com.kodextech.project.kodexlib.model.User
import com.kodextech.project.kodexlib.ui.main.worker.adapter.WorkerListingVH

class PorterDetailAdapter(var context: Context, var list:ArrayList<User>) :RecyclerView.Adapter<PorterDetailAdapter.PorterListVH>(){
    class PorterListVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: WorkerItemBinding? = DataBindingUtil.bind(itemView)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PorterListVH {
        val view = LayoutInflater.from(context).inflate(R.layout.worker_item, parent, false)
        return PorterListVH(view)

    }

    override fun onBindViewHolder(holder: PorterListVH, position: Int) {
        val mItem = list[position]

        holder.binding?.tvWorkerName?.text =
            mItem.profile?.first_name + " " + mItem.profile?.last_name
        holder.binding?.tvWorkerRole?.text = mItem.profile?.worker_type
        holder.binding?.tvWorkerEmail?.text = mItem.email
        holder.binding?.tvWorkerHourly?.text = mItem.profile?.price_amount + " /hr"


    }

    override fun getItemCount(): Int {
        return  list.size
    }
}