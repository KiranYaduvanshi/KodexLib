package com.kodextech.project.kodexlib.ui.main.communication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.LayoutRvItemCommunicationEmailBinding
import com.kodextech.project.kodexlib.model.Data

class EmialCommunicationAdapter(var context:Context,
                                var mData: ArrayList<Data>,var  onClickInterface: emailClickInterface) : RecyclerView.Adapter<EmailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_rv_item_communication_email, parent, false)
        return EmailViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmailViewHolder, position: Int) {

        holder.binding?.tvOrder?.text = mData[position].Order.toString()
        holder.binding?.tvWorkerEmail?.text = mData[position].Email
        holder.binding?.tvName?.text = mData[position].Name
        holder.binding?.dateTv?.text = mData[position].Date
        holder.binding?.btnResend?.setOnClickListener {
            onClickInterface.resendEmail(mData[position].id)
        }

        holder.itemView.setOnClickListener {
            onClickInterface.onEmailClick(mData[position].Order.toString(),position)


        }
    }

    override fun getItemCount(): Int {
       return  mData.size
    }
}

class EmailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: LayoutRvItemCommunicationEmailBinding? = DataBindingUtil.bind(itemView)
}

interface  emailClickInterface{
    fun onEmailClick(orderId:String,position: Int){

    }

    fun resendEmail(id: Int?)
}
