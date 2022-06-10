package com.kodextech.project.kodexlib.ui.main.booking

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.telephony.SmsManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arkapp.iosdatettimepicker.ui.DialogDateTimePicker
import com.arkapp.iosdatettimepicker.utils.OnDateTimeSelectedListener
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.gson.Gson
import com.jaiselrahman.filepicker.model.MediaFile
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityAddBookingBinding
import com.kodextech.project.kodexlib.dialog.*
import com.kodextech.project.kodexlib.model.*
import com.kodextech.project.kodexlib.network.LocalPreference
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.NetworkClass.Companion.TAG
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.booking.adapter.AddAddressAdapter
import com.kodextech.project.kodexlib.ui.main.booking.adapter.AddVanAdapter
import com.kodextech.project.kodexlib.ui.main.booking.adapter.ImageUploadAdapter
import com.kodextech.project.kodexlib.ui.main.jobs.JobsListing
import com.kodextech.project.kodexlib.ui.main.jobs.adapter.ImageListingAdapter
import com.kodextech.project.kodexlib.ui.main.termsAndServices.AdressListingModel
import com.kodextech.project.kodexlib.utils.*
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.toString


class AddBooking : BaseActivity() {

    private var binding: ActivityAddBookingBinding? = null

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)

    private var location_id_GOOGLE: String = ""
    private var locationName: String = ""
    private var location_lat: String = ""
    private var location_lng: String = ""
    private var location_city: String = ""
    private var location_address: String = ""
    private var location_state: String = ""
    private var location_country: String = ""
    private var location_postal_code: String = ""
    private var is_location_saved: String = ""
    private var location_id: String = ""

    private var hashmap = HashMap<String, String>()
    private var drop_location_id_GOOGLE: String = ""
    private var drop_locationName: String = ""
    private var drop_location_lat: String = ""
    private var drop_location_lng: String = ""
    private var drop_location_city: String = ""
    private var drop_location_address: String = ""
    private var drop_location_state: String = ""
    private var drop_location_country: String = ""
    private var drop_location_postal_code: String = ""
    private var drop_is_location_saved: String = ""
    private var drop_location_id: String = ""

    private var firstName: String? = null
    private var secondName: String? = null
    private var email: String? = null
    private var phoneCode: String? = "+44"
    private var number: String? = null
    private var number2: String? = null
    private var phoneNo: String? = null
    private var phoneNo2: String? = null
    private var service: String? = null
    private var vans: String? = null
    private var advancePayment: String? = null
    private var noOfMens: String? = null
    private var locationPickupID: String? = null
    private var locationDropID: String? = null
    private var pickupAddress: String? = null
    private var price: String? = null
    private var model: JSONArray? = null
    private var otherComments: String? = null
    private var profileType: String? = null
    private var dateTime: String? = ""
    private var priceNatureState: String? = "fixed-price"
    private var packingService: String? = null
    private var dismantling: String? = null
    private var priorityState: String? = ""
    private var vanCountState: String? = ""
    private var liftState: String? = null
    private var white_goods: String? = null
    private var minimum_booking: String? = null
    private var approx_boxes_count: String? = null
    private var approx_bags_count: String? = null
    private var floor_no: String? = null
    private var has_requested_insurance: String? = null
    private var has_lift: String? = "0"
    private var has_assembling: String? = null
    private var phone_code_2: String? = null
    private var phone_numb_2: String? = null
    private var c: String = ""
    private var packingFee: String = ""
    var profile_image_selected: ArrayList<Uri>? = null

    private var mImageAdapter: ImageUploadAdapter? = null

    private var mCustomerData = ArrayList<CustomerModel>()
    private var mCustomerName = ArrayList<String>()
    private var file = ArrayList<File>()
    private var mMediaData = ArrayList<MediaModel>()
    private var media: String? = ""
    private val mediaFiles = java.util.ArrayList<MediaFile>()
    private val IMAGE_REQUEST_CODE = 2212

    private var mVansArray = ArrayList<Vanmodel>()
    private var vanArray = ArrayList<Vanmodel>()
    private var mFloorArray = ArrayList<FloorAndFlatModel>()
    private var mPAddressArray = ArrayList<PickupAddress>()
    private var mDropAddressArray = ArrayList<PickupAddress>()
    private var mVanAdapter: AddVanAdapter? = null
    private var mAddressAdapter: AddAddressAdapter? = null


    private var jumboVanCount: Int? = 0
    private var wmbVanCount: Int? = 0
    private var lutonVanCount: Int? = 0

    private var smsContent: String? = null
    private var uploaded_files: String? = null
    private var dropAddressString: String? = null
    private var pickupAddressString: String? = null
    private var jsonVan: String? = null
    private var jobIdForEdit: String? = ""


    companion object {
        val scrollImageList = ArrayList<MediaModel>()
        val mData = ArrayList<File>()
    }

    override fun onSetupViewGroup() {
        mViewGroup = binding?.contentAddBooking
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_booking)

        checkPermissions()
        initTopBar()
        changePriceColorState()
        setImageAdapter()

        binding?.etEmail?.hint = setMandatoryHintData("Email");
        binding?.etFirstName?.hint = setMandatoryHintData("First Name");
        binding?.etLastName?.hint = setMandatoryHintData("Last Name");
        binding?.etNoOfBags?.hint = setMandatoryHintData("No of Bags");
        binding?.etNoOfBoxes?.hint = setMandatoryHintData("No of Boxes");
        binding?.etAdvancePayment?.hint = setMandatoryHintData("Deposit");
        binding?.spMen?.text = setMandatoryHintData("How Many Mens You Required?");
        binding?.spService?.text = setMandatoryHintData("Services");
        binding?.etPrice?.hint = setMandatoryHintData("Price Amount");
        binding?.tvPickUpAddress?.text = setMandatoryHintData("Pickup Address");
        binding?.tvDropAddress?.text = setMandatoryHintData("Drop Address");
        binding?.tvDateTime?.text = setMandatoryHintData("Date & Time");

        binding?.tvBookedBy?.text = LocalPreference.shared.user?.user?.profile?.full_name

        binding?.topBar?.ivBack?.setOnClickListener { onBackPressed() }

        binding?.tvDateTime?.setOnClickListener {
            setDateAndTime()
        }

        binding?.btnCancel?.setOnClickListener {
            finish()
        }

//        binding?.btnLiftNo?.setOnClickListener {
//            has_lift = "0"
//            liftState = "liftNo"
//            ChangeLiftColorState()
//        }
//
//        binding?.btnLiftYes?.setOnClickListener {
//            has_lift = "1"
//            liftState = "liftYes"
//            ChangeLiftColorState()
//        }


        binding?.btnPackingServiceYes?.setOnClickListener {
            binding?.rlPackingFee?.visibility = View.VISIBLE
            packingService = "1"
            changePackingColor()
        }

        binding?.btnPackingServiceNo?.setOnClickListener {
            binding?.rlPackingFee?.visibility = View.GONE
            packingService = "0"
            changePackingColor()
        }
        binding?.btnDismantlingYes?.setOnClickListener {
            dismantling = "1"
            changeDismantlingColor()
        }

        binding?.btnDismantlingNo?.setOnClickListener {
            dismantling = "0"
            changeDismantlingColor()
        }


        binding?.btnHourlyPrice?.setOnClickListener {
            priceNatureState = "hourly-price"
            binding?.llMinimumBooking?.visible()
//            minimum_booking = "2"
//            changeMinimumBookingColor()
            changePriceColorState()
        }

        binding?.btnFixedPrice?.setOnClickListener {
            priceNatureState = "fixed-price"
            binding?.llMinimumBooking?.gone()
//            minimum_booking = "2"
//            changeMinimumBookingColor()
            changePriceColorState()
        }

        binding?.btnNormal?.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                priorityState = "normal"
                changePriorityColorState()
            }
        }

        binding?.btnImportant?.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                priorityState = "high"
                changePriorityColorState()
            }
        }

        binding?.rgMinimum?.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rbMinimum2 -> {
                    minimum_booking = "2"
                    changeMinimumBookingColor(minimum_booking ?: "2")
                }
                R.id.rbMinimum3 -> {
                    minimum_booking = "3"
                    changeMinimumBookingColor(minimum_booking ?: "3")
                }
                R.id.rbMinimum4 -> {
                    minimum_booking = "4"
                    changeMinimumBookingColor(minimum_booking ?: "4")
                }
                R.id.rbMinimum5 -> {
                    minimum_booking = "5"
                    changeMinimumBookingColor(minimum_booking ?: "5")
                }
                R.id.rbMinimum6 -> {
                    minimum_booking = "6"
                    changeMinimumBookingColor(minimum_booking ?: "6")
                }

            }
        }

        binding?.cbInsurance?.setOnClickListener {
            has_requested_insurance = if (binding?.cbInsurance!!.isChecked) {
                "1"
            } else {
                "0"
            }
        }

        binding?.tvPickUpAddress?.setOnClickListener {
            binding?.rvPickUpAddress?.visible()
            if (service.isNullOrEmpty()) {
                showToast("Please First Select Service")
            } else if (service?.equals("Flat Move") == true) {
                addPickupAddressDialog("forFlat")
            } else {
                addPickupAddressDialog("forHouse")
//                binding?.rvPickUpAddress?.gone()
//                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
//                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this)
            }


        }

        binding?.tvDropAddress?.setOnClickListener {

            if (service.isNullOrEmpty()) {
                showToast("Please First Select Service")
            } else if (service.equals("Flat Move")) {
                binding?.rvDropAddress?.visible()
                addDropAddressDialog("forFlat")
            } else {
                binding?.rvDropAddress?.gone()
                addDropAddressDialog("forHouse")
//                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
//                        .build(this)
//                startActivityForResult(intent, 223344)
            }
        }

        binding?.btnAdd?.setOnClickListener {
            validation(jobIdForEdit)

        }

        binding?.etFloorNo?.setOnClickListener {
            val vanDialog = AddVanDialog.newInstance { addVanOption, itemName, itemQuantity ->
                when (addVanOption) {
                    AddVanOption.DONE -> {
                        mFloorArray.add(
                            FloorAndFlatModel(
                                itemName, itemQuantity
                            )
                        )


                        binding?.rvFloors?.adapter = AddVanAdapter(this@AddBooking, mFloorArray)
                        binding?.rvFloors?.layoutManager =
                            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                        mVanAdapter?.notifyDataSetChanged()
                        val jsonVan = Gson().toJson(mFloorArray)
//                        showToast(jsonVan.toString())
                    }
                }
            }
            vanDialog.show(this.supportFragmentManager, "")


        }


        binding?.ivPlus?.setOnClickListener {
            jumboVanCount = jumboVanCount?.plus(1)
            binding?.jumboCount?.text = jumboVanCount.toString()
        }

        binding?.ivMinus?.setOnClickListener {
            if (jumboVanCount == 0) {
                binding?.jumboCount?.text = "0"
            } else {
                jumboVanCount = jumboVanCount?.minus(1)
                binding?.jumboCount?.text = jumboVanCount.toString()
            }
        }

        binding?.ivMWBPlus?.setOnClickListener {
            wmbVanCount = wmbVanCount?.plus(1)
            binding?.wmbCount?.text = wmbVanCount.toString()
        }

        binding?.ivMWBMinus?.setOnClickListener {
            if (wmbVanCount == 0) {
                binding?.wmbCount?.text = "0"
            } else {
                wmbVanCount = wmbVanCount?.minus(1)
                binding?.wmbCount?.text = wmbVanCount.toString()
            }
        }

        binding?.ivLuttonPlus?.setOnClickListener {
            lutonVanCount = lutonVanCount?.plus(1)
            binding?.luttonCount?.text = lutonVanCount.toString()
        }

        binding?.ivLuttonMinus?.setOnClickListener {
            if (lutonVanCount == 0) {
                binding?.luttonCount?.text = "0"
            } else {
                lutonVanCount = lutonVanCount?.minus(1)
                binding?.luttonCount?.text = lutonVanCount.toString()
            }
        }

        binding?.spService?.setOnItemSelectedListener { view, position, id, item ->
            when {
                item.toString().lowercase() == "Flat Move".lowercase() -> {
                    service = "Flat Move"
                    mPAddressArray.clear()
                    mDropAddressArray.clear()
                    setPickupAdapter(mPAddressArray)
                    setDropAddressAdapter(mDropAddressArray)
                    binding?.tvDropAddress?.text = ""
//                    binding?.tvPickUpAddress?.text = ""
//                    binding?.tvDropAddress?.text = ""
//                    binding?.tvPickUpAddress?.setCompoundDrawablesWithIntrinsicBounds(
//                            0,
//                            0,
//                            R.drawable.ic_baseline_add_circle,
//                            0
//                    )
                }
                item.toString().lowercase() == "Office Move".lowercase() -> {
                    service = "Office Move"
//                    mPAddressArray.clear()
//                    mDropAddressArray.clear()
//                    binding?.tvPickUpAddress?.setCompoundDrawablesWithIntrinsicBounds(
//                            0,
//                            0,
//                            R.drawable.ic_baseline_add_circle,
//                            0
//                    )
////                    setPickupAdapter(mPAddressArray)
//                    setDropAddressAdapter(mDropAddressArray)

                }
                item.toString().lowercase() == "House Move".lowercase() -> {
                    service = "House Move"
//                    mPAddressArray.clear()
//                    mDropAddressArray.clear()
//                    binding?.tvPickUpAddress?.setCompoundDrawablesWithIntrinsicBounds(
//                            0,
//                            0,
//                            R.drawable.ic_baseline_add_circle,
//                            0
//                    )
////                    setDropAddressAdapter(mDropAddressArray)
//                    setPickupAdapter(mPAddressArray)

                }
                item.toString().lowercase() == "Event Move".lowercase() -> {
                    service = "Event Move"
//                    mPAddressArray.clear()
//                    mDropAddressArray.clear()
//                    binding?.tvPickUpAddress?.setCompoundDrawablesWithIntrinsicBounds(
//                            0,
//                            0,
//                            R.drawable.ic_baseline_add_circle,
//                            0
//                    )
//                    setDropAddressAdapter(mDropAddressArray)
//                    setPickupAdapter(mPAddressArray)
                }
            }
        }

        binding?.spMen?.setOnItemSelectedListener { view, position, id, item ->

            noOfMens = if (item.toString().lowercase() == "Self Load".lowercase()) {
                "0"
            } else {
                item.toString()

            }

        }

        binding?.etFirstName?.setOnItemClickListener { adapterView, view, i, l ->
            val string = adapterView.getItemAtPosition(i)
            val data = hashmap.get(adapterView.getItemAtPosition(i))
            val d = mCustomerData.filter { it.email == data }


            secondName = d.firstOrNull()?.last_name
            email = d.firstOrNull()?.email
            phoneCode = d.firstOrNull()?.phone_code
            phoneNo = d.firstOrNull()?.phone_numb
            firstName = binding?.etFirstName?.text.toString()
            Log.d(
                "getData",
                "setupContentViewWithBinding: $d\n\n\n$data\n\n\n$firstName"
            )
            binding?.etLastName?.setText(secondName)
            binding?.etEmail?.setText(email)
            binding?.tvCountryCode?.text = phoneCode
            binding?.phoneNo?.setText(phoneNo)


        }

    }

    private fun changeDismantlingColor() {
        when (dismantling) {
            "1" -> {
                binding?.btnDismantlingYes?.isChecked = true
                binding?.btnDismantlingNo?.isChecked = false
                binding?.btnDismantlingYes?.buttonTintList = getColorStateList(R.color.blue)
                binding?.btnDismantlingNo?.buttonTintList =
                    getColorStateList(R.color.locationCol)
            }
            "0" -> {
                binding?.btnDismantlingNo?.isChecked = true
                binding?.btnDismantlingYes?.isChecked = false
                binding?.btnDismantlingNo?.buttonTintList = getColorStateList(R.color.blue)
                binding?.btnDismantlingYes?.buttonTintList =
                    getColorStateList(R.color.locationCol)

            }
        }
    }

    private fun changePackingColor() {
        when (packingService) {
            "1" -> {
                binding?.btnPackingServiceYes?.isChecked = true
                binding?.btnPackingServiceNo?.isChecked = false
                binding?.btnPackingServiceYes?.buttonTintList = getColorStateList(R.color.blue)
                binding?.btnPackingServiceNo?.buttonTintList =
                    getColorStateList(R.color.locationCol)
            }
            "0" -> {
                binding?.btnPackingServiceNo?.isChecked = true
                binding?.btnPackingServiceYes?.isChecked = false
                binding?.btnPackingServiceNo?.buttonTintList = getColorStateList(R.color.blue)
                binding?.btnPackingServiceYes?.buttonTintList =
                    getColorStateList(R.color.locationCol)

            }
        }
    }

    private fun addDropAddressDialog(s: String?) {
        mDropAddressArray.clear()
        val dialog = AddAddressDialog.newInstance(
            "Add Drop Address",
            "Save",
            "drop",
            s ?: ""
        ) { addAddressOption, address ->
            when (addAddressOption) {
                AddAddressOption.DONE -> {
                    mDropAddressArray.add(address)
                    setDropAddressAdapter(mDropAddressArray)

                }
            }
        }
        dialog.show(supportFragmentManager, "")
    }

    private fun setDropAddressAdapter(mDropAddressArray: ArrayList<PickupAddress>) {
        if (service.equals("Flat Move")) {
            if (mDropAddressArray?.firstOrNull()?.has_lift == "0") {
                binding?.tvDropAddress?.text =
                    mDropAddressArray?.firstOrNull()?.address1 + ", " + mDropAddressArray?.firstOrNull()?.pickup_flat_meta?.firstOrNull()?.floor_no + " Floor, Lift Available"
            } else {
                binding?.tvDropAddress?.text =
                    mDropAddressArray?.firstOrNull()?.address1 + ", " + mDropAddressArray?.firstOrNull()?.pickup_flat_meta?.firstOrNull()?.floor_no + " Floor, Lift Not Available"
            }

        } else {
            binding?.tvDropAddress?.text = mDropAddressArray?.firstOrNull()?.address1
        }
//        binding?.rvDropAddress?.adapter =
//                AddAddressAdapter(this@AddBooking, mDropAddressArray)
//        binding?.rvDropAddress?.layoutManager =
//                LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        mAddressAdapter?.notifyDataSetChanged()
        dropAddressString = Gson().toJson(mDropAddressArray)
    }


    private fun addPickupAddressDialog(s: String) {
        val dialog = AddAddressDialog.newInstance(
            "Add Pickup Address",
            "Save",
            "pickup",
            s
        ) { addAddressOption, address ->
            when (addAddressOption) {
                AddAddressOption.DONE -> {
                    mPAddressArray.add(address)
                    setPickupAdapter(mPAddressArray)
                }
            }
        }
        dialog.show(supportFragmentManager, "")
    }

    private fun setPickupAdapter(mPAddressArrayy: ArrayList<PickupAddress>) {
        binding?.rvPickUpAddress?.adapter =
            AddAddressAdapter(this@AddBooking, mPAddressArrayy) { position ->
                mPAddressArrayy.removeAt(position)
//                mAddressAdapter?.notifyItemRemoved(position)
//                mAddressAdapter?.notifyItemRangeRemoved(position, mPAddressArrayy.size)
                pickupAddressString = Gson().toJson(mPAddressArrayy)

                binding?.rvPickUpAddress?.adapter?.notifyDataSetChanged()
            }
        binding?.rvPickUpAddress?.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding?.rvPickUpAddress?.adapter?.notifyDataSetChanged()
        pickupAddressString = Gson().toJson(mPAddressArrayy)
    }


    private fun changeMinimumBookingColor(minimum_booking: String) {
        when (minimum_booking) {
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

    private fun checkPermissions() {
        Dexter.withContext(this@AddBooking)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {

                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // Remember to invoke this method when the custom rationale is closed
                    // or just by default if you don't want to use any custom rationale.
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
            }
            .check()
    }

    private fun setImageAdapter() {
        mImageAdapter =
            ImageUploadAdapter(this, mData) { position, forDelete ->
                if (forDelete) {
                    if (position == null) {
                        showBarToast("System Having Some Issue")
                    } else {
                        deleteImage(position)
                    }
                } else {
                    FilePickerBuilder.instance
                        .setMaxCount(5)
                        .setActivityTheme(R.style.LibAppTheme) //optional
                        .pickPhoto(this, IMAGE_REQUEST_CODE)


                }
                mImageAdapter?.notifyDataSetChanged()
            }

        binding?.rvImage?.layoutManager =
            GridLayoutManager(this, 3)
        binding?.rvImage?.adapter = mImageAdapter
        mImageAdapter?.notifyDataSetChanged()

    }

    private fun deleteImage(position: Int) {
        showLoading()
        NetworkClass.callApi(URLApi.deleteDocument(mMediaData, "booking"),
            object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    mMediaData.removeAt(position - 1)
                    mData.removeAt(position - 1)
                    addDocumentCall()
                    mImageAdapter?.notifyDataSetChanged()
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                }

            })

    }

    private fun addDocumentCall() {
        showLoading()
        NetworkClass.callFileUpload(URLApi.addDocument(nature = "booking"),
            mData, "uploadedFiles[]", object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    val json = JSONArray(response ?: "")
                    val data = generateList(json.toString(), Array<MediaModel>::class.java)
                    mMediaData.addAll(data)
                    uploaded_files = Gson().toJson(mMediaData)//mMediaData.toString()
                    Log.d("uploaded_files", uploaded_files?.toString() ?: " ")
                    AddBooking.scrollImageList.clear()
                    scrollImageList.addAll(mMediaData)
                    showBarToast(message)
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                    mData.clear()
                    mImageAdapter?.notifyDataSetChanged()
                }

            })

    }

    private fun setDateAndTime() {
        val startDate: Calendar = Calendar.getInstance()

        val dateTimeSelectedListener = object :

            OnDateTimeSelectedListener {
            override fun onDateTimeSelected(selectedDateTime: Calendar) {
                val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm")
                val date: Date = selectedDateTime.time
                val formatedDate = dateFormat.format(date)
                binding?.tvDateTime?.text = formatedDate.getDesiredOutputDateString(
                    "yyyy/MM/dd HH:mm",
                    "dd MMM yyyy HH:mm "
                )
                dateTime = formatedDate.getDesiredOutputDateString(
                    "yyyy/MM/dd HH:mm:ss",
                    "dd-MM-YYYY HH:mm:ss"
                )

//                showToast("Selected date ${selectedDateTime.time}")
                Log.d("Formate", formatedDate)
                Log.d("GetTime", selectedDateTime.time.toString())

            }

        }

        val dateTimePickerDialog = DialogDateTimePicker(
            this, //context
            startDate, //start date of calendar
            12, //No. of future months to shown in calendar
            dateTimeSelectedListener,
            "Select Date and Time"
        ) //Dialog title


        dateTimePickerDialog.setTitleTextColor(android.R.color.black)
        dateTimePickerDialog.setDividerBgColor(R.color.locationCol)


        dateTimePickerDialog.setCancelBtnTextColor(R.color.white)
        dateTimePickerDialog.setCancelBtnColor(R.color.blue)
        dateTimePickerDialog.setSubmitBtnColor(R.color.blue)
        dateTimePickerDialog.setSubmitBtnTextColor(R.color.white)


        dateTimePickerDialog.setCancelBtnText("Cancel")
        dateTimePickerDialog.setSubmitBtnText("OK")

        dateTimePickerDialog.setFontSize(14)
        dateTimePickerDialog.setCenterDividerHeight(38)

        dateTimePickerDialog.show()

    }


    private fun changeVanColorState(
        vanOne: RadioButton,
        vanTwo: RadioButton,
        vanThree: RadioButton,
        vanFour: RadioButton
    ) {
        when (vanCountState) {
            "1" -> {
                vanOne.isChecked = true
                vanThree.isChecked = false
                vanTwo.isChecked = false
                vanFour.isChecked = false
                vanOne.buttonTintList = getColorStateList(R.color.blue)
                vanTwo.buttonTintList = getColorStateList(R.color.locationCol)
                vanThree.buttonTintList = getColorStateList(R.color.locationCol)
                vanFour.buttonTintList = getColorStateList(R.color.locationCol)
            }
            "2" -> {
                vanOne.isChecked = false
                vanTwo.isChecked = true
                vanThree.isChecked = false
                vanFour.isChecked = false
                vanOne.buttonTintList = getColorStateList(R.color.locationCol)
                vanTwo.buttonTintList = getColorStateList(R.color.blue)
                vanThree.buttonTintList = getColorStateList(R.color.locationCol)
                vanFour.buttonTintList = getColorStateList(R.color.locationCol)
            }
            "3" -> {
                vanOne.isChecked = false
                vanThree.isChecked = true
                vanTwo.isChecked = false
                vanFour.isChecked = false
                vanOne.buttonTintList = getColorStateList(R.color.locationCol)
                vanTwo.buttonTintList = getColorStateList(R.color.locationCol)
                vanThree.buttonTintList = getColorStateList(R.color.blue)
                vanFour.buttonTintList = getColorStateList(R.color.locationCol)
            }
            "4" -> {
                vanOne.isChecked = false
                vanThree.isChecked = false
                vanTwo.isChecked = false
                vanFour.isChecked = true
                vanOne.buttonTintList = getColorStateList(R.color.locationCol)
                vanTwo.buttonTintList = getColorStateList(R.color.locationCol)
                vanThree.buttonTintList = getColorStateList(R.color.locationCol)
                vanFour.buttonTintList = getColorStateList(R.color.blue)
            }
        }
    }

    private fun changePriorityColorState() {
        when (priorityState) {
            "normal" -> {
                binding?.btnNormal?.isChecked = true
                binding?.btnImportant?.isChecked = false
                binding?.btnNormal?.buttonTintList = getColorStateList(R.color.blue)
                binding?.btnImportant?.buttonTintList = getColorStateList(R.color.locationCol)
            }
            "high" -> {
                binding?.btnNormal?.isChecked = false
                binding?.btnImportant?.isChecked = true
//                binding?.btnNotImportant?.isChecked = false
                binding?.btnImportant?.buttonTintList = getColorStateList(R.color.blue)
                binding?.btnNormal?.buttonTintList = getColorStateList(R.color.locationCol)
//                binding?.btnNotImportant?.buttonTintList = getColorStateList(R.color.locationCol)
            }
            "low" -> {
                binding?.btnNormal?.isChecked = false
                binding?.btnImportant?.isChecked = false
//                binding?.btnNotImportant?.isChecked = true
//                binding?.btnNotImportant?.buttonTintList = getColorStateList(R.color.blue)
                binding?.btnNormal?.buttonTintList = getColorStateList(R.color.locationCol)
                binding?.btnImportant?.buttonTintList = getColorStateList(R.color.locationCol)
            }
        }
    }

    private fun changePriceColorState() {
        if (priceNatureState == "fixed-price") {
            binding?.priceSign?.gone()
            binding?.btnFixedPrice?.isChecked = true
            binding?.btnHourlyPrice?.isChecked = false
            binding?.etPrice?.background = getDrawable(R.drawable.bg_right_radius)
            binding?.etPrice?.backgroundTintList = getColorStateList(R.color.bgEdit)

            binding?.btnFixedPrice?.buttonTintList = getColorStateList(R.color.blue)
            binding?.btnHourlyPrice?.buttonTintList = getColorStateList(R.color.locationCol)
        } else if (priceNatureState == "hourly-price") {
            binding?.priceSign?.visible()
            binding?.btnHourlyPrice?.isChecked = true
            binding?.btnFixedPrice?.isChecked = false
            binding?.etPrice?.background = getDrawable(R.drawable.button_without_radius)
            binding?.etPrice?.backgroundTintList = getColorStateList(R.color.bgEdit)
            binding?.btnHourlyPrice?.buttonTintList = getColorStateList(R.color.blue)
            binding?.btnFixedPrice?.buttonTintList = getColorStateList(R.color.locationCol)
        }
    }

    private fun validation(jobIdForEdit: String?) {

        firstName = binding?.etFirstName?.text.toString().trim()
        secondName = binding?.etLastName?.text.toString().trim()
        email = binding?.etEmail?.text.toString().trim()
        phoneNo = binding?.phoneNo?.text.toString().trim()
        advancePayment = binding?.etAdvancePayment?.text.toString().trim()
        phone_numb_2 = binding?.phoneNo2?.text.toString().trim()
        white_goods = binding?.etListOfGoods?.text.toString().trim()
        approx_bags_count = binding?.etNoOfBags?.text.toString().trim()
        approx_boxes_count = binding?.etNoOfBoxes?.text.toString().trim()
        packingFee = binding?.etPackingFee?.text.toString().trim()

        price = binding?.etPrice?.text.toString().trim()
        otherComments = binding?.tvDescription?.text.toString().trim()

        number = phoneCode + phoneNo
        phone_code_2 = "+92"
        phone_numb_2 = "00000"
        number2 = phone_code_2 + phone_numb_2

        vanArray.add(Vanmodel("Jumbo", jumboVanCount.toString()))
        vanArray.add(Vanmodel("MWB", wmbVanCount.toString()))
        vanArray.add(Vanmodel("Luton", lutonVanCount.toString()))

        jsonVan = Gson().toJson(vanArray)
        Log.d("VANSCOUNT", jsonVan.toString())

        if (packingService == "1") {
            if (packingFee.isNullOrEmpty()) {
                binding?.etPackingFee?.error = "Required"
            }
        } else if (packingService == "0") {
            packingFee = "0"
        }
        if (dateTime.isNullOrEmpty()) {
            showBarToast("Please Select Date & Time")
        } else if (firstName.isNullOrEmpty()) {
            binding?.etFirstName?.error = "Required"
        } else if (email.isNullOrEmpty()) {
            binding?.etEmail?.error = "Required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding?.etEmail?.text.toString())
                .matches()
        ) {
            showBarToast("Invalid Email Format")
        } else if (phoneNo.isNullOrEmpty()) {
            binding?.phoneNo?.error = "Required"
            showBarToast("Invalid Phone Number")
        }
//        else if (phoneNo!!.length != 7) {
//            binding?.phoneNo?.error = "Enter valid phone number."
//            showBarToast("Invalid Phone Number")
//        }
        else if (number?.length != 13) {
            showBarToast("Invalid Phone Number")
        } else if (service.isNullOrEmpty()) {
            showBarToast("Please Select Service")
        } else if (advancePayment.isNullOrEmpty()) {
            binding?.etAdvancePayment?.error = "Required"
        } else if (number?.length != 13) {
            showBarToast("Invalid Contact Number")
        } else if (priceNatureState.isNullOrEmpty()) {
            showBarToast("Please Select Price Nature")
        } else if (priceNatureState == "hourly-price" && minimum_booking.isNullOrEmpty()) {
            showBarToast("Please Select Minimum Booking Time")
        } else if (noOfMens.isNullOrEmpty()) {
            showBarToast("Please Select No of Mens Required")
        } else if (priorityState.isNullOrEmpty()) {
            showBarToast("Please Select Priority")
        } else if (price.isNullOrEmpty()) {
            binding?.etPrice?.error = "Required"
        } else if (pickupAddressString.isNullOrEmpty()) {
            showBarToast("Please Add Pickup Address")
        } else if (dropAddressString.isNullOrEmpty()) {
            showBarToast("Please Add Drop Address")
        } else if (packingService.isNullOrEmpty()) {
            showBarToast("Do you wanna add Packing Service?")
        } else if (dismantling.isNullOrEmpty()) {
            showBarToast("Do you wanna add Dismantling?")
        } else {
            callAddBooking(
                jobIdForEdit ?: "",
                dateTime.toString().trim(),
                firstName.toString().trim(),
                secondName.toString().trim(),
                email.toString().trim(),
                phoneCode.toString().trim(),
                phoneNo.toString().trim(),
                service.toString().trim(),
                advancePayment.toString().trim(),
                location_id_GOOGLE.toString().trim(),
                locationName.toString().trim(),
                location_address.toString().trim(),
                location_city.toString().trim(),
                location_state.toString().trim(),
                location_country.toString().trim(),
                location_postal_code.toString().trim(),
                location_lat.toString().trim(),
                location_lng.toString().trim(),
                priceNatureState.toString().trim(),
                price.toString().trim(),
                priorityState.toString().trim(),
                otherComments.toString().trim(),
                floor_no.toString().trim(),
                phone_code_2.toString().trim(),
                phone_numb_2.toString().trim(),
                white_goods?.toString()?.trim() ?: "",
                approx_bags_count?.toString()?.trim() ?: "",
                approx_boxes_count?.toString()?.trim() ?: "",
                has_requested_insurance,
                drop_location_id_GOOGLE,
                drop_locationName,
                drop_location_address,
                drop_location_city,
                drop_location_state,
                drop_location_country,
                drop_location_postal_code,
                drop_location_lat,
                drop_location_lng,
                noOfMens ?: "",
                minimum_booking ?: "",
                jsonVan ?: "",
                packingFee ?: "",
                packingService ?: "",
                dismantling ?: ""
            )
        }


    }

    private fun callAddBooking(
        jobId: String,
        dateTime: String,
        firstName: String,
        secondName: String,
        email: String,
        phoneCode: String,
        phoneNo: String,
        service: String,
        advancePayment: String,
        locationId: String,
        locationName: String,
        pickupAddress: String,
        city: String,
        state: String,
        country: String,
        postalCode: String,
        lat: String,
        long: String,
        priceNatureState: String,
        price: String,
        priorityState: String,
        otherComments: String,
        floorNo: String,
        phoneCode2: String,
        phoneNo2: String,
        whiteGoods: String,
        approxBagsCount: String,
        approxBoxesCount: String,
        hasRequestedInsurance: String?,
        drop_location_id_GOOGLE: String,
        drop_locationName: String,
        drop_location_address: String,
        drop_location_city: String,
        drop_location_state: String,
        drop_location_country: String,
        drop_location_postal_code: String,
        drop_location_lat: String,
        drop_location_lng: String,
        mens: String,

        minimum_booking: String,
        vans: String,
        packingFee: String,
        is_packing_service: String,
        is_any_dismantling: String,
    ) {
        showLoading()
        NetworkClass.callApi(
            URLApi.addBooking(
                job_uuid = jobId,
                booker_uuid = LocalPreference.shared.user?.user?.profile?.uuid,
                worker_uuid = null,
                first_name = firstName,
                last_name = secondName,
                email = email,
                phone_code = phoneCode,
                phone_numb = phoneNo,
                priority = priorityState,
                price_nature = priceNatureState,
                price_amount = price,
                advance_amount = advancePayment,
                service = service,
                "0",
                job_status = "pending",
                start_time = dateTime,
                additional_details = otherComments,
                white_goods = whiteGoods,
                approx_boxes_count = approxBoxesCount,
                approx_bags_count = approxBagsCount,
                floor_no = "1",
                has_requested_insurance = has_requested_insurance,
                "1",
                phone_code_2 = phoneCode2,
                phone_numb_2 = phoneNo2,
                worker_remarks = null,
                signature_media = null,
                pickup_addresses = pickupAddressString,
                drop_addresses = dropAddressString,

                has_lift = has_lift,
                uploaded_files = uploaded_files,
                men_count = mens,
                min_hours_count = minimum_booking,
                vans_data = jsonVan,
                packing_fee = packingFee,
                is_packing_service = is_packing_service,
                is_any_dismantling = is_any_dismantling
                //                pickup_places_id = locationId,
//                pickup_address_title = locationName,
//                pickup_address1 = pickupAddress,
//                pickup_city = city,
//                pickup_state = state,
//                pickup_country = country,
//                pickup_post_code = null,
//                pickup_lat = lat,
//                pickup_lng = long,
//                drop_places_id = drop_location_id_GOOGLE,
//                drop_address_title = drop_locationName,
//                drop_address1 = drop_location_address,
//                drop_city = drop_location_city,
//                drop_state = drop_location_state,
//                drop_country = drop_location_country,
//                drop_post_code = null,
//                drop_lat = drop_location_lat,
//                drop_lng = drop_location_lng,
            ), object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()

                    val json = JSONObject(response ?: "")
                    val obj = Gson().fromJson(json.toString(), JobModel::class.java)

                    if (jobIdForEdit?.isNullOrEmpty() == false) {
                        showBarToast("Booking Updated Successfully")
                        val intent = Intent(this@AddBooking, JobsListing::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        showBarToast("Booking Created Successfully")
                        setDataForSMS(obj)
                        val dialog = SendConfirmationDialog.newInstance {
                            when (it) {
                                ConfirmationOption.SMS -> {
                                    sendSMS(finish())
                                }
                                ConfirmationOption.EMAIL -> {
                                 //   resendEmailDialog()
                                    sendEmail(finish(), obj?.uuid)
                                }
                                ConfirmationOption.CANCEL -> {
                                    finish()
                                }
                            }
                        }
                        dialog.show(supportFragmentManager, "")
                    }
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                }

            }
        )
    }

    private fun sendEmail(finish: Unit, uuid: String?) {
        showLoading()
        NetworkClass.callApi(URLApi.notifyByEmail(
            job_uuid = uuid
        ), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                showBarToast(message)
               // Toast.makeText(binding?.root?.context, "response"+response, Toast.LENGTH_SHORT).show()
                Log.i("Res","reposne "+response)
//                resendEmailDialog()
                finish
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
              //  Toast.makeText(binding?.root?.context, "error"+response, Toast.LENGTH_SHORT).show()

                showBarToast(error ?: "")
                Log.i("Res","error "+response)

            }

        })
    }

    private fun setDataForSMS(obj: JobModel?) {
        val fullName = obj?.first_name + " " + obj?.last_name
        val jobId = obj?.uuid
        val startTime = obj?.start_time?.zDateConvertor(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", " dd MMMM, YYYY", obj.start_time
        )
        val time = obj?.start_time?.zDateConvertor(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "HH:mm", obj.start_time
        )
        val number = obj?.price_amount?.toDoubleOrNull() ?: 0.0
        val rounded = String.format("%.2f", number)
        val amountRounded = rounded

        var address: String? = null
        var dropAddress: String? = null

        if (obj?.service == "Flat Move") {
            val newArray = ArrayList<AdressListingModel>()
            val newDropArray = ArrayList<AdressListingModel>()
            newArray.clear()
            newDropArray.clear()
            obj.pickup_addresses?.forEachIndexed { index, pickupAddress ->
                if (pickupAddress.has_lift.equals("1")) {
                    val s =
                        pickupAddress.address1 + ", Floor No: " + pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no + " Lift Available \n"
                    newArray.add(AdressListingModel(s))
                } else {
                    val s =
                        pickupAddress.address1 + ", Floor No: " + pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no + " Lift UnAvailable \n"
                    newArray.add(AdressListingModel(s))
                }

            }

            obj.drop_addresses?.forEachIndexed { index, pickupAddress ->
                if (pickupAddress.has_lift.equals("1")) {
                    val s =
                        pickupAddress.address1 + pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no + " Floor with Lift \n"
                    newDropArray.add(AdressListingModel(s))
                } else {
                    val s =
                        pickupAddress.address1 + pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no + " Floor with No-Lift \n"
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

            address = finalAddress?.replace("null", "")
            dropAddress = newDropAddress?.replace("null", "")

        } else {

            address = obj?.pickup_address?.address1
            dropAddress = obj?.drop_address?.address1
        }

        val detail = obj?.additional_details
        var info: String? = ""
        val string = obj?.van_meta?.map { it.qnty + " " + it.model_name }
        val vanDetail = string?.joinToString(
            ", ",
            "",
            "",
            obj.van_meta.size,
            "",
            transform = { it.uppercase(Locale.getDefault()) })
        info = if (obj?.men_count == "0") {
            "Self Loaded"
        } else {
            obj?.men_count + " Men"
        }


        var termsText: String? = null
        termsText = if (obj?.price_nature == "fixed-price") {
            "£ $amountRounded fixed booking. Cash will be collected before offloading."
        } else {
            "£ $amountRounded/hr " + obj?.min_hours_count + " hours minimum booking. Cash will be collected before offloading."
        }


        val bookingNo = obj?.id
        smsContent = "Hello $fullName, \n \n" +
                "Booking #$bookingNo \n\n" +
                "Your removals is scheduled on the $startTime at $time \n\n" +
                "From: $address \n\n" +
                "To: $dropAddress \n\n" +
                "Detail: $vanDetail & $info \n\n" +
                "Notes: $detail \n\n" +
                "Terms: $termsText \n\n" +
                "Cheers!"

    }

//    private fun sendSMS(finish: Unit) {
////        val no = phoneCode + phoneNo
//        val no = "+91" + phoneNo
//        val uri = Uri.parse("smsto:$no")
//        val SENT = "SMS_SENT"
//        val DELIVERED = "SMS_DELIVERED"
//        val sentPI: PendingIntent = PendingIntent.getBroadcast(
//            this, 0,
//            Intent(SENT), 0
//        )
//        val deliveredPI: PendingIntent = PendingIntent.getBroadcast(
//            this, 0,
//            Intent(DELIVERED), 0
//        )
//
//        //---when the SMS has been sent---
//        registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(arg0: Context?, arg1: Intent?) {
//                when (getResultCode()) {
//                    RESULT_OK -> Toast.makeText(
//                        baseContext, "SMS sent",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
//                        baseContext, "Generic failure",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
//                        baseContext, "No service",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(
//                        baseContext, "Null PDU",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
//                        baseContext, "Radio off",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }, IntentFilter(SENT))
//
//        //---when the SMS has been delivered---
//        registerReceiver(object : BroadcastReceiver() {
//            override fun onReceive(arg0: Context?, arg1: Intent?) {
//                when (getResultCode()) {
//                    RESULT_OK -> Toast.makeText(
//                        baseContext, "SMS delivered",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    RESULT_CANCELED -> Toast.makeText(
//                        baseContext, "SMS not delivered",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }, IntentFilter(DELIVERED))
//        val sms = SmsManager.getDefault()
//        sms.sendTextMessage(no, null, smsContent, sentPI, deliveredPI)
//    }

    private fun sendSMS(finish: Unit) {
        val no = "+44" + phoneNo
        val uri = Uri.parse("smsto:$no")
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(no, null, smsContent, null, null)
//            Toast.makeText(
//                applicationContext, "Message Sent",
//                Toast.LENGTH_LONG
//            ).show()
        } catch (ex: Exception) {
            Toast.makeText(
                applicationContext, ex.message.toString(),
                Toast.LENGTH_LONG
            ).show()
            ex.printStackTrace()
            Log.i("SMS", ex.toString());
        }
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra("sms_body", smsContent)
        startActivity(intent)
        return finish
    }


    private fun initTopBar() {

        binding?.topBar?.ivLogout?.gone()
        profileType = LocalPreference.shared.user?.user?.profile_type

        jobIdForEdit = intent.getStringExtra("forEdit")

        if (jobIdForEdit?.isNotEmpty() == true) {
            binding?.topBar?.tvText?.text = "Edit Booking"
            getJobDetail(jobIdForEdit ?: "")
        } else {
            binding?.topBar?.tvText?.text = "Add Bookings"
        }

        getCustomerListing()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mCustomerName)
        binding?.etFirstName?.setAdapter(adapter)

        binding?.spService?.setItems(
            "House Move",
            "Event Move",
            "Office Move",
            "Flat Move"
        )

        binding?.spMen?.setItems(
            "Self Load",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
        )


    }

    private fun getJobDetail(jobId: String) {
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

    private fun setJobDetail(obj: JobModel?) {
        if (obj?.medias?.isEmpty() == true) {
            binding?.tvDiamantling?.gone()
            binding?.rvImage?.gone()
        } else {

            val mMedia = obj?.medias
            val mAdapter = ImageListingAdapter(this, mMedia ?: ArrayList())
            binding?.rvImage?.layoutManager = GridLayoutManager(this, 3)
            binding?.rvImage?.adapter = mAdapter
            mAdapter.notifyDataSetChanged()
        }

        binding?.tvDateTime?.text = obj?.start_time?.zDateConvertor(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "dd-MM-yyyy HH:mm:ss",
            obj?.start_time
        )

        dateTime = binding?.tvDateTime?.text.toString()
        binding?.etFirstName?.setText(obj?.first_name)
        binding?.etLastName?.setText(obj?.last_name)
        binding?.etEmail?.setText(obj?.email)
        binding?.phoneNo?.setText(obj?.phone_numb)
        binding?.spService?.text = obj?.service
        service = obj?.service

        binding?.spService?.setTextColor(getColorStateList(R.color.textColor))
        binding?.tvDropAddress?.text = obj?.drop_address?.address1
        binding?.etListOfGoods?.setText(obj?.white_goods)
        binding?.etNoOfBags?.setText(obj?.approx_bags_count)
        binding?.etNoOfBoxes?.setText(obj?.approx_boxes_count)
        binding?.spMen?.text = obj?.men_count
        noOfMens = obj?.men_count
        binding?.spMen?.setTextColor(getColorStateList(R.color.textColor))
        binding?.cbInsurance?.isChecked = obj?.has_requested_insurance == "1"
        binding?.etPackingFee?.setText(obj?.packing_fee)
        binding?.tvDescription?.setText(obj?.additional_details)

        val advanceAmount = obj?.advance_amount?.toDoubleOrNull() ?: 0.0
        val advanceAmountRounded = String.format("%.2f", advanceAmount)
        binding?.etAdvancePayment?.setText(advanceAmountRounded)
        val priceAmount = obj?.advance_amount?.toDoubleOrNull() ?: 0.0
        val priceAmountRounded = String.format("%.2f", priceAmount)
        binding?.etPrice?.setText(priceAmountRounded)

        if (obj?.price_nature?.lowercase() == "fixed-price".lowercase()) {
            binding?.priceSign?.gone()
            binding?.llMinimumBooking?.gone()
            binding?.btnFixedPrice?.isChecked = true
            binding?.btnHourlyPrice?.isChecked = false
            binding?.btnFixedPrice?.buttonTintList = getColorStateList(R.color.blue)
            binding?.btnHourlyPrice?.buttonTintList = getColorStateList(R.color.locationCol)
        } else {
            binding?.priceSign?.visible()
            binding?.llMinimumBooking?.visible()
            setMinimumButtonColorState(obj?.min_hours_count)
            binding?.btnFixedPrice?.isChecked = false
            binding?.btnHourlyPrice?.isChecked = true
            binding?.btnFixedPrice?.buttonTintList = getColorStateList(R.color.locationCol)
            binding?.btnHourlyPrice?.buttonTintList = getColorStateList(R.color.blue)
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

        obj?.van_meta?.forEachIndexed { index, responseVanModel ->
            when (responseVanModel.model_name) {
                "Luton" -> {
                    lutonVanCount = responseVanModel.qnty?.toInt() ?: 0
                    binding?.luttonCount?.text = lutonVanCount.toString()
                }
                "MWB" -> {
                    wmbVanCount = responseVanModel.qnty?.toInt() ?: 0
                    binding?.wmbCount?.text = wmbVanCount.toString()
                }
                "Jumbo" -> {
                    jumboVanCount = responseVanModel.qnty?.toInt() ?: 0
                    binding?.jumboCount?.text = jumboVanCount.toString()
                }
            }
        }

        mPAddressArray.clear()
        mDropAddressArray.clear()

//        val newArray: ArrayList<PickupAddress> = ArrayList()
//        newArray.addAll(obj?.pickup_addresses!!)

        mPAddressArray.addAll(obj?.pickup_addresses!!)
        mDropAddressArray.addAll(obj?.drop_addresses!!)
        setPickupAdapter(mPAddressArray)
        setDropAddressAdapter(mDropAddressArray)

        if (obj.is_packing_service == "0") {
            binding?.btnPackingServiceNo?.isChecked = true
            binding?.btnPackingServiceYes?.isChecked = false
            binding?.btnPackingServiceYes?.buttonTintList = getColorStateList(R.color.locationCol)
            binding?.btnPackingServiceNo?.buttonTintList = getColorStateList(R.color.blue)
            binding?.rlPackingFee?.gone()
            packingService = "0"


        } else {
            binding?.btnPackingServiceYes?.isChecked = true
            binding?.btnPackingServiceNo?.isChecked = false
            binding?.rlPackingFee?.visible()
            packingService = "1"
            binding?.btnPackingServiceNo?.buttonTintList = getColorStateList(R.color.locationCol)
            binding?.btnPackingServiceYes?.buttonTintList = getColorStateList(R.color.blue)
        }

        if (obj?.is_any_dismantling == "0") {
            binding?.btnDismantlingNo?.isChecked = true
            binding?.btnDismantlingYes?.isChecked = false
            dismantling = "0"
            binding?.btnDismantlingYes?.buttonTintList = getColorStateList(R.color.locationCol)
            binding?.btnDismantlingNo?.buttonTintList = getColorStateList(R.color.blue)
        } else {
            binding?.btnDismantlingYes?.isChecked = true
            binding?.btnDismantlingNo?.isChecked = false
            dismantling = "1"
            binding?.btnDismantlingNo?.buttonTintList = getColorStateList(R.color.locationCol)
            binding?.btnDismantlingYes?.buttonTintList = getColorStateList(R.color.blue)
        }

        binding?.btnAdd?.setOnClickListener {
            validation(jobIdForEdit ?: "")
        }


    }

    private fun setMinimumButtonColorState(minHoursCount: String?) {
        when (minHoursCount) {
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

    private fun getCustomerListing() {
//        showLoading()
        NetworkClass.callApi(URLApi.getCustomerList(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
//                hideLoading()
                mData.clear()
                val json = JSONObject(response ?: "")
                val json2 = json.optJSONArray("models")
                var data = generateList(json2.toString(), Array<CustomerModel>::class.java)
                mCustomerData.addAll(data)
                mCustomerName.addAll(mCustomerData.map { it.first_name ?: "" })
                mCustomerData.forEachIndexed { index, customerModel ->
                    hashmap.put(customerModel.first_name ?: "", customerModel.email ?: "")

                }
//                hideLoading()
            }

            override fun onErrorResponse(error: String?, response: String?) {
//                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }


    fun resendEmailDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.resendemail)
        val resendEmail = dialog.findViewById<Button>(R.id.btnResenEmail)
        val cancelBtn = dialog.findViewById<Button>(R.id.cancelBtn)
        val crossImg = dialog.findViewById<AppCompatImageView>(R.id.ivCross)
        resendEmail.setOnClickListener { // App.getSharedPre().userLogOut();
            dialog.dismiss()
        }
        crossImg.setOnClickListener {
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener { dialog.dismiss() }
        dialog.setCancelable(false)
        dialog.show()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setGravity(Gravity.CENTER)


//        val builder1 = AlertDialog.Builder(binding?.root?.context)
//        builder1.setTitle("Resend Email")
//        builder1.setMessage("Are you sure you want to resend  ")
//        builder1.setCancelable(false)
////        builder1.setPositiveButton(
////            "Yes"
////        ) { dialog, which -> dialog.cancel() }
//        builder1.setNegativeButton(
//            "No"
//        ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
//        val alert11 = builder1.create()
//        alert11.show()

    }


    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {

    }

    override fun setupLoader() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_REQUEST_CODE && data != null) {
            val array = ArrayList<Uri>()
            data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)?.let {
                array.addAll(
                    it
                )
            }
            AsyncTask.THREAD_POOL_EXECUTOR.execute {
                array.forEachIndexed { index, uri ->
                    val file =
                        com.kodextech.project.kodexlib.dialog.FileUtilityClass.getFileFromUri(
                            this@AddBooking,
                            uri
                        )
                    mData.add(file!!)
                }

                this.runOnUiThread {
                    addDocumentCall()
                    mImageAdapter?.notifyDataSetChanged()

                }

            }
            profile_image_selected?.addAll(array)
            Log.d("ProfileTAG", array.toString())
        }
        if (resultCode == RESULT_OK && requestCode == 22345) {
            val locationObj = data?.getSerializableExtra("locationObj") as LocationDataModel

            location_id = locationObj.location_id ?: ""
            locationPickupID = locationObj.location_id ?: ""
            locationName = locationObj.location_name ?: ""
            location_lat = locationObj.location_lat ?: ""
            location_lng = locationObj.location_lng ?: ""

            binding?.tvPickUpAddress?.text = locationObj.location_name

        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(
                            "TAG",
                            "Place: ${place.name}, ${place.id}, ${place.address}, ${place.latLng}"
                        )

                        location_id_GOOGLE = place.id ?: ""
                        locationName = place.name ?: ""
                        location_lat = place.latLng?.latitude.toString()
                        location_lng = place.latLng?.longitude.toString()
                        location_address = place.address.toString()
                        binding?.tvPickUpAddress?.movementMethod = ScrollingMovementMethod()
//                        binding?.tvPickUpAddress?.isSelected = true
                        binding?.tvPickUpAddress?.text = place.address

                        val latitude: Double = place.latLng?.latitude ?: 0.0
                        val longitude: Double = place.latLng?.longitude ?: 0.0

                        try {
                            val geocoder = Geocoder(this@AddBooking, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                            if (addresses != null && addresses.size > 0) {
                                val address =
                                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                val city = addresses[0].locality
                                val state = addresses[0].adminArea
                                val country = addresses[0].countryName
                                //     val knownName = addresses[0].featureName // Only if available else return NULL
                                Log.d(TAG, "getAddress:  address$address")
                                Log.d(TAG, "getAddress:  city$city")
                                Log.d(TAG, "getAddress:  state$state")
//                                Log.d(TAG, "getAddress:  knownName$knownName")

                                location_city = city ?: ""
                                location_state = state ?: ""
                                location_country = country ?: ""
                                Log.d(
                                    "AddressFormat",
                                    place.address + "\n" + place.name + "\n" + location_city + "\n" + location_state + "\n" + location_country
                                )

                                val flatData = ArrayList<FlatData>()
                                flatData.clear()
                                mPAddressArray.clear()
                                val data = PickupAddress()
                                flatData.add(FlatData("0", "0"))
                                data.address1 = place.address.toString()
                                data.city = city
                                data.country = country
                                data.has_lift = "0"
                                data.id = locationPickupID?.toInt()
                                data.flats_data = flatData
                                data.lat = place.latLng?.latitude.toString()
                                data.lng = place.latLng?.longitude.toString()
                                data.places_id = place.id ?: ""
                                mPAddressArray.add(data)
                                binding?.rvPickUpAddress?.gone()
                                setPickupAdapter(mPAddressArray)


                            } else {
                                showBarToast("Error For Getting Address Details ... ")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }


                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("TAG", status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        } else if (requestCode == 223344) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(
                            "TAG",
                            "Place: ${place.name}, ${place.id}, ${place.address}, ${place.latLng}"
                        )

                        drop_location_id_GOOGLE = place.id ?: ""
                        drop_locationName = place.name ?: ""
                        drop_location_lat = place.latLng?.latitude.toString()
                        drop_location_lng = place.latLng?.longitude.toString()
                        drop_location_address = place.address.toString()
                        binding?.tvDropAddress?.movementMethod = ScrollingMovementMethod()
                        binding?.tvDropAddress?.text = place.address



                        Log.d("DropAdress", place.address)

                        val latitude: Double = place.latLng?.latitude ?: 0.0
                        val longitude: Double = place.latLng?.longitude ?: 0.0

                        try {
                            val geocoder = Geocoder(this@AddBooking, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                            if (addresses != null && addresses.size > 0) {
                                val address =
                                    addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                val city = addresses[0].locality
                                val state = addresses[0].adminArea
                                val country = addresses[0].countryName

                                //     val knownName = addresses[0].featureName // Only if available else return NULL
                                Log.d(TAG, "getDropAddress:  address$address")
                                Log.d(TAG, "getDropAddress:  city$city")
                                Log.d(TAG, "getDropAddress:  state$state")
                                //Log.d(TAG, "getAddress:  knownName$knownName")

                                drop_location_city = city ?: ""
                                drop_location_state = state ?: ""
                                drop_location_country = country ?: ""

                                val flatData = ArrayList<FlatData>()
                                mDropAddressArray.clear()
                                flatData.clear()
                                var data = PickupAddress()
                                flatData.add(FlatData("0", "0"))
                                data.address1 = place.address.toString()
                                data.city = city ?: ""
                                data.country = country ?: ""
                                data.has_lift = "0"
                                data.id = locationPickupID?.toInt()
                                data.flats_data = flatData
                                data.lat = place.latLng?.latitude ?: 0.0
                                data.lng = place.latLng?.longitude ?: 0.0
                                data.places_id = place.id ?: ""
                                mDropAddressArray.add(data)
                                binding?.rvDropAddress?.gone()
                                setDropAddressAdapter(mDropAddressArray)


                            } else {
                                showBarToast("Error For Getting Address Details ... ")
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }


                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("TAG", status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun setMandatoryHintData(hintData: String): SpannableStringBuilder? {
        val colored = " *"
        val builder = SpannableStringBuilder()
        builder.append(hintData)
        val start: Int = builder.length
        builder.append(colored)
        val end: Int = builder.length
        builder.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.red)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return builder
    }

}


