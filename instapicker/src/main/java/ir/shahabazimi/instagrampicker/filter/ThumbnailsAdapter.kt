package ir.shahabazimi.instagrampicker.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import ir.shahabazimi.instagrampicker.R
import java.util.*

class ThumbnailsAdapter internal constructor(
    private val mContext: Context,
    private val thumbnailItemList: ArrayList<ThumbnailItem>,
    private val listener: ThumbnailsAdapterListener
) : RecyclerView.Adapter<ThumbnailsAdapter.MyViewHolder>() {
    private var selectedIndex = 0

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.thumbnail)
        val filterName: TextView = view.findViewById(R.id.filter_name)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.thumbnail_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val thumbnailItem = thumbnailItemList[position]
        holder.thumbnail.setImageBitmap(thumbnailItem.image)
        holder.thumbnail.setOnClickListener { view: View? ->
            listener.onFilterSelected(thumbnailItem.filter)
            selectedIndex = position
            notifyDataSetChanged()
        }
        holder.filterName.text = thumbnailItem.filterName
        if (selectedIndex == position) {
            holder.filterName.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.filter_label_selected
                )
            )
        } else {
            holder.filterName.setTextColor(
                ContextCompat.getColor(
                    mContext,
                    R.color.filter_label_normal
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return thumbnailItemList.size
    }

    interface ThumbnailsAdapterListener {
        fun onFilterSelected(filter: Filter?)
    }
}