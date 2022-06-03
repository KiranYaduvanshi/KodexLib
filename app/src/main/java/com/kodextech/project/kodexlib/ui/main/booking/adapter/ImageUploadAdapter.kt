package com.kodextech.project.kodexlib.ui.main.booking.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ImageUploadBinding
import com.kodextech.project.kodexlib.ui.main.booking.FullImageView
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.Placeholders
import com.kodextech.project.kodexlib.ui.main.dashboard.adapter.loadImage
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible
import java.io.File

class ImageUploadAdapter(
    var mContext: BaseActivity,
    var mData: ArrayList<File>,
    var callBack: ((position: Int, forDelete: Boolean) -> Unit)
) : RecyclerView.Adapter<ImageVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
        val view = LayoutInflater.from(mContext).inflate(R.layout.image_upload, parent, false)
        return ImageVH(view)
    }

    override fun onBindViewHolder(holder: ImageVH, position: Int) {

        if (position == 0) {
            holder.binding?.rluploadFile?.visible()
            holder.binding?.llItem?.gone()
            holder.itemView.setOnClickListener {
                callBack(position, false)
            }
        } else {
            holder.binding?.llItem?.visible()
            holder.binding?.rluploadFile?.gone()
            val mItem = mData[position - 1]


            holder.binding?.ivUploadedMedia?.loadImage(
                mItem.path,
                Placeholders.USER,
                false
            )

            holder.binding?.ivDeleteImageMediaUpload?.setOnClickListener {
                callBack(position, true)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(mContext, FullImageView::class.java)
                intent.putExtra("from", "addBooking")
                mContext.startActivity(intent)
            }

        }


    }


    override fun getItemCount() = mData.size + 1
}

class ImageVH(view: View) : RecyclerView.ViewHolder(view) {
    val binding: ImageUploadBinding? = DataBindingUtil.bind(view)
}
