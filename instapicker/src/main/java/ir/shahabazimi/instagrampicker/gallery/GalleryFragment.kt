package ir.shahabazimi.instagrampicker.gallery

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theartofdev.edmodo.cropper.CropImage
import ir.shahabazimi.instagrampicker.InstagramPicker
import ir.shahabazimi.instagrampicker.R
import ir.shahabazimi.instagrampicker.classes.TouchImageView
import ir.shahabazimi.instagrampicker.filter.FilterActivity
import java.util.*

class GalleryFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var adapter: GalleryAdapter? = null
    private var multiSelectBtn: ImageView? = null
    private var preview: TouchImageView? = null
    private var multiSelect = false
    private val data = ArrayList<GalleryModel>()
    private var selectedPic = ""
    private var selectedPics: ArrayList<String>? = null
    private var mContext: Context? = null
    private var mActivity: FragmentActivity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_gallery, container, false)
        mContext = context
        mActivity = activity
        assert(mActivity != null)
        val actionBar = (mActivity as AppCompatActivity?)!!.supportActionBar!!
        actionBar.setTitle(getString(R.string.instagrampicker_gallery_title))
        setHasOptionsMenu(true)
        multiSelectBtn = v.findViewById(R.id.gallery_multiselect)
        if (!InstagramPicker.multiSelect) {
            multiSelectBtn?.visibility = View.GONE
        }
        multiSelectBtn?.setOnClickListener(View.OnClickListener { w: View? ->
            val positionView = (Objects.requireNonNull(
                recyclerView!!.layoutManager
            ) as GridLayoutManager).findFirstVisibleItemPosition()
            multiSelect = !multiSelect
            multiSelectBtn?.setImageResource(if (multiSelect) R.mipmap.ic_multi_enable else R.mipmap.ic_multi_disable)
            adapter = GalleryAdapter(data, object : GalleySelectedListener {
                override fun onSingleSelect(address: String?) {
                    // preview.setImageURI(Uri.parse(address));
                    try {
                        preview?.setImageBitmap(scale(address ?: ""))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    selectedPic = address ?: ""
                }

                override fun onMultiSelect(addresses: ArrayList<String>) {
                    if (addresses.isNotEmpty()) {
                        selectedPic = ""
                        //  preview.setImageURI(Uri.parse(addresses.get(addresses.size() - 1)));
                        try {
                            preview!!.setImageBitmap(scale(addresses[addresses.size - 1]))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        selectedPics = addresses
                    }
                }
            }, multiSelect)
            recyclerView!!.adapter = adapter
            adapter!!.notifyDataSetChanged()
            recyclerView!!.layoutManager!!.scrollToPosition(positionView)
        })
        preview = v.findViewById(R.id.gallery_view)
        recyclerView = v.findViewById(R.id.gallery_recycler)
        recyclerView?.layoutManager = GridLayoutManager(
            context,
            4,
            RecyclerView.VERTICAL,
            false
        )
        adapter = GalleryAdapter(data, object : GalleySelectedListener {
            override fun onSingleSelect(address: String?) {
                // preview.setImageURI(Uri.parse(address));
                try {
                    preview?.setImageBitmap(scale(address ?: ""))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                selectedPic = address ?: ""
            }

            override fun onMultiSelect(addresses: ArrayList<String>) {
                selectedPic = ""
                if (!addresses.isEmpty()) {
                    //   preview.setImageURI(Uri.parse(addresses.get(0)));
                    try {
                        preview?.setImageBitmap(scale(addresses[0]))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    selectedPics = addresses
                }
            }
        }, multiSelect)
        recyclerView?.adapter = adapter
        picturePaths
        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result?.uri
                val `in` = Intent(context, FilterActivity::class.java)
                `in`.putExtra("uri", resultUri)
                FilterActivity.picAddress = resultUri
                startActivity(`in`)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result?.error
                Toast.makeText(context, error?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_open) {
            val x = InstagramPicker.x
            val y = InstagramPicker.y
            if (!selectedPic.isEmpty()) {
                CropImage.activity(Uri.parse(selectedPic)) //                        .setAspectRatio(x, y)
                    .setFixAspectRatio(false)
                    .start(mContext!!, this)
            } else if (selectedPics!!.size == 1) {
                CropImage.activity(Uri.parse(selectedPics!![0])) //                        .setAspectRatio(x, y)
                    .setFixAspectRatio(false)
                    .start(mContext!!, this)
            } else if (selectedPics!!.size > 1) {
                val i = Intent(activity, MultiSelectActivity::class.java)
                MultiSelectActivity.addresses = selectedPics
                startActivity(i)
                mActivity!!.overridePendingTransition(R.anim.bottom_up_anim, R.anim.bottom_down_anim)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun getOrientation(context: Context?, photoUri: Uri): Int {
        val cursor = context?.contentResolver?.query(
            photoUri,
            arrayOf(MediaStore.Images.ImageColumns.ORIENTATION),
            null,
            null,
            null
        )
        return if (cursor != null) {
            if (cursor.count != 1) {
                cursor.close()
                return -1
            }
            cursor.moveToFirst()
            cursor.getInt(0)
        } else -1
    }

    @Throws(Exception::class)
    private fun scale(address: String): Bitmap? {
        val photoUri = Uri.parse(address)
        var `is` = mContext!!.contentResolver.openInputStream(photoUri)
        val dbo = BitmapFactory.Options()
        dbo.inJustDecodeBounds = true
        BitmapFactory.decodeStream(`is`, null, dbo)
        `is`!!.close()
        val rotatedWidth: Int
        val rotatedHeight: Int
        val orientation = getOrientation(mContext, photoUri)
        if (orientation == 90 || orientation == 270) {
            rotatedWidth = dbo.outHeight
            rotatedHeight = dbo.outWidth
        } else {
            rotatedWidth = dbo.outWidth
            rotatedHeight = dbo.outHeight
        }
        val MAX_IMAGE_DIMENSION = 960
        var srcBitmap: Bitmap?
        `is` = mContext!!.contentResolver.openInputStream(photoUri)
        if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
            val widthRatio = rotatedWidth.toFloat() / MAX_IMAGE_DIMENSION.toFloat()
            val heightRatio = rotatedHeight.toFloat() / MAX_IMAGE_DIMENSION.toFloat()
            val maxRatio = Math.max(widthRatio, heightRatio)
            val options = BitmapFactory.Options()
            options.inSampleSize = maxRatio.toInt()
            srcBitmap = BitmapFactory.decodeStream(`is`, null, options)
        } else {
            srcBitmap = BitmapFactory.decodeStream(`is`)
        }
        `is`!!.close()
        if (orientation > 0) {
            val matrix = Matrix()
            matrix.postRotate(orientation.toFloat())
            srcBitmap = Bitmap.createBitmap(
                srcBitmap!!, 0, 0, srcBitmap.width,
                srcBitmap.height, matrix, true
            )
        }
        return srcBitmap
    }

    //   preview.setImageURI(Uri.parse(selectedPic));
    private val picturePaths: Unit
        private get() {
            val allImagesuri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection =
                arrayOf(MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media._ID)
            try {
                if (activity != null) {
                    val cursor = mActivity?.contentResolver?.query(
                        allImagesuri,
                        projection,
                        null,
                        null,
                        MediaStore.Images.Media.DATE_ADDED
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        do {
                            val datapath = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))
                            ).toString()
                            val model = GalleryModel(datapath, false)
                            data.add(0, model)
                            adapter!!.notifyItemInserted(data.size)
                        } while (cursor.moveToNext())
                        if (!data[0].address.isEmpty()) {
                            selectedPic = data[0].address
                            //   preview.setImageURI(Uri.parse(selectedPic));
                            try {
                                preview!!.setImageBitmap(scale(selectedPic))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        cursor.close()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}