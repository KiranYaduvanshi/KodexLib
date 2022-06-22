package com.kodextech.project.kodexlib.ui.main.quatation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.CustomerListingAVH

class QuatationAdapter(var context:Context):RecyclerView.Adapter<QuatationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuatationViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.quattaion_list_item, parent, false)
        return QuatationViewHolder(view)

    }

    override fun onBindViewHolder(holder: QuatationViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return  6
    }
}

class QuatationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}
