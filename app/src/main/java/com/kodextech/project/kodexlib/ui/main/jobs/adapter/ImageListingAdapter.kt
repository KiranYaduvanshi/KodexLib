package com.kodextech.project.kodexlib.ui.main.jobs.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ImageViewBinding
import com.kodextech.project.kodexlib.model.MediaModel
import com.kodextech.project.kodexlib.ui.main.booking.AddBooking
import com.kodextech.project.kodexlib.ui.main.booking.FullImageView

class ImageListingAdapter(
    var mContext: BaseActivity,
    var mData: ArrayList<MediaModel>
) : RecyclerView.Adapter<ImageVHListing>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVHListing {
        val view = LayoutInflater.from(mContext).inflate(R.layout.image_view, parent, false)
        return ImageVHListing(view)
    }

    override fun onBindViewHolder(holder: ImageVHListing, position: Int) {
        val mItem = mData[position]

        val circularProgressDrawable = CircularProgressDrawable(mContext)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        Glide.with(mContext)
            .load(mItem.file_url)
            .centerCrop()
            .placeholder(circularProgressDrawable)//circularProgressDrawable
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(holder.binding?.ivImage!!)

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, FullImageView::class.java)
            AddBooking.scrollImageList.clear()
            AddBooking.scrollImageList.addAll(mData)
            Log.i("PATH",""+mData[position].path)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}

class ImageVHListing(view: View) : RecyclerView.ViewHolder(view) {
    val binding: ImageViewBinding? = DataBindingUtil.bind(view)
}

