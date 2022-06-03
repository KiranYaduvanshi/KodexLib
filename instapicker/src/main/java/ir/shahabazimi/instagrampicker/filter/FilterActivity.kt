package ir.shahabazimi.instagrampicker.filter

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.zomato.photofilters.imageprocessors.Filter
import ir.shahabazimi.instagrampicker.InstagramPicker
import ir.shahabazimi.instagrampicker.R
import ir.shahabazimi.instagrampicker.classes.BackgroundActivity.Companion.instance
import ir.shahabazimi.instagrampicker.classes.Statics
import ir.shahabazimi.instagrampicker.classes.Statics.INTENT_FILTER_ACTION_NAME
import ir.shahabazimi.instagrampicker.filter.BitmapUtils.getBitmapFromGallery
import ir.shahabazimi.instagrampicker.filter.FiltersListFragment.FiltersListFragmentListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class FilterActivity : AppCompatActivity(), FiltersListFragmentListener {
    private var imagePreview: ImageView? = null
    private var originalImage: Bitmap? = null
    private var filteredImage: Bitmap? = null
    private var finalImage: Bitmap? = null
    private var filtersListFragment: FiltersListFragment? = null

    companion object {
        var position = -1
        var picAddress: Uri? = null

        init {
            System.loadLibrary("NativeImageProcessor")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        val toolbar = findViewById<Toolbar>(R.id.filter_toolbar)
        setSupportActionBar(toolbar)
        assert(supportActionBar != null)
        supportActionBar!!.title = getString(R.string.instagrampicker_filter_title)
        val viewPager = findViewById<ViewPager>(R.id.viewpager)
        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        imagePreview = findViewById(R.id.image_preview)
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
        Handler().postDelayed({
            try {
                renderImage(picAddress)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 500)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        filtersListFragment = FiltersListFragment()
        filtersListFragment!!.setListener(this)
        adapter.addFragment(filtersListFragment!!, getString(R.string.tab_filters))
        viewPager.adapter = adapter
    }

    override fun onFilterSelected(filter: Filter?) {
        filteredImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
        imagePreview?.setImageBitmap(filter?.processFilter(filteredImage))
        finalImage = filteredImage?.copy(Bitmap.Config.ARGB_8888, true)
    }

    internal class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(
        manager!!
    ) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_open) {
            Statics.updatedPic = null
            return if (position != -1) {
                val i = Intent("ImageUpdated")
                Statics.updatedPic = filteredImage
                i.putExtra("position", position)
                sendBroadcast(i)
                finish()
                true
            } else {
                try {
                    val b = finalImage
                    val f = File.createTempFile(
                        "my_pic",
                        ".jpeg",
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    )
                    val bos = ByteArrayOutputStream()
                    b!!.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                    val pic = bos.toByteArray()
                    val fileOutputStream = FileOutputStream(f)
                    fileOutputStream.write(pic)
                    fileOutputStream.close()
                    fileOutputStream.flush()
                    if (InstagramPicker.addresses == null) {
                        InstagramPicker.addresses = ArrayList()
                    }
                    InstagramPicker.addresses.add(Uri.fromFile(f).toString())
                    sendBroadcast(Intent(INTENT_FILTER_ACTION_NAME))
                    finish()
                    Objects.requireNonNull(instance!!.activity)!!.finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Throws(Exception::class)
    private fun renderImage(uri: Uri?) {
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        filteredImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
        finalImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
        imagePreview!!.setImageBitmap(originalImage)
        bitmap.recycle()

        // render selected image thumbnails
        filtersListFragment!!.prepareThumbnail(originalImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val SELECT_GALLERY_IMAGE = 101
        if (resultCode == RESULT_OK && requestCode == SELECT_GALLERY_IMAGE) {
            val bitmap = getBitmapFromGallery(this, data!!.data, 800, 800)
            originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            filteredImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
            finalImage = originalImage?.copy(Bitmap.Config.ARGB_8888, true)
            imagePreview!!.setImageBitmap(originalImage)
            bitmap.recycle()
            filtersListFragment?.prepareThumbnail(originalImage)
        } else {
            onBackPressed()
        }
    }


}