package com.kodextech.project.kodexlib.ui.main.termsAndServices

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.PickupAddressListItemBinding
import com.kodextech.project.kodexlib.model.PickupAddress

class PickupAddressADapter(var context:Context,var addressList:ArrayList<PickupAddress>,var selectAddress: selectAddress):RecyclerView.Adapter<AddressViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        var view =LayoutInflater.from(context).inflate(R.layout.pickup_address_list_item,parent,false)
        return  AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {

        holder.binding?.tvPickUpAddress?.text =addressList[position].address1
        holder.binding?.tvPickUpAddress?.setOnClickListener {
            selectAddress.onAddressClick(position,addressList[position].address1.toString())
        }

      //  Toast.makeText(context, "size--- "+addressList.size, Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        return  addressList.size
    }


}

class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding:PickupAddressListItemBinding? = DataBindingUtil.bind(itemView)

}
interface  selectAddress{
    fun onAddressClick(position: Int,addres:String)
}
