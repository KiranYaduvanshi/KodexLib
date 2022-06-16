package com.kodextech.project.kodexlib.ui.main.communication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.LayoutRvItemCommunicationSmsBinding

class SmsCommunicationAdapter(var context:Context):RecyclerView.Adapter<SmsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_rv_item_communication_sms, parent, false)
        return SmsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return  4
    }
}

class SmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: LayoutRvItemCommunicationSmsBinding? = DataBindingUtil.bind(itemView)
}
