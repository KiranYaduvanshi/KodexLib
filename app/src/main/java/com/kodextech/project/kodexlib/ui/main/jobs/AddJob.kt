package com.kodextech.project.kodexlib.ui.main.jobs

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.binaryfork.spanny.Spanny
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityAddJobBinding
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.model.User
import com.kodextech.project.kodexlib.network.LocalPreference
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.termsAndServices.BitmapHelper
import com.kodextech.project.kodexlib.utils.*
import com.williamww.silkysignature.views.SignaturePad
import com.williamww.silkysignature.views.SignaturePad.OnSignedListener
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.toString


class AddJob : BaseActivity() {

    private var binding: ActivityAddJobBinding? = null

    private var isFor: String? = ""

    private var cusUUID: String? = ""

    private var driverName: String? = ""
    private var driverUUID: String? = ""
    private var customerUUID: String? = ""
    private var priceNature: String? = ""
    private var minimumAmount: String? = ""
    private var bookingName: String? = ""
    private var bookingUUID: String? = ""
    private var startTime: String? = ""
    private var endTime: String? = ""
    private var jobType: String? = ""
    private var charged: String? = ""
    private var address1: String? = ""
    private var address2: String? = ""

    private var json: JSONArray? = null
    private var media: String? = null

    val mDriver = ArrayList<User>()
    val mCustomer = ArrayList<User>()
    val mBooking = ArrayList<JobModel>()

    override fun onSetupViewGroup() {
        mViewGroup = binding?.contentAddJob
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_job)

        initTopBar()
        setSpinnerValues()

        binding?.topBar?.ivBack?.setOnClickListener { onBackPressed() }


        binding?.spDriverName?.setOnItemSelectedListener { view, position, id, item ->
            val item = mDriver[position]
            driverUUID = item.profile?.uuid.toString()
        }

        binding?.spCustomerName?.setOnItemSelectedListener { view, position, id, item ->
            val item = mCustomer[position]
            cusUUID = item.profile?.uuid

        }

        binding?.spPriceNature?.setOnItemSelectedListener { view, position, id, item ->
            if (position == 0) {
                priceNature = "fixed-price"
                binding?.tvPriceNature?.text = "/f."
            } else if (position == 1) {
                priceNature = "hourly-price"
                binding?.tvPriceNature?.text = "/hr."
            }
        }
        binding?.tvStartTime?.setOnClickListener {
            val dateTimeDialog =
                DateTimepickerDialog.newInstance { datePickerOptions, dateString, timeString, timeStamp ->
                    when (datePickerOptions) {
                        DatePickerOptions.OK -> {
                            binding?.tvStartTime?.text = timeStamp?.getDateWithUtcToLocalConversion(
                                "yyyy/MM/dd HH:mm",
                                "yy-MM-dd HH:mm"
                            )
                            startTime = timeStamp?.getDateWithUtcToLocalConversion(
                                "yyyy/MM/dd HH:mm:ss",
                                "YYYY-MM-dd HH:mm:ss"
                            )
                        }
                        DatePickerOptions.CANCEL -> {

                        }
                    }
                }
            dateTimeDialog.show(supportFragmentManager, "")
        }

        binding?.tvEndTime?.setOnClickListener {
            val dateTimeDialog =
                DateTimepickerDialog.newInstance { datePickerOptions, dateString, timeString, timeStamp ->
                    when (datePickerOptions) {
                        DatePickerOptions.OK -> {
                            binding?.tvEndTime?.text = timeStamp?.getDateWithUtcToLocalConversion(
                                "yyyy/MM/dd HH:mm",
                                "yy-MM-dd HH:mm"
                            )
                            endTime = timeStamp?.getDateWithUtcToLocalConversion(
                                "yyyy/MM/dd HH:mm:ss",
                                "YYYY-MM-dd HH:mm:ss"
                            )
                        }
                        DatePickerOptions.CANCEL -> {

                        }
                    }
                }
            dateTimeDialog.show(supportFragmentManager, "")
        }

        binding?.spJobType?.setOnItemSelectedListener { view, position, id, item ->
            when {
                item.toString().lowercase() == "Flat Move".lowercase() -> {
                    jobType = "flat-move"
                }
                item.toString().lowercase() == "Office Move".lowercase() -> {
                    jobType = "office-move"
                }
                item.toString().lowercase() == "House Move".lowercase() -> {
                    jobType = "house-move"
                }
                item.toString().lowercase() == "Event Move".lowercase() -> {
                    jobType = "event-move"
                }
            }
        }


        val mSignaturePad = findViewById<View>(R.id.signature_pad) as SignaturePad
        mSignaturePad.setOnSignedListener(object : OnSignedListener {
            override fun onStartSigning() {

            }

            override fun onSigned() {

//                binding?.ivCheck?.setOnClickListener {
//                    addSignature(mSignaturePad) }
                binding?.ivCancel?.setOnClickListener { mSignaturePad.clear() }
            }

            override fun onClear() {

            }
        })

        binding?.btnSave?.setOnClickListener {

            if (isFor == "createJob") {
                createJobValidation()
            } else {
                validation()
            }
        }

    }

    private fun addSignature(mSignaturePad: SignaturePad) {
        val bitmap = mSignaturePad.transparentSignatureBitmap
        var file = bitmapToFile(bitmap, "File.jpg")
        val array = ArrayList<File>()
        array.let {
            it.add(file!!)
        }
        Dexter.withContext(this@AddJob)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            addSignatureCall(array)

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
                showToast(it.name)
            }
            .check()
        BitmapHelper.instance.bitmap = bitmap


    }

    private fun addSignatureCall(file: ArrayList<File>?) {
        showLoading()
        NetworkClass.callFileUpload(URLApi.addDocument(nature = "booking"),
            file!!, "uploadedFiles[]", object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    json = JSONArray(response ?: "")
                    media = json.toString()
                    showBarToast(message ?: "")
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")

                }

            })
    }


    private fun createJobValidation() {

        minimumAmount = binding?.etMinimumAmount?.text.toString()
        address1 = binding?.etPickUpAddress?.text.toString()
        address2 = binding?.etDropAddress?.text.toString()

        if (cusUUID?.isNullOrEmpty() == true) {
            showBarToast("Please Select Customer Name")
        } else if (startTime?.isNullOrEmpty() == true) {
            binding?.tvStartTime?.error = "Required"
        } else if (jobType?.isNullOrEmpty() == true) {
            showBarToast("Please Select Job Type")
        } else if (address1?.isNullOrEmpty() == true) {
            binding?.etPickUpAddress?.error = "Required"
        } else if (address2?.isNullOrEmpty() == true) {
            binding?.etDropAddress?.error = "Required"
        } else if (priceNature?.isNullOrEmpty() == true) {
            showBarToast("Please Select Price Nature")
        } else if (charged?.isNullOrEmpty() == true) {
            binding?.etCharged?.error = "Required"
        } else if (minimumAmount?.isNullOrEmpty() == true) {
            binding?.etMinimumAmount?.error = "Required"
        } else {


            createJob(
                cusUUID?.trim() ?: "",
                priceNature?.trim() ?: "",
                minimumAmount?.trim() ?: "",
                charged?.trim() ?: "",
                jobType?.trim() ?: "",
                address1?.trim() ?: "",
                address2?.trim() ?: ""
            )
        }
    }

    private fun createJob(
        cusUUID: String,
        priceNature: String,
        minimumAmount: String,
        charged: String,
        jobType: String,
        address1: String,
        address2: String
    ) {
        showLoading()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validation() {


        if (driverUUID?.isNullOrEmpty() == true) {
            showBarToast("Please Select Driver Name")
        } else if (bookingUUID?.isNullOrEmpty() == true) {
            showBarToast("Please Select Booking")
        } else if (startTime?.isNullOrEmpty() == true) {
            binding?.tvStartTime?.error = "Required"
        } else if (endTime?.isNullOrEmpty() == true) {
            binding?.tvEndTime?.error = "Required"
        } else if (charged?.isNullOrEmpty() == true) {
            binding?.etCharged?.error = "Required"
        } else if (address1?.isNullOrEmpty() == true) {
            binding?.etPickUpAddress?.error = "Required"
        } else if (address2?.isNullOrEmpty() == true) {
            binding?.etDropAddress?.error = "Required"
        } else if (jobType?.isNullOrEmpty() == true) {
            showBarToast("Please Select Job Type")
        } else {
            val sDate =
                LocalDate.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            val eDate = LocalDate.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            if (eDate.isBefore(sDate)) {
                showBarToast("Your Booking Timer is Incorrect")

            } else {
                assignJobCall(
                    cusUUID,
                    driverUUID,
                    bookingUUID,
                    startTime,
                    endTime,
                    jobType,
                    charged,
                    address1,

                    )
            }
        }
    }

    private fun assignJobCall(
        cusUUID: String? = null,
        driverUUID: String? = null,
        startTime: String? = null,
        endTime: String? = null,
        jobType: String? = null,
        charged: String? = null,
        address1: String? = null,
        address2: String? = null
    ) {
        showLoading()
        NetworkClass.callApi(URLApi.addJob(
            customer_uuid = cusUUID,
            driver_uuid = driverUUID,
            job_type = jobType,
            is_approved = "0",
            job_status = "pending",
            start_time = startTime,
            end_time = endTime,
            charged_amount = charged,
            pickup_address1 = address1,
            drop_address1 = address2
        ), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                showBarToast("Job Assigned Successfully")
                finish()
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }


    private fun setSpinnerValues() {
        binding?.spJobType?.setItems(
            "Flat Move",
            "Office Move",
            "House Move",
            "Event Move"
        )
        binding?.spDriverName?.setHint("Driver Name")
        binding?.spCustomerName?.setHint("Customer Name")

        binding?.spPriceNature?.setItems(
            "Fixed Price", "Hourly Prices"
        )


    }


    private fun getUser() {
        showLoading()
        NetworkClass.callApi(URLApi.getUser(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                mDriver.clear()
                mCustomer.clear()
                hideLoading()
                val json = JSONObject(response ?: "")
                val json2 = json.optJSONArray("user")
                var data = generateList(json2.toString(), Array<User>::class.java)
                val driver = data.filter { it.profile_type?.lowercase() == "driver".lowercase() }
                val customer =
                    data.filter { it.profile_type?.lowercase() == "customer".lowercase() }
                mDriver.addAll(driver)
                mCustomer.addAll(customer)

                binding?.spDriverName?.setItems(mDriver.map { it.profile?.full_name })
                binding?.spCustomerName?.setItems(mCustomer.map { it.profile?.full_name })

                if (LocalPreference.shared.user?.user?.profile_type?.lowercase() == "customer".lowercase()) {
                    binding?.llCustomerName?.gone()
                    customerUUID = LocalPreference.shared.user?.user?.profile?.uuid
                } else {
                    hideLoading()
                }


            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }

        })
    }


    override fun onRecycleBeforeDestroy() {
    }

    override fun onBecameInvisibleToUser() {
    }

    override fun onBecameVisibleToUser() {
    }

    override fun setupLoader() {
    }


    private fun initTopBar() {
        binding?.topBar?.tvText?.text = "Add Jobs"
        binding?.topBar?.ivLogout?.gone()

        isFor = intent.getStringExtra("isFor")




        if (isFor == "createJob") {
            binding?.llDriverName?.gone()
            binding?.llCancel?.gone()
            binding?.btnSave?.text = "Submitted"
        } else {
            binding?.llDriverName?.visible()
            binding?.llCancel?.visible()
            binding?.btnSave?.text = "Save"
        }


        if (LocalPreference.shared?.user?.user?.profile_type == "customer") {
            binding?.llCustomerName?.gone()
            cusUUID = LocalPreference?.shared?.user?.user?.profile?.uuid
        } else if (LocalPreference.shared.user?.user?.profile_type == "admin") {
            binding?.llCustomerName?.visible()
        }

        getUser()


        val spanny =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Spanny(
                    "2 hours minimum booking",
                    ForegroundColorSpan(Color.RED),
                    BulletSpan(40, getColor(R.color.red), 10)

                )
            } else {
                TODO("VERSION.SDK_INT < P")
            }

        binding?.tvReman?.text = spanny

        val cashCollected = Spanny(
            "Cash is collected BEFORE offloading",
            ForegroundColorSpan(Color.RED),
            BulletSpan(40, getColor(R.color.red), 10)

        )
        binding?.tvCashCollected?.text = cashCollected

    }

    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return file // it will return null
        }
    }
}