package com.kodextech.project.kodexlib.ui.main.termsAndServices

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker.Companion.REQUEST_CODE
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityTermsServicesBinding
import com.kodextech.project.kodexlib.dialog.AppAlertOption
import com.kodextech.project.kodexlib.dialog.LogoutDialog
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.model.PickupAddress
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.booking.AddBooking
import com.kodextech.project.kodexlib.ui.main.jobs.StartJobActivity
import com.kodextech.project.kodexlib.ui.main.jobs.adapter.ImageListingAdapter
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible
import com.kodextech.project.kodexlib.utils.zDateConvertor
import com.williamww.silkysignature.views.SignaturePad
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class TermsServices : BaseActivity(), selectAddress {


    private var binding: ActivityTermsServicesBinding? = null
    private var isCalling: Boolean = false
    private var jobId: String? = null

    private var json: JSONArray? = null
    private var media: String? = null
    private  var pickupAddressADapter:PickupAddressADapter? =null
    private var pickupAddList=ArrayList<PickupAddress>()
    private var baseImagePath = "http://13.58.42.125/public/uploads/"

    override fun onSetupViewGroup() {
        mViewGroup = binding?.contentTerms
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms_services)

        initTopBar()
        setPickUpAddressAdapter()

        binding?.topBar?.ivBack?.setOnClickListener { onBackPressed() }

        binding?.tvPhone?.setOnClickListener(View.OnClickListener {
            makeCall()
        })

        val mSignaturePad = findViewById<View>(R.id.signature_pad) as SignaturePad
        binding?.btnSubmit?.setOnClickListener {
            val intent = Intent(this@TermsServices, StartJobActivity::class.java)
            intent.putExtra("jobId", jobId)
            startActivity(intent)
        }

        binding?.btnEmailSend?.setOnClickListener {
            val dl = LogoutDialog.newInstance(
                "Booking Confirmation",
                "Do you Want to Send Email?",
                "Yes",
                "No"
            ) {
                when (it) {
                    AppAlertOption.YES -> {
                        sendEmail(jobId ?: "")
                    }
                    AppAlertOption.NO -> {
                    }
                }
            }
            dl.show(supportFragmentManager, "")
        }

    }

    private fun sendEmail(id: String) {
        showLoading()
        NetworkClass.callApi(URLApi.notifyByEmail(
            job_uuid = id ?: ""
        ), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                showBarToast(message)
                finish()
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }

    private  fun setPickUpAddressAdapter(){
        pickupAddressADapter = PickupAddressADapter(this,pickupAddList,this)
        binding?.pickUpAddressRv?.adapter= pickupAddressADapter

    }

    private fun initTopBar() {
        jobId = intent.getStringExtra("job_id")
        var isForView = intent.getStringExtra("isForView")
        var isFor = intent.getStringExtra("isFor")

        if (isForView.equals("viewOnly")) {
            binding?.topBar?.tvText?.text = "Booking Detail"
            if (isFor.equals("history")) {
                binding?.rlIvCancel?.gone()
                binding?.signaturePad?.gone()
                binding?.ivSignature?.visible()
                binding?.btnSubmit?.gone()
            } else if (isFor.equals("cancelled") || isFor.equals("pending")) {
                binding?.btnSubmit?.gone()
                binding?.tvSignature?.gone()
                binding?.rlIvSignature?.gone()
                binding?.rlNote?.gone()
                binding?.tvComments?.gone()
                binding?.tvDescription?.gone()
                binding?.tvDiamantling?.gone()
            } else if (isFor.equals("assigned")) {
                binding?.rlIvCancel?.gone()
                binding?.rlIvSignature?.gone()
                binding?.rlNote?.gone()
                binding?.tvDescription?.gone()
                binding?.tvComments?.gone()
                binding?.tvSignature?.gone()
                binding?.btnSubmit?.visible()
                binding?.btnSubmit?.text = "Continue"
            } else if (isFor.equals("active")) {
                binding?.btnSubmit?.gone()
                binding?.tvSignature?.gone()
                binding?.rlIvSignature?.gone()
                binding?.rlNote?.gone()
                binding?.tvComments?.gone()
                binding?.tvDescription?.gone()
            }
        } else {
            binding?.topBar?.tvText?.text = "Terms of Service"
            when {
                isFor.equals("history") -> {
                    binding?.rlIvCancel?.gone()
                    binding?.signaturePad?.gone()
                    binding?.ivSignature?.visible()
                    binding?.btnSubmit?.gone()
                    binding?.tvComments?.visible()
                    binding?.tvDescription?.visible()
                }
                isFor.equals("assigned") -> {
                    binding?.rlIvCancel?.gone()
                    binding?.rlIvSignature?.gone()
                    binding?.rlNote?.gone()
                    binding?.tvDescription?.gone()
                    binding?.tvComments?.gone()
                    binding?.tvSignature?.gone()
                    binding?.btnSubmit?.text = "Continue"
                }
                isFor.equals("cancelled") -> {
                    binding?.rlIvCancel?.gone()
                    binding?.rlIvSignature?.gone()
                    binding?.rlNote?.gone()
                    binding?.tvDescription?.visible()
                    binding?.tvComments?.visible()
                    binding?.tvSignature?.gone()
                    binding?.btnSubmit?.gone()
                }
            }

        }
        binding?.topBar?.ivLogout?.setImageResource(R.drawable.ic_edit)

        getJobDetails(jobId)

        binding?.topBar?.ivLogout?.setOnClickListener {
            val intent = Intent(this@TermsServices, AddBooking::class.java)
            intent.putExtra("forEdit", jobId)
            startActivity(intent)
        }

    }

    private fun getJobDetails(jobId: String? = null) {
        Log.i("id","id------------"+jobId)
       // Toast.makeText(binding?.root?.context, ""+jobId, Toast.LENGTH_SHORT).show()
        showLoading()
        NetworkClass.callApi(URLApi.getSpecificJob(job_uuid = jobId.toString()), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                val json = JSONObject(response ?: "")
                val obj = Gson().fromJson(json.toString(), JobModel::class.java)
                setJobDetail(obj)
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }

    private fun setJobDetail(obj: JobModel? = null) {

        if (obj?.is_notify == "0") {
            binding?.btnEmailSend?.visibility = View.VISIBLE
        } else {
            binding?.btnEmailSend?.visibility = View.GONE
        }

        if (obj?.job_status == "pending" || obj?.job_status == "assigned") {
            binding?.topBar?.ivLogout?.visible()
        } else {
            binding?.topBar?.ivLogout?.gone()
        }

        if (obj?.medias?.isEmpty() == true) {
            binding?.tvDiamantling?.gone()
            binding?.rvImage?.gone()
        } else {

            val mMedia = obj?.medias
            if (mMedia != null) {
                for (m in mMedia) {
                    println(m.file_url);
                    Log.i("file path --- ",""+m.path)
                }


            }
            val mAdapter = ImageListingAdapter(this, mMedia ?: ArrayList())
            binding?.rvImage?.layoutManager = GridLayoutManager(this, 3)
            binding?.rvImage?.adapter = mAdapter
            mAdapter?.notifyDataSetChanged()
        }


        binding?.tvBookedBy?.text = obj?.booker?.full_name
        binding?.tvDate?.text = obj?.start_time?.zDateConvertor(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "dd-MM-yyyy HH:mm:ss",
            obj?.start_time
        )
        binding?.tvFirstName?.text = obj?.first_name
        binding?.tvLastName?.text = obj?.last_name
        binding?.tvEmail?.text = obj?.email
        binding?.tvPhone?.text = obj?.phone_code + " " + obj?.phone_numb
        binding?.tvService?.text = obj?.service
        binding?.tvPackingFee?.text = obj?.packing_fee

        if (obj?.service == "House Move") {
            val newArray = ArrayList<AdressListingModel>()
            val newDropArray = ArrayList<AdressListingModel>()
            var floorNo: String? = null
            var dropFloorNo: String? = null
            newArray.clear()
            newDropArray.clear()

            obj.pickup_addresses?.forEachIndexed { index, pickupAddress ->
                pickupAddList.add(pickupAddress)

                setPickUpAddressAdapter()

//                if (pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no == "-1") {
//                    floorNo = "Basement"
//                } else if (pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no == "0") {
//                    floorNo = "Ground Floor"
//                } else {
//                    floorNo = pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no + " Floor"
//                }
//
//                if (pickupAddress.has_lift.equals("1")) {
//                    val s =
//                        "Address: " + pickupAddress.address1 + "\nFloor No: " + floorNo + "\nLift Available: Yes\n"
//                    newArray.add(AdressListingModel(s))
//
//                } else {
//                    val s =
//                        "Address: " + pickupAddress.address1 + "\nFloor No: " + floorNo + "\nLift Available: No\n"
//                    newArray.add(AdressListingModel(s))
//                }

           }



            obj.drop_addresses?.forEachIndexed { index, pickupAddress ->

                when (pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no) {
                    "-1" -> {
                        dropFloorNo = "Basement"
                    }
                    "0" -> {
                        floorNo = "Ground Floor"
                    }
                    else -> {
                        dropFloorNo =
                            pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no + " Floor"
                    }
                }

                if (pickupAddress.has_lift.equals("1")) {
                    val s =
                        "Address: " + pickupAddress.address1 + "\nFloor No: " + dropFloorNo + "\nLift Available: Yes \n"
                    newDropArray.add(AdressListingModel(s))
                } else {
                    val s =
                        "Address: " + pickupAddress.address1 + "\nFloor No: " + dropFloorNo + "\nLift Available: No \n"
                    newDropArray.add(AdressListingModel(s))
                }
            }

            var newDropAddress: String? = null
            newDropArray.forEach {
                newDropAddress += it.address + ""
            }
            var finalAddress: String? = null
            newArray.forEach {
                finalAddress += it.address + ""
            }

            binding?.tvDropAddress?.text = newDropAddress?.replace("null", "")
          //  binding?.tvPickUpAddress?.text = finalAddress?.replace("null", "")

        }
        else {
            val newArray = ArrayList<AdressListingModel>()
            newArray.clear()
            obj?.pickup_addresses?.forEachIndexed { index, pickupAddress ->


//                val s = pickupAddress.address1 + "\n"
//                newArray.add(AdressListingModel(s))

                pickupAddList.add(pickupAddress)

                setPickUpAddressAdapter()


//                val s = pickupAddress.address1
//                newArray.add(AdressListingModel(s))
            }

            var newAddress: String? = null
            newArray.forEach {
                newAddress += it.address + ""
            }
         //   binding?.tvPickUpAddress?.text = newAddress?.replace("null", "")
            binding?.tvDropAddress?.text =
                obj?.drop_address?.address1
        }

//        ° N, ° E

//        binding?.tvPickUpAddress?.setOnClickListener(
//            View.OnClickListener {
//                  openMapDialog(binding?.tvPickUpAddress?.text.toString())
//
//            }
//        )

//        binding?.tvPickUpAddress?.setOnLongClickListener(View.OnLongClickListener {
//            var textToCopy = binding?.tvPickUpAddress?.text
//            val clipboardManager =
//                applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clipData = ClipData.newPlainText("text", textToCopy)
//            clipboardManager.setPrimaryClip(clipData)
//            Toast.makeText(baseContext, "Detail Copied to Clipboard", Toast.LENGTH_SHORT).show()
//            true
//        })
        binding?.tvDropAddress?.setOnLongClickListener(View.OnLongClickListener {
            var textToCopy = binding?.tvDropAddress?.text
            val clipboardManager =
                applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", textToCopy)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(baseContext, "Detail Copied to Clipboard", Toast.LENGTH_SHORT).show()
            true
        })
        binding?.tvDropAddress?.setOnClickListener(
            View.OnClickListener {
                mapWazeDialog(binding?.tvDropAddress?.text.toString())

//                var latLng = getLocationFromAddress(
//                    context = binding?.root?.context,
//                    strAddress = binding!!.tvDropAddress.text.toString()
//                )
//                var geoUri: String =
//                    "http://maps.google.com/maps?q=loc:" + latLng?.latitude + "," + latLng?.longitude + " (" + binding!!.tvDropAddress.text.toString() + ")"
//                var mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
//                if (mapIntent.resolveActivity(binding?.root?.context!!.packageManager) != null) {
//                    startActivity(mapIntent);
//                }
//                var textToCopy = binding?.tvDropAddress?.text
//                val clipboardManager =
//                    applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                val clipData = ClipData.newPlainText("text", textToCopy)
//                clipboardManager.setPrimaryClip(clipData)
//                Toast.makeText(baseContext, "Detail Copied to Clipboard", Toast.LENGTH_SHORT).show()
            }
        )





        binding?.tvFloorNo?.text = obj?.floor_no + " Floor"

        val advance_amount = obj?.advance_amount?.toDoubleOrNull() ?: 0.0
        val advance_amount_rounded = String.format("%.2f", advance_amount)
        binding?.tvDeposit?.text = advance_amount_rounded


        val price_amount = obj?.price_amount?.toDoubleOrNull() ?: 0.0
        val price_amount_rounded = String.format("%.2f", price_amount)
        binding?.tvPrice?.text = price_amount_rounded
        binding?.tvListOfGoods?.text = obj?.white_goods
        binding?.tvNoOfBags?.text = obj?.approx_bags_count + " Bags"
        binding?.tvNoOfBoxes?.text = obj?.approx_boxes_count + " Boxes"
        if (obj?.men_count == "0") {
            binding?.tvMen?.text = "Self Loaded"
        } else {
            binding?.tvMen?.text = obj?.men_count + " Men Required"
        }

        val string = obj?.van_meta?.map { it.qnty + " " + it.model_name }
        binding?.tvVans?.text = string?.joinToString(
            ", ",
            "",
            "",
            obj.van_meta.size,
            "",
            transform = { it.toUpperCase() })
        if (obj?.has_requested_insurance?.equals("1") == true) {
            binding?.cbInsurance?.isChecked = false
            binding?.cbInsurance?.gone()
            binding?.insurancedCustomer?.gone()
        } else {

            binding?.cbInsurance?.gone()
            binding?.insurancedCustomer?.gone()
        }

        binding?.tvDescription?.text = obj?.additional_details

        if (obj?.is_packing_service == "0") {
            binding?.btnPackingServiceNo?.isChecked = true
            binding?.btnPackingServiceYes?.isChecked = false

            binding?.btnPackingServiceYes?.buttonTintList = getColorStateList(R.color.locationCol)
            binding?.btnPackingServiceNo?.buttonTintList = getColorStateList(R.color.blue)
        } else {
            binding?.btnPackingServiceYes?.isChecked = true
            binding?.btnPackingServiceNo?.isChecked = false

            binding?.btnPackingServiceNo?.buttonTintList = getColorStateList(R.color.locationCol)
            binding?.btnPackingServiceYes?.buttonTintList = getColorStateList(R.color.blue)
        }

        if (obj?.is_any_dismantling == "0") {
            binding?.btnDismantlingNo?.isChecked = true
            binding?.btnDismantlingYes?.isChecked = false
            binding?.btnDismantlingYes?.buttonTintList = getColorStateList(R.color.locationCol)
            binding?.btnDismantlingNo?.buttonTintList = getColorStateList(R.color.blue)
        } else {
            binding?.btnDismantlingYes?.isChecked = true
            binding?.btnDismantlingNo?.isChecked = false
            binding?.btnDismantlingNo?.buttonTintList = getColorStateList(R.color.locationCol)
            binding?.btnDismantlingYes?.buttonTintList = getColorStateList(R.color.blue)
        }


        if (obj?.price_nature?.lowercase() == "fixed-price".lowercase()) {
            binding?.priceSign?.gone()
            binding?.llMinimumBooking?.gone()
            binding?.btnFixedPrice?.isChecked = true
            binding?.btnHourlyPrice?.isChecked = false
            binding?.btnFixedPrice?.buttonTintList = getColorStateList(R.color.blue)
            binding?.btnHourlyPrice?.buttonTintList = getColorStateList(R.color.locationCol)
            binding?.tvPrice?.setBackgroundDrawable(getDrawable(R.drawable.button_right_radius))
            binding?.tvPrice?.background?.setTint(getColor(R.color.bgEdit))
        } else {
            binding?.priceSign?.visible()
            binding?.llMinimumBooking?.visible()
            setMinimumButtonColorState(obj?.min_hours_count)
            binding?.btnFixedPrice?.isChecked = false
            binding?.btnHourlyPrice?.isChecked = true
            binding?.tvPrice?.setBackgroundDrawable(getDrawable(R.drawable.button_without_radius))
            binding?.btnFixedPrice?.buttonTintList = getColorStateList(R.color.locationCol)
            binding?.btnHourlyPrice?.buttonTintList = getColorStateList(R.color.blue)
            binding?.tvPrice?.background?.setTint(getColor(R.color.bgEdit))
        }

        when {
            obj?.priority?.lowercase() == "normal" -> {
                binding?.btnNormal?.isChecked = true
                binding?.btnImportant?.isChecked = false
                binding?.btnNormal?.buttonTintList = getColorStateList(R.color.blue)
                binding?.btnImportant?.buttonTintList = getColorStateList(R.color.locationCol)

            }
            obj?.priority?.lowercase() == "high" -> {
                binding?.btnNormal?.isChecked = false
                binding?.btnImportant?.isChecked = true
                binding?.btnImportant?.buttonTintList = getColorStateList(R.color.blue)
                binding?.btnNormal?.buttonTintList = getColorStateList(R.color.locationCol)

            }
            obj?.priority?.lowercase() == "low" -> {
                binding?.btnNormal?.isChecked = false
                binding?.btnImportant?.isChecked = false
                binding?.btnNormal?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.btnImportant?.buttonTintList = getColorStateList(R.color.locationCol)

            }
        }
        Log.i("URL", "" + obj?.signature?.path)
     //
        //   Toast.makeText(binding?.root?.context, "image path ---- "+obj?.signature?.path, Toast.LENGTH_SHORT).show()

        Glide.with(this).load(baseImagePath + obj?.signature?.path)
            .placeholder(R.drawable.ic_baseline_cloud_download_24).into(
                binding?.ivSignature!!
            )

    }


    private fun setMinimumButtonColorState(minimumBooking: String?) {
        when (minimumBooking) {
            "2" -> {
                binding?.rbMinimum2?.isChecked = true
                binding?.rbMinimum3?.isChecked = false
                binding?.rbMinimum4?.isChecked = false
                binding?.rbMinimum5?.isChecked = false
                binding?.rbMinimum6?.isChecked = false
                binding?.rbMinimum2?.buttonTintList = getColorStateList(R.color.blue)
                binding?.rbMinimum3?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum4?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum5?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum6?.buttonTintList = getColorStateList(R.color.locationCol)
            }
            "3" -> {
                binding?.rbMinimum2?.isChecked = false
                binding?.rbMinimum3?.isChecked = true
                binding?.rbMinimum4?.isChecked = false
                binding?.rbMinimum5?.isChecked = false
                binding?.rbMinimum6?.isChecked = false
                binding?.rbMinimum2?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum3?.buttonTintList = getColorStateList(R.color.blue)
                binding?.rbMinimum4?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum5?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum6?.buttonTintList = getColorStateList(R.color.locationCol)
            }
            "4" -> {
                binding?.rbMinimum2?.isChecked = false
                binding?.rbMinimum3?.isChecked = false
                binding?.rbMinimum4?.isChecked = true
                binding?.rbMinimum5?.isChecked = false
                binding?.rbMinimum6?.isChecked = false
                binding?.rbMinimum2?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum3?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum4?.buttonTintList = getColorStateList(R.color.blue)
                binding?.rbMinimum5?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum6?.buttonTintList = getColorStateList(R.color.locationCol)
            }
            "5" -> {
                binding?.rbMinimum2?.isChecked = false
                binding?.rbMinimum3?.isChecked = false
                binding?.rbMinimum4?.isChecked = false
                binding?.rbMinimum5?.isChecked = true
                binding?.rbMinimum6?.isChecked = false
                binding?.rbMinimum2?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum3?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum4?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum5?.buttonTintList = getColorStateList(R.color.blue)
                binding?.rbMinimum6?.buttonTintList = getColorStateList(R.color.locationCol)
            }
            "6" -> {
                binding?.rbMinimum2?.isChecked = false
                binding?.rbMinimum3?.isChecked = false
                binding?.rbMinimum4?.isChecked = false
                binding?.rbMinimum5?.isChecked = false
                binding?.rbMinimum6?.isChecked = true
                binding?.rbMinimum2?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum3?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum4?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum5?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.rbMinimum6?.buttonTintList = getColorStateList(R.color.blue)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Log.i("tag", "Called: $REQUEST_CODE");

        if (isCalling) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                isCalling = false
                ActivityCompat.requestPermissions(
                    this, arrayOf(android.Manifest.permission.CALL_PHONE),
                    REQUEST_CODE
                )

            } else {
                isCalling = false
                val intent = Intent(
                    Intent.ACTION_CALL,
//                    Uri.parse("tel:" + binding?.tvPhone?.text.toString())
                    Uri.parse("tel:" + binding?.tvPhone?.text.toString())
                )
                startActivity(intent)
            }
        }

    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {

    }

    override fun setupLoader() {

    }

    fun pickupAddressMap(address: String?){

        var latLng = getLocationFromAddress(
            context = binding?.root?.context,
            strAddress = address
        )
        var geoUri: String =
            "http://maps.google.com/maps?q=loc:" + latLng?.latitude + "," + latLng?.longitude + " (" + address + ")"
        var mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        if (mapIntent.resolveActivity(binding?.root?.context!!.packageManager) != null) {
            startActivity(mapIntent);
        }


    }

    fun openAddressWaze(address: String?){

        var latLng = getLocationFromAddress(
            context = binding?.root?.context,
            strAddress = address
        )
        var geoUri: String = "https://waze.com/ul?q=66%20Acacia%20Avenue&ll="+latLng?.latitude+","+ latLng?.longitude +"&navigate=yes"

//        "https://waze.com/ul?ll="+""+","+""
        var mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
        mapIntent.setPackage("com.waze");

        if (mapIntent.resolveActivity(binding?.root?.context!!.packageManager) != null) {
            startActivity(mapIntent);
        }
    }



    fun openMapDialog(address: String?) {

        val builder1 = AlertDialog.Builder(binding?.root?.context)
        builder1.setTitle("Open Map")
        builder1.setMessage("Are you sure you want to Redirect Map?")
        builder1.setCancelable(false)
        builder1.setPositiveButton(
            "Yes"
        ) { dialog, which -> mapWazeDialog(address) }
        builder1.setNegativeButton(
            "No"
        ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()

    }


    fun makeCall() {

        val builder1 = AlertDialog.Builder(binding?.root?.context)
        builder1.setTitle("Confirm Call")
        builder1.setMessage("Are you sure you want to make a call?")
        builder1.setCancelable(false)
        builder1.setPositiveButton(
            "Yes"
        ) { dialog, which -> requestCallPermission() }
        builder1.setNegativeButton(
            "No"
        ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()

    }

    fun requestCallPermission() {
        isCalling = true
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.CALL_PHONE),
                REQUEST_CODE
            )

        } else {
            val intent = Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:" + "${binding?.tvPhone?.text.toString()}")
//                Uri.parse("tel:" + binding?.tvPhone?.text.toString())
            )
            startActivity(intent)
            isCalling = false

        }
    }

    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>
        var p1: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            p1 = LatLng(location.getLatitude(), location.getLongitude())
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return p1
    }


    fun mapWazeDialog(address: String?) {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.map_waze_dialog)
        val crossImg = dialog.findViewById<AppCompatImageView>(R.id.ivCross)
        val mapImg = dialog.findViewById<ImageView>(R.id.mapImg)
        val wazeImg = dialog.findViewById<AppCompatImageView>(R.id.wazeImg)
        crossImg.setOnClickListener { dialog.dismiss() }

        wazeImg.setOnClickListener {
            openAddressWaze(address)

        }

        mapImg.setOnClickListener {
            pickupAddressMap(address)

        }
        dialog.setCancelable(false)
        dialog.show()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setGravity(Gravity.CENTER)

    }

    override fun onAddressClick(position: Int, addres: String) {
        mapWazeDialog(addres)
    }




}

class AdressListingModel(
    var address: String? = null,


    )