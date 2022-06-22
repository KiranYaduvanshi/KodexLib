package com.kodextech.project.kodexlib

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MulSelAdapter(var context:Context):RecyclerView.Adapter<MulSelViewHolder>() {
    var sleected:Int =-1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MulSelViewHolder {
        val view =LayoutInflater.from(context).inflate(R.layout.mul_sel_list_item,parent,false)
        return  MulSelViewHolder(view)
    }

    override fun onBindViewHolder(holder: MulSelViewHolder, position: Int) {
        sleected =position

        holder.itemView.setOnClickListener {
            if (sleected==position){
                sleected =-1
                holder.cardView.setCardBackgroundColor(Color.GREEN)
                Toast.makeText(context, "if part slected", Toast.LENGTH_SHORT).show()
            }
            else{
                sleected =position
                holder.cardView.setCardBackgroundColor(Color.WHITE)
                Toast.makeText(context, "else  part deselect", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return  10
    }
}

class MulSelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

   var  cardView: CardView=itemView.findViewById(R.id.cardView)

}
