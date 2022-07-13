package com.kodextech.project.kodexlib.ui.main.termsAndServices

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.PickupAddressListItemBinding
import com.kodextech.project.kodexlib.model.PickupAddress

class PickupAddressADapter(
    var context: Context,
    var addressList: ArrayList<PickupAddress>,
    var selectAddress: selectAddress
) : RecyclerView.Adapter<AddressViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        var view =
            LayoutInflater.from(context).inflate(R.layout.pickup_address_list_item, parent, false)
        return AddressViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {

        holder.binding?.tvPickUpAddress?.setOnClickListener {
            selectAddress.onAddressClick(position, addressList[position].address1.toString())
        }
        var floorNo: String = ""

        if (addressList[position].pickup_flat_meta?.firstOrNull()?.floor_no == "-1") {
            floorNo = "Basement"
        } else if (addressList[position].pickup_flat_meta?.firstOrNull()?.floor_no == "0") {
            floorNo = "Ground Floor"
        } else {
            floorNo = addressList[position].pickup_flat_meta?.firstOrNull()?.floor_no + " Floor"
        }

        if (addressList[position].has_lift.equals("1")) {
            val s =
                "Address: " + addressList[position].address1 + "\nFloor No: " + floorNo + "\nLift Available: Yes\n"
            holder.binding?.tvPickUpAddress?.text = s


        } else {
            val s =
                "Address: " + addressList[position].address1 + "\nFloor No: " + floorNo + "\nLift Available: No\n"
            holder.binding?.tvPickUpAddress?.text = s

        }

        //  Toast.makeText(context, "size--- "+addressList.size, Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        return addressList.size
    }


}

class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding: PickupAddressListItemBinding? = DataBindingUtil.bind(itemView)

}

interface selectAddress {
    fun onAddressClick(position: Int, addres: String)
}
