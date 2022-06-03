package com.kodextech.project.kodexlib.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Point
import android.location.Geocoder
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseDialogueFragment
import com.kodextech.project.kodexlib.databinding.AddAddressDialogBinding
import com.kodextech.project.kodexlib.model.FlatData
import com.kodextech.project.kodexlib.model.LocationDataModel
import com.kodextech.project.kodexlib.model.PickupAddress
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class AddAddressDialog : BaseDialogueFragment() {

    private var binding: AddAddressDialogBinding? = null
    private var title: String? = ""
    private var IS_FOR: String? = ""
    private var useFor: String? = ""
    private var btnSubmitTitle: String? = ""
    private var addressData: PickupAddress? = null
    private var onClicksCallBack: ((AddAddressOption, address: PickupAddress) -> Unit)? =
            null
    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    var flatobj: PickupAddress? = null
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
    private var has_lift: String = ""
    private var floor_no: String = ""
    private var liftState: String = ""


    override fun onSetupArguments() {



        arguments.let {
            this.title = it?.getString("TITLE", "") ?: ""
            this.btnSubmitTitle = it?.getString("YES_TITLE", "") ?: ""
            this.IS_FOR = it?.getString("IS_FOR", "") ?: ""
            this.useFor = it?.getString("useFor", "") ?: ""
        }
        isCancelable = true



    }

    override fun onBindItemListenerOrViewVariables() {

    }

    override fun setupContentViewWithBinding(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_address_dialog, container, false)

        binding?.tvPickUpAddress?.setOnClickListener {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(mActivity)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        binding?.tvLabelAddAddress?.text = title

                            if (IS_FOR == "drop"){
                binding?.tvPickUpAddress!!.hint="Drop Address"

            }
            else{
                binding?.tvPickUpAddress!!.hint="Pickup Address"
            }



        binding?.rgLift?.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.btnLiftYes -> {
                    has_lift = "1"
                    ChangeLiftColorState()
                }
                R.id.btnLiftNo -> {
                    has_lift = "0"
                    ChangeLiftColorState()
                }
            }
        }

        binding?.rgFloor?.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rbBasement -> {
                    floor_no = "-1"
                    ChangeFloorColorState()
                }
                R.id.rbGround -> {
                    floor_no = "0"
                    ChangeFloorColorState()
                }
                R.id.rbFirstFloor -> {
                    floor_no = "1"
                    ChangeFloorColorState()
                }
                R.id.rbSecondFloor -> {
                    floor_no = "2"
                    ChangeFloorColorState()
                }
                R.id.rbThirdFloor -> {
                    floor_no = "3"
                    ChangeFloorColorState()
                }
                R.id.rbFourFloor -> {
                    floor_no = "4"
                    ChangeFloorColorState()
                }
            }
        }


        if (useFor.equals("forFlat")) {
            binding?.llFloor?.visible()
            binding?.llLift?.visible()
        } else {
            floor_no = "-2"
            has_lift = "-2"
            binding?.llFloor?.gone()
            binding?.llLift?.gone()
        }

        binding?.btnSave?.setOnClickListener {

            val flatData = ArrayList<FlatData>()
            flatData.clear()
            val data = PickupAddress()
            if (location_id_GOOGLE.isNullOrEmpty()) {
                mActivity.showToast("Please Enter Address")
            } else if (floor_no.isNullOrEmpty()) {
                mActivity.showToast("Please Select Floor No")
            } else {
                flatData.add(FlatData(floor_no, "0"))
                data.address1 = location_address
                data.city = location_city
                data.country = location_country
                data.has_lift = has_lift
                data.id = location_id.toIntOrNull()
                data.pickup_flat_meta = flatData
                data.drop_flat_meta = flatData

                data.flats_data = flatData
                data.lat = location_lat
                data.lng = location_lng
                data.places_id = location_id_GOOGLE
                flatobj = data
                onClicksCallBack?.invoke(
                        AddAddressOption.DONE, flatobj ?: PickupAddress()
                )
                Log.d("XYXA", flatobj.toString())
                dismiss()
            }
        }

        return binding?.root!!
    }

    private fun ChangeLiftColorState() {
        when (has_lift) {
            "1" -> {
                binding?.btnLiftYes?.isChecked = true
                binding?.btnLiftNo?.isChecked = false
                binding?.btnLiftYes?.buttonTintList = mActivity.getColorStateList(R.color.blue)
                binding?.btnLiftNo?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)

            }
            "0" -> {
                binding?.btnLiftYes?.isChecked = false
                binding?.btnLiftNo?.isChecked = true
                binding?.btnLiftNo?.buttonTintList = mActivity.getColorStateList(R.color.blue)
                binding?.btnLiftYes?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)

            }
        }

    }

    private fun ChangeFloorColorState() {
        when (floor_no) {
            "-1" -> {
                binding?.rbBasement?.isChecked = true
                binding?.rbGround?.isChecked = false
                binding?.rbFirstFloor?.isChecked = false
                binding?.rbSecondFloor?.isChecked = false
                binding?.rbThirdFloor?.isChecked = false
                binding?.rbFourFloor?.isChecked = false
                binding?.rbBasement?.buttonTintList = mActivity.getColorStateList(R.color.blue)
                binding?.rbGround?.buttonTintList = mActivity.getColorStateList(R.color.locationCol)
                binding?.rbFirstFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbSecondFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbThirdFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbFourFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)

            }
            "0" -> {
                binding?.rbBasement?.isChecked = false
                binding?.rbGround?.isChecked = true
                binding?.rbFirstFloor?.isChecked = false
                binding?.rbSecondFloor?.isChecked = false
                binding?.rbThirdFloor?.isChecked = false
                binding?.rbFourFloor?.isChecked = false
                binding?.rbBasement?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbGround?.buttonTintList = mActivity.getColorStateList(R.color.blue)
                binding?.rbFirstFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbSecondFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbThirdFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbFourFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)

            }
            "1" -> {
                binding?.rbBasement?.isChecked = false
                binding?.rbGround?.isChecked = false
                binding?.rbFirstFloor?.isChecked = true
                binding?.rbSecondFloor?.isChecked = false
                binding?.rbThirdFloor?.isChecked = false
                binding?.rbFourFloor?.isChecked = false
                binding?.rbBasement?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbGround?.buttonTintList = mActivity.getColorStateList(R.color.locationCol)
                binding?.rbFirstFloor?.buttonTintList = mActivity.getColorStateList(R.color.blue)
                binding?.rbSecondFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbThirdFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbFourFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)

            }
            "2" -> {
                binding?.rbBasement?.isChecked = false
                binding?.rbGround?.isChecked = false
                binding?.rbFirstFloor?.isChecked = false
                binding?.rbSecondFloor?.isChecked = true
                binding?.rbThirdFloor?.isChecked = false
                binding?.rbFourFloor?.isChecked = false
                binding?.rbBasement?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbGround?.buttonTintList = mActivity.getColorStateList(R.color.locationCol)
                binding?.rbFirstFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbSecondFloor?.buttonTintList = mActivity.getColorStateList(R.color.blue)
                binding?.rbThirdFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbFourFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)

            }
            "3" -> {
                binding?.rbBasement?.isChecked = false
                binding?.rbGround?.isChecked = false
                binding?.rbFirstFloor?.isChecked = false
                binding?.rbSecondFloor?.isChecked = false
                binding?.rbThirdFloor?.isChecked = true
                binding?.rbFourFloor?.isChecked = false
                binding?.rbBasement?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbGround?.buttonTintList = mActivity.getColorStateList(R.color.locationCol)
                binding?.rbFirstFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbSecondFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbThirdFloor?.buttonTintList = mActivity.getColorStateList(R.color.blue)
                binding?.rbFourFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)

            }
            "4" -> {
                binding?.rbBasement?.isChecked = false
                binding?.rbGround?.isChecked = false
                binding?.rbFirstFloor?.isChecked = false
                binding?.rbSecondFloor?.isChecked = false
                binding?.rbThirdFloor?.isChecked = false
                binding?.rbFourFloor?.isChecked = true
                binding?.rbBasement?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbGround?.buttonTintList = mActivity.getColorStateList(R.color.locationCol)
                binding?.rbFirstFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbSecondFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbThirdFloor?.buttonTintList =
                        mActivity.getColorStateList(R.color.locationCol)
                binding?.rbFourFloor?.buttonTintList = mActivity.getColorStateList(R.color.blue)

            }

        }
    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        dialog?.setCancelable(true)
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setLayout(
                (getScreenWidth(context as Activity) * .9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun getScreenWidth(activity: Activity): Int {
        val size = Point()
        activity.windowManager.defaultDisplay.getSize(size)
        return size.x
    }

    companion object {
        fun newInstance(
                title: String? = "",
                btnSubmitTitle: String? = "Done",
                isFor: String = "pickup",
                useFor: String = "",
                onClicksCallBack: ((AddAddressOption, address: PickupAddress) -> Unit)? = null
        ): AddAddressDialog {
            val args = Bundle()
            args.putString("TITLE", title)
            args.putString("YES_TITLE", btnSubmitTitle)
            args.putString("IS_FOR", isFor)
            args.putString("useFor", useFor)
            val fragment = AddAddressDialog()
            fragment.onClicksCallBack = onClicksCallBack
            fragment.arguments = args
            return fragment
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 22345) {
            val locationObj = data?.getSerializableExtra("locationObj") as LocationDataModel

            location_id = locationObj.location_id ?: ""
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
                        location_address = place?.address.toString()
                        binding?.tvPickUpAddress?.movementMethod = ScrollingMovementMethod()
//                        binding?.tvPickUpAddress?.isSelected = true
                        binding?.tvPickUpAddress?.text = place.address

                        val latitude: Double = place.latLng?.latitude ?: 0.0
                        val longitude: Double = place.latLng?.longitude ?: 0.0

                        try {
                            val geocoder = Geocoder(mActivity, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                            if (addresses != null && addresses.size > 0) {
                                val address =
                                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                val city = addresses[0].locality
                                val state = addresses[0].adminArea
                                val country = addresses[0].countryName
                                //     val knownName = addresses[0].featureName // Only if available else return NULL
                                Log.d(NetworkClass.TAG, "getAddress:  address$address")
                                Log.d(NetworkClass.TAG, "getAddress:  city$city")
                                Log.d(NetworkClass.TAG, "getAddress:  state$state")
//                                Log.d(TAG, "getAddress:  knownName$knownName")

                                location_city = city ?: ""
                                location_state = state ?: ""
                                location_country = country ?: ""
                                Log.d(
                                        "AddressFormat",
                                        place.address + "\n" + place.name + "\n" + location_city + "\n" + location_state + "\n" + location_country
                                )

                            } else {
                                mActivity.showToast("Error For Getting Address Details ... ")
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
    }
}

enum class AddAddressOption {
    DONE
}

