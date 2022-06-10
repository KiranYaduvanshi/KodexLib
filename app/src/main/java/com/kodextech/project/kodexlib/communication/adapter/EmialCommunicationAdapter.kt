package com.kodextech.project.kodexlib.communication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.AddressItemBinding
import com.kodextech.project.kodexlib.databinding.LayoutRvItemCommunicationEmailBinding
import com.kodextech.project.kodexlib.ui.main.booking.adapter.AddAddressVH

class EmialCommunicationAdapter(var context:Context) : RecyclerView.Adapter<EmailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_rv_item_communication_email, parent, false)
        return EmailViewHolder(view)

    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
       return  5
    }
}

class EmailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: LayoutRvItemCommunicationEmailBinding? = DataBindingUtil.bind(itemView)
}
