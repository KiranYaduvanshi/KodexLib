package com.kodextech.project.kodexlib.ui.main.quatation

import CustomerSearchModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.databinding.CustomerNameListBinding

class SearchCustomerAdapter(var context:Context,var list:ArrayList<CustomerSearchModel>) :RecyclerView.Adapter<MySearchClass>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MySearchClass {
        val view =
            LayoutInflater.from(context).inflate(R.layout.customer_name_list, parent, false)
        return MySearchClass(view)
    }

    override fun onBindViewHolder(holder: MySearchClass, position: Int) {

        holder.binding?.nameTv?.text=list[position].full_name.toString()


      //  Toast.makeText(context, "Size --- "+list.toString(), Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        return  list.size
    }
}

class MySearchClass(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: CustomerNameListBinding? = DataBindingUtil.bind(itemView)


}
