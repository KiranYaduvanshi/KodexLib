package com.kodextech.project.kodexlib.ui.main.quatation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.JobItemBinding
import com.kodextech.project.kodexlib.databinding.QuattaionListItemBinding
import com.kodextech.project.kodexlib.model.CustomerModel
import com.kodextech.project.kodexlib.ui.main.customer.CustomerProfile
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.CustomerListingAVH
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible

class QuatationAdapter(var context: Context, var mData: ArrayList<CustomerModel>,
                       var callBack: ((position: Int) -> Unit)) : RecyclerView.Adapter<QuatationViewHolder>() {
    var isSelected = false
    companion object{
        var list: ArrayList<CustomerModel> = ArrayList()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuatationViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.quattaion_list_item, parent, false)
        return QuatationViewHolder(view)

    }

    override fun onBindViewHolder(holder: QuatationViewHolder, position: Int) {
        isSelected = true
        if (mData.size >0) {
            var itemData = mData[position]
            holder.binding?.cardView?.setOnClickListener {

                if (list.contains(itemData)) {
                    list.remove(itemData)
                    holder.binding?.ivSelected?.gone()

                  //  holder.binding?.cardView?.setCardBackgroundColor(Color.WHITE)
                } else {
                    holder.binding?.ivSelected?.visible()

                    list.add(itemData)
                 //   holder.binding?.cardView?.setCardBackgroundColor(Color.BLUE)
                }
            }

            holder.binding?.customerName?.text = "${itemData?.first_name} ${itemData?.last_name}"
            holder.binding?.tvEmail?.text = itemData.email
            holder.binding?.tvPhone?.text = "${itemData?.phone_code} ${itemData?.phone_numb}"
            holder.binding?.clientAddress?.text = itemData.pickup_address?.address1
            holder.binding?.customerRating?.text = "${itemData.customer_rating.toString()}.0"
            holder.binding?.rating?.rating = itemData.customer_rating?.toFloat()!!

            holder.binding?.btnDetail?.setOnClickListener {
                val intent = Intent(context, CustomerProfile::class.java)
                intent.putExtra("customerObj", itemData)
                context.startActivity(intent)
            }
        }
        else{

        }


    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun filterList(filteredList: ArrayList<CustomerModel>){
        mData = filteredList
        notifyDataSetChanged()
    }

}

class QuatationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: QuattaionListItemBinding? = DataBindingUtil.bind(itemView)
}

