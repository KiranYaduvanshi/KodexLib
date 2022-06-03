package ir.shahabazimi.instagrampicker.gallery

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ir.shahabazimi.instagrampicker.InstagramPicker
import ir.shahabazimi.instagrampicker.R
import java.util.*

class GalleryAdapter internal constructor(
    private val list: ArrayList<GalleryModel>,
    private val galleySelectedListener: GalleySelectedListener,
    multiSelect: Boolean
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    private var count = 0
    private val selectedPics: ArrayList<String>
    private val multiSelect: Boolean
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_gallery_pics, parent, false)
        )
    }

    override fun onBindViewHolder(h: ViewHolder, position: Int) {
        val model = list[position]

//        h.pic.load
//        Picasso.get().load(Uri.parse(model.getAddress()))
//                .resize(150, 150)
//                .centerCrop()
//                .into(h.pic);
//        h.pic.
        Glide.with(h.pic)
            .load(Uri.parse(model.address))
            .centerCrop()
            .into(h.pic)

        h.bgSelect.visibility = if (model.isSelected) View.VISIBLE else View.GONE
        h.itemView.setOnClickListener { v: View? ->
            if (multiSelect) {
                if (count == selectedPics.size) {
                    if (model.isSelected) {
                        h.bgSelect.visibility = View.GONE
                        selectedPics.remove(model.address)
                    }
                    return@setOnClickListener
                }
                model.isSelected = !model.isSelected
                h.bgSelect.visibility = if (model.isSelected) View.VISIBLE else View.GONE
                if (model.isSelected) {
                    h.bgSelect.isChecked = !h.bgSelect.isChecked
                    selectedPics.add(model.address)
                } else {
                    selectedPics.remove(model.address)
                }
                galleySelectedListener.onMultiSelect(selectedPics)
            } else {
                galleySelectedListener.onSingleSelect(model.address)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val pic: ImageView = v.findViewById(R.id.row_gallery_pic)
        val bgSelect: RadioButton = v.findViewById(R.id.row_gallery_select)

    }

    init {
        if (multiSelect) {
            count = InstagramPicker.numberOfPictures
        }
        this.multiSelect = multiSelect
        selectedPics = ArrayList()
        if (!multiSelect) {
            for (i in 1 until list.size) list[i].isSelected = false
        }
    }
}