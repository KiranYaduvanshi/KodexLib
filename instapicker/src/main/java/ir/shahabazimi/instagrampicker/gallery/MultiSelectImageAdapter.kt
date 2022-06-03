package ir.shahabazimi.instagrampicker.gallery

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import java.util.*

class MultiSelectImageAdapter internal constructor(
    private val context: Context,
    private val addresses: ArrayList<String>,
    private val sl: SelectListener
) : PagerAdapter() {
    override fun getCount(): Int {
        return addresses.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val model = addresses[position]
        val imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        imageView.setOnClickListener { w: View? -> sl.onClick(model, position) }

//            Picasso.get().load(Uri.parse(model))
//                    .into(imageView);
//            container.addView(imageView);
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }
}