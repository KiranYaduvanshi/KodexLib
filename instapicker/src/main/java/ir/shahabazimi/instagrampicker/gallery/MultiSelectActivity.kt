package ir.shahabazimi.instagrampicker.gallery

//import ir.shahabazimi.instagrampicker.ultrapger.transformer.UltraScaleTransformer
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.theartofdev.edmodo.cropper.CropImage
import ir.shahabazimi.instagrampicker.InstagramPicker
import ir.shahabazimi.instagrampicker.R
import ir.shahabazimi.instagrampicker.classes.BackgroundActivity.Companion.instance
import ir.shahabazimi.instagrampicker.classes.Statics
import ir.shahabazimi.instagrampicker.classes.Statics.INTENT_FILTER_ACTION_NAME
import ir.shahabazimi.instagrampicker.filter.FilterActivity
import ir.shahabazimi.instagrampicker.ultrapger.UltraScaleTransformer
import ir.shahabazimi.instagrampicker.ultrapger.UltraViewPager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MultiSelectActivity : AppCompatActivity() {
    private var finalAddresses: ArrayList<String>? = null
    private var position = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_select)
        val toolbar = findViewById<Toolbar>(R.id.multi_select_toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull(supportActionBar)!!.title =
            getString(R.string.instagrampicker_multi_select_title)
        finalAddresses = ArrayList()
        finalAddresses!!.addAll(addresses!!)
        initViewPager(finalAddresses)
        registerReceiver(br, IntentFilter("ImageUpdated"))
    }

    private fun initViewPager(addressesList: ArrayList<String>?) {
        val ultraViewPager = findViewById<UltraViewPager>(R.id.multi_select_pager)
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
        val adapter =
            MultiSelectImageAdapter(this, addressesList!!, object:SelectListener{
                override fun onClick(a: String?, p: Int) {
                    position = p
                    CropImage.activity(Uri.parse(a)) //                    .setAspectRatio(4, 3)
                        .setFixAspectRatio(false)
                        .start(this@MultiSelectActivity)
                }
            })
        ultraViewPager.adapter = adapter
        ultraViewPager.setPageTransformer(true, UltraScaleTransformer())
        ultraViewPager.initIndicator()
        ultraViewPager.indicator
            ?.setOrientation(UltraViewPager.Orientation.HORIZONTAL)
            ?.setFocusColor(-0x328ca)
            ?.setNormalColor(-0x13100f)
            ?.setRadius(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, resources.displayMetrics)
                    .toInt()
            )
        ultraViewPager.indicator?.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM)
        ultraViewPager.indicator?.setMargin(0, 0, 0, 30)
        ultraViewPager.setMultiScreen(0.6f)
        ultraViewPager.setItemRatio(1.0)
        //  ultraViewPager.setAutoMeasureHeight(true);
        ultraViewPager.indicator?.build()
    }

    private val br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, data: Intent) {
            try {
                val b = Statics.updatedPic
                val p = Objects.requireNonNull(data.extras)?.getInt("position") ?:0
                val f = File.createTempFile(
                    "mypic",
                    ".jpeg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                )
                val bos = ByteArrayOutputStream()
                b!!.compress(Bitmap.CompressFormat.JPEG, 80, bos)
                val pic = bos.toByteArray()
                val fileOutputStream = FileOutputStream(f)
                fileOutputStream.write(pic)
                fileOutputStream.close()
                fileOutputStream.flush()
                finalAddresses!![p] = Uri.fromFile(f).toString()
                initViewPager(finalAddresses)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(br)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_open) {
            if (InstagramPicker.addresses == null) {
                InstagramPicker.addresses = ArrayList()
            }
            InstagramPicker.addresses = finalAddresses
            sendBroadcast(Intent(INTENT_FILTER_ACTION_NAME))
            finish()
            Objects.requireNonNull(instance!!.activity)!!.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result?.uri
                val `in` = Intent(this, FilterActivity::class.java)
                FilterActivity.picAddress = resultUri
                FilterActivity.position = position
                startActivityForResult(`in`, 123)
            }
        }
    }

    companion object {
        var addresses: ArrayList<String>? = null
    }
}