package com.kodextech.project.kodexlib.ui.main.communication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.LayoutRvItemCommunicationSmsBinding
import com.kodextech.project.kodexlib.model.Data
import com.kodextech.project.kodexlib.model.SmsCommunicationModel

class SmsCommunicationAdapter(var context:Context,var smsData: ArrayList<SmsCommunicationModel>):RecyclerView.Adapter<SmsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_rv_item_communication_sms, parent, false)
        return SmsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        holder.binding?.tvOrder?.text = smsData[position].Order
        holder.binding?.tvWorkerPhone?.text = smsData[position].phone
        holder.binding?.tvName?.text = smsData[position].Name
        holder.binding?.tvData?.text = smsData[position].Date
        Toast.makeText(context, ""+smsData[position].phone, Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        return  smsData.size
    }
}

class SmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: LayoutRvItemCommunicationSmsBinding? = DataBindingUtil.bind(itemView)
}
