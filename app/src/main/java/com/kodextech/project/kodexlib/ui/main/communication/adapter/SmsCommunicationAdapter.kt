package com.kodextech.project.kodexlib.ui.main.communication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.LayoutRvItemCommunicationSmsBinding
import com.kodextech.project.kodexlib.model.SmsCommunicationModel

class SmsCommunicationAdapter(var context:Context,var smsData: ArrayList<SmsCommunicationModel>,var viewSmsSelect: viewSmsSelect):RecyclerView.Adapter<SmsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_rv_item_communication_sms, parent, false)
        return SmsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        holder.binding?.tvOrder?.text = smsData[position].Order
        holder.binding?.tvWorkerPhone?.text = smsData[position].phone
        holder.binding?.tvName?.text = smsData[position].Name
        holder.binding?.tvData?.text = smsData[position].Date
       // Toast.makeText(context, ""+smsData[position].phone, Toast.LENGTH_SHORT).show()

        holder.itemView.setOnClickListener {
            viewSmsSelect.onClickViewSms(smsData[position].sms.toString(),position)
        }
        holder.binding?.btnResend?.setOnClickListener {
            viewSmsSelect.onResendSms(smsData[position].sms.toString(),smsData[position].phone.toString(),position,smsData[position].id)
        }

    }

    override fun getItemCount(): Int {
        return  smsData.size
    }
}

class SmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: LayoutRvItemCommunicationSmsBinding? = DataBindingUtil.bind(itemView)
}


interface viewSmsSelect{
    fun onClickViewSms(sms:String,positon:Int)
    fun onResendSms(sms: String, phone:String, positon: Int, id: Int?)
}