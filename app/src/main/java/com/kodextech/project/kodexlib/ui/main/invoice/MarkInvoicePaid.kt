package com.kodextech.project.kodexlib.ui.main.invoice

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityMarkInvoicePaidBinding
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.jobs.adapter.ImageListingAdapter
import com.kodextech.project.kodexlib.ui.main.termsAndServices.AdressListingModel
import com.kodextech.project.kodexlib.ui.main.termsAndServices.BitmapHelper
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible
import com.kodextech.project.kodexlib.utils.zDateConvertor
import com.williamww.silkysignature.views.SignaturePad
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class MarkInvoicePaid : BaseActivity() {

    private var binding: ActivityMarkInvoicePaidBinding? = null

    private var jobId: String? = null
    private var invoiceId: String? = null
    private var json: JSONArray? = null
    private var media: String? = null
    private var baseImagePath = "http://13.58.42.125/public/uploads/"

    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mark_invoice_paid)
        super.onCreate(savedInstanceState)

        initTopBar()

        val mSignaturePad = findViewById<View>(R.id.signature_pad) as SignaturePad
        mSignaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {

            }

            override fun onSigned() {
                binding?.btnSubmit?.setOnClickListener {
                    addSignature(mSignaturePad)
                }
                binding?.ivCancel?.setOnClickListener { mSignaturePad.clear() }
            }

            override fun onClear() {

            }
        })

    }

    private fun addSignature(mSignaturePad: SignaturePad) {

        Dexter.withContext(this@MarkInvoicePaid)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
//                            val bitmap = mSignaturePad.transparentSignatureBitmap
                            val bitmap = mSignaturePad.signatureBitmap
                            var file = bitmapToFile(bitmap, "IMAGE_${UUID.randomUUID()}.png")
                            addSignatureCall(file)
                            BitmapHelper.instance.bitmap = bitmap


                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {

                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                showToast(it.name)
            }
            .check()


    }

    private fun bitmapToFile(bitmap: Bitmap?, fileNameToSave: String): File {
        val wrapper = ContextWrapper(applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, fileNameToSave)

        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return file
    }

    private fun addSignatureCall(file: File?) {
        showLoading()
        NetworkClass.callFileUploadSingle(URLApi.addDocument(nature = "job"),
            file, "uploadedFiles[]", object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    json = JSONArray(response ?: "")
                    media = json.toString()
                    markJobPaid(invoiceId)

                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")

                }

            })
    }

    private fun markJobPaid(invoiceId: String?) {
        showLoading()
        NetworkClass.callApi(
            URLApi.updateInvoicesStatus(
                invoice_uuid = invoiceId,
                status = "paid"
            ), object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    showToast("Job Mark As Paid Successfully")
                    finish()
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showToast(error ?: "")
                }

            })
    }

    private fun initTopBar() {
        jobId = intent.getStringExtra("job_id")
        invoiceId = intent.getStringExtra("invoiceId")

        binding?.topBar?.ivLogout?.gone()
        binding?.topBar?.ivBack?.setOnClickListener { onBackPressed() }

        binding?.rlIvCancel?.gone()
        binding?.rlIvSignature?.visible()
        binding?.btnSubmit?.visible()
        binding?.rlNote?.gone()
        binding?.tvDescription?.gone()
        binding?.tvComments?.gone()
        binding?.tvSignature?.visible()
        binding?.btnSubmit?.text = "Mark Invoice Paid"

        getJobDetails(jobId)

    }

    private fun getJobDetails(jobId: String?) {
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


        if (obj?.job_status == "pending" || obj?.job_status == "assigned") {
            binding?.topBar?.ivLogout?.gone()
        } else {
            binding?.topBar?.ivLogout?.gone()
        }

        if (obj?.medias?.isEmpty() == true) {
            binding?.tvDiamantling?.gone()
            binding?.rvImage?.gone()
        } else {

            val mMedia = obj?.medias
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

        if (obj?.service == "Flat Move") {
            val newArray = ArrayList<AdressListingModel>()
            val newDropArray = ArrayList<AdressListingModel>()
            var floorNo: String? = null
            var dropFloorNo: String? = null
            newArray.clear()
            newDropArray.clear()
            obj.pickup_addresses?.forEachIndexed { index, pickupAddress ->

                if (pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no == "-1") {
                    floorNo = "Basement"
                } else if (pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no == "0") {
                    floorNo = "Ground Floor"
                } else {
                    floorNo = pickupAddress.pickup_flat_meta?.firstOrNull()?.floor_no + " Floor"
                }

                if (pickupAddress.has_lift.equals("1")) {
                    val s =
                        "Address: " + pickupAddress.address1 + "\nFloor No: " + floorNo + "\nLift Available: Yes\n"
                    newArray.add(AdressListingModel(s))

                } else {
                    val s =
                        "Address: " + pickupAddress.address1 + "\nFloor No: " + floorNo + "\nLift Available: No\n"
                    newArray.add(AdressListingModel(s))
                }

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
            binding?.tvPickUpAddress?.text = finalAddress?.replace("null", "")

        } else {
            val newArray = ArrayList<AdressListingModel>()
            newArray.clear()
            obj?.pickup_addresses?.forEachIndexed { index, pickupAddress ->
                val s = "Address: " + pickupAddress.address1 + "\n"
                newArray.add(AdressListingModel(s))
            }

            var newAddress: String? = null
            newArray.forEach {
                newAddress += it.address + ""
            }
            binding?.tvPickUpAddress?.text = newAddress?.replace("null", "")



            binding?.tvDropAddress?.text =
                obj?.drop_address?.address1
        }




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

        Glide.with(this).load(baseImagePath + obj?.signature?.path)
            .placeholder(R.drawable.ic_baseline_cloud_download_24).into(
                binding?.ivSignature!!
            )

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

    override fun onSetupViewGroup() {

    }

    override fun setupContentViewWithBinding() {

    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {

    }

    override fun setupLoader() {

    }
}
