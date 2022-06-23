package com.kodextech.project.kodexlib.ui.main.jobs

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityStartJobBinding
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.termsAndServices.AdressListingModel
import com.kodextech.project.kodexlib.ui.main.termsAndServices.BitmapHelper
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.visible
import com.williamww.silkysignature.views.SignaturePad
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class StartJobActivity : BaseActivity() {

    private var binding: ActivityStartJobBinding? = null

    private var jobId: String? = null
    private var json: JSONArray? = null
    private var media: String? = null


    override fun onSetupViewGroup() {
        mViewGroup = binding?.contentStartJob
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
        binding = DataBindingUtil.setContentView(this, R.layout.activity_start_job)
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

        Dexter.withContext(this@StartJobActivity)
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


    private fun addSignatureCall(file: File?) {
        showLoading()
        NetworkClass.callFileUploadSingle(URLApi.addDocument(nature = "job"),
            file, "uploadedFiles[]", object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    json = JSONArray(response ?: "")
                    media = json.toString()
                    startJobCall(media)
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")

                }

            })
    }

    private fun startJobCall(media: String?) {
        showLoading()
        NetworkClass.callApi(
            URLApi.startJob(job_uuid = jobId, signature_media = media),
            object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    warningDialog()

                    val intent = Intent(this@StartJobActivity, JobsListing::class.java)
                    startActivity(intent)
                    finish()
                    showBarToast("Job Started Successfully")
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                }
            })
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

    private fun initTopBar() {
        jobId = intent.getStringExtra("jobId")
        binding?.topBar?.tvText?.text = "Terms Of Service"
        binding?.topBar?.ivLogout?.gone()
        binding?.topBar?.ivBack?.setOnClickListener { onBackPressed() }

        setData()
    }

    private fun setData() {
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
        binding?.tvCustomerName?.text = obj?.first_name + " " + obj?.last_name
        binding?.tvDriverName?.text = obj?.worker?.full_name

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


            var finalAddress: String? = null
            newArray.forEach {
                finalAddress += it.address + ""
            }

            binding?.tvCustomerAddress?.text = finalAddress?.replace("null", "")

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
            binding?.tvCustomerAddress?.text = newAddress?.replace("null", "")

        }


        if (obj?.has_requested_insurance?.equals("0") == true) {
            binding?.insurancedCustomer?.gone()
        } else {
            binding?.insurancedCustomer?.gone()
        }

        val price_amount = obj?.price_amount?.toDoubleOrNull() ?: 0.0
        val price_amount_rounded = String.format("%.2f", price_amount)
        binding?.tvPrice?.text = price_amount_rounded
        if (obj?.price_nature?.equals("fixed-price") == true) {
            binding?.priceSign?.gone()
            binding?.llMinimumBooking?.gone()
            binding?.tvPrice?.background = getDrawable(R.drawable.bg_right_radius)
            binding?.tvPrice?.backgroundTintList = getColorStateList(R.color.bgEdit)
        } else {
            binding?.priceSign?.visible()
            binding?.llMinimumBooking?.visible()
            val minimum_booking = obj?.min_hours_count
            setMinimumButtonColorState(minimum_booking)
            binding?.tvPrice?.backgroundTintList = getColorStateList(R.color.bgEdit)
            binding?.tvPrice?.background = getDrawable(R.drawable.bg_right_radius)
        }

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

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {

    }

    override fun setupLoader() {

    }

    fun warningDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.waring_dialog)
        val doneBtn = dialog.findViewById<Button>(R.id.doneBtn)
        val cancelBtn = dialog.findViewById<Button>(R.id.cancelBtn)
        val crossImg = dialog.findViewById<AppCompatImageView>(R.id.ivCross)
        doneBtn.setOnClickListener { // App.getSharedPre().userLogOut();
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
    }


}