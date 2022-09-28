package com.kodextech.project.kodexlib.ui.main.jobs

import WariningImageAdapter
import android.Manifest
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseActivity
import com.kodextech.project.kodexlib.databinding.ActivityJobsListingBinding
import com.kodextech.project.kodexlib.dialog.AppAlertOption
import com.kodextech.project.kodexlib.dialog.LogoutDialog
import com.kodextech.project.kodexlib.model.JobModel
import com.kodextech.project.kodexlib.model.MediaModel
import com.kodextech.project.kodexlib.network.LocalPreference
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.ui.main.auth.LoginActivity
import com.kodextech.project.kodexlib.ui.main.booking.AddBooking
import com.kodextech.project.kodexlib.ui.main.dashboard.Dashboard
import com.kodextech.project.kodexlib.ui.main.jobs.adapter.JobListingAdapter
import com.kodextech.project.kodexlib.ui.main.jobs.adapter.JobListingAdapter.Companion.mDataSelected
import com.kodextech.project.kodexlib.ui.main.jobs.adapter.selectDialog
import com.kodextech.project.kodexlib.ui.main.termsAndServices.BitmapHelper
import com.kodextech.project.kodexlib.utils.generateList
import com.kodextech.project.kodexlib.utils.gone
import com.kodextech.project.kodexlib.utils.text
import com.kodextech.project.kodexlib.utils.visible
import com.williamww.silkysignature.views.SignaturePad
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class JobsListing : BaseActivity(), selectDialog {

    private var binding: ActivityJobsListingBinding? = null
    private var mData = ArrayList<JobModel>()
    private var mAdapter: JobListingAdapter? = null
    private var viewStates: String? = "today"
    private var from: String? = null
    private var listingFor: String? = null
    private var mDataDate = ArrayList<String>()
    private var searchText: String? = ""
    private var screenSate: JOBSSTATE? = JOBSSTATE.INPROGRESS
    private var isFor: String? = ""

    private var mImageAdapter: WariningImageAdapter? = null
    private val IMAGE_REQUEST_CODE = 2212
    private var mMediaData = ArrayList<MediaModel>()
    private var uploaded_files: String? = null
    var profile_image_selected: ArrayList<Uri>? = null
    private var json: JSONArray? = null
    private var media: String? = null
    private var jobId: String? = null

    override fun onSetupViewGroup() {
        mViewGroup = binding?.contentJobs
    }

    override fun setupContentViewWithBinding() {
        statusBarColor(getColor(R.color.blue))
//        binding?.rlButton?.visibility = View.GONE
        binding = DataBindingUtil.setContentView(this, R.layout.activity_jobs_listing)
        initTopBar()
        setJobData()
        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
                filter(s.toString())
            }
        })

        binding?.btnDelete?.setOnClickListener {
            deleteBookings()
        }

        binding?.topBar?.ivBack?.setOnClickListener {
            if (from == "customer") {
                val intent = Intent(this, Dashboard::class.java)
                startActivity(intent)
                finish()
            }
            else {

                finish()
            }
//            val intent = Intent(this, Dashboard::class.java)
//            startActivity(intent)
//            finish()
        }

        binding?.topBar?.ivLogout?.setOnClickListener {
            val dl = LogoutDialog.newInstance(
                "LOGOUT",
                "Do you really Want to logout?",
                "Yes",
                "No"
            ) {
                when (it) {
                    AppAlertOption.YES -> {
                        logout()
                    }
                    AppAlertOption.NO -> {
                    }
                }
            }
            dl.show(supportFragmentManager, "")
        }

        binding?.topBar?.ivSearch?.setOnClickListener {
            if (binding?.etSearch?.visibility == View.VISIBLE) {
                binding?.etSearch?.visibility = View.GONE
                searchText = ""
                viewStates = "today"
                getJobListCall()
                changeViewColor()
            } else {
                binding?.etSearch?.visibility = View.VISIBLE
            }
        }
        binding?.btnNotAssigned?.setOnClickListener {
            binding?.viewNotAssigned?.visibility = View.VISIBLE
            binding?.viewCompleted?.visibility = View.INVISIBLE
            binding?.viewInprogress?.visibility = View.INVISIBLE
            binding?.etSearch?.text("")

            listingFor = "notAssigned"
            checkAndSetData()
        }

        binding?.btnJobsInprogress?.setOnClickListener {
            binding?.viewInprogress?.visibility = View.VISIBLE
            binding?.viewCompleted?.visibility = View.INVISIBLE
            binding?.viewNotAssigned?.visibility = View.INVISIBLE
            binding?.etSearch?.text("")
            listingFor = "inprogress"
            checkAndSetData()
        }

        binding?.btnJobsCompleted?.setOnClickListener {
            binding?.viewInprogress?.visibility = View.INVISIBLE
            binding?.viewCompleted?.visibility = View.VISIBLE
            binding?.viewNotAssigned?.visibility = View.INVISIBLE
            listingFor = "completed"
            binding?.etSearch?.text("")

            checkAndSetData()
        }
//        binding?.svJobs?.setOnRefreshListener {
//            if (LocalPreference.shared.user?.user?.profile_type == "worker") {
//                getWorkerJob(LocalPreference.shared.user?.user?.profile?.uuid)
//            } else {
//                getJobListCall()
//            }
//        }

        binding?.tvDay?.setOnClickListener {
            viewStates = "today"
            binding?.etSearch?.text("");
            getJobListCall()
            changeViewColor()

        }
        binding?.tvMonth?.setOnClickListener {
            binding?.etSearch?.text("");
            viewStates = "current-month"
            changeViewColor()
            getJobListCall()

        }
        binding?.tvYear?.setOnClickListener {
            binding?.etSearch?.text("");
            viewStates = "current-year"
            changeViewColor()
            getJobListCall()

        }
        binding?.tvWeekly?.setOnClickListener {
            binding?.etSearch?.text("");
            viewStates = "current-week"
            changeViewColor()
            getJobListCall()
        }

        binding?.btnExport?.setOnClickListener {
            jobExport()
        }
    }

    private fun deleteBookings() {
        val jobIds = mDataSelected.map { it.uuid }.joinToString(",")
        val newIds = mDataSelected
        showLoading()
        NetworkClass.callApi(URLApi.deleteBooking(job_uuid = jobIds), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                showToast("Your Mark Booking Delete Successfully")
                binding?.rlButton?.gone()
                mDataSelected.clear()
                checkAndSetData()
                hideLoading()
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showToast(error ?: "")
            }
        })
    }

    private fun jobExport() {
        Toast.makeText(binding?.root?.context, "Download started....", Toast.LENGTH_SHORT).show()
        var list: ArrayList<Int> = ArrayList();
        val jobIds = mDataSelected.map { it.uuid }.joinToString(",")
        val newIds = mDataSelected
        showLoading()
        mDataSelected.map { list.add(it.id!!) }
        Toast.makeText(binding?.root?.context, list.toString(), Toast.LENGTH_SHORT).show()
        NetworkClass.callApi(URLApi.getExport(booking_id = list), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                //showToast("Tester" +response.toString())
                val uri: Uri =
                    Uri.parse(response)
                val downloadManager: DownloadManager =
                    baseContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                val request: DownloadManager.Request = DownloadManager.Request(uri)
                request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI or
                            DownloadManager.Request.NETWORK_MOBILE
                )
// set title and description
                request.setTitle("CSV Downloaded")
                request.setDescription("CSV Downloaded Successfully")

                request.allowScanningByMediaScanner()
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

//set the local destination for download file to a path within the application's external files directory
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "CDV.pdf"
                )
                request.setMimeType("*/*")
                downloadManager?.enqueue(request)
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showToast(error ?: "")
            }
        })
    }

    private fun getJobListCall() {
//        if (binding?.svJobs?.isRefreshing == false) {
//            binding?.svJobs?.isRefreshing = true
//        }

        showLoading()
        mDataSelected.clear()
        binding?.rlButton?.visibility = View.GONE
        NetworkClass.callApi(
            URLApi.getJobLoistByTimeNature(
                worker_uuid = null,
                time_nature = viewStates
            ), object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    mData.clear()
                    val json = JSONObject(response ?: "")
                    var model = json.optJSONArray("models")
                    val data = generateList(model.toString() ?: "", Array<JobModel>::class.java)
                    when (listingFor) {
                        "notAssigned" -> {
                            val filterData = data.filter {
                                it.job_status?.lowercase() == "pending".lowercase()
                            }
                            mData.addAll(filterData)
                        }
                        "inprogress" -> {
                            val filterData = data.filter {
                                it.job_status?.lowercase() == "assigned".lowercase()
                                        || it.job_status?.lowercase() == "active".lowercase()
                            }
                            mData.addAll(filterData)
                        }
                        else -> {
                            val filterData = data.filter {
                                it.job_status?.lowercase() == "completed".lowercase()
                                        || it.job_status?.lowercase() == "cancelled".lowercase()
                            }
                            mData.addAll(filterData)
                        }
                    }
                    setJobData()
                    //  binding?.svJobs?.isRefreshing = false
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                    //  binding?.svJobs?.isRefreshing = false
                }
            })
    }

    private fun filter(text: String) {
        var filterlist: ArrayList<JobModel> = ArrayList()
        var fullName: String? = ""
        for (item in mData) {
            fullName = item.first_name.toString() + " " + item.last_name.toString()
            if (item.first_name?.lowercase()?.contains(text.lowercase()) == true
                || item.last_name?.lowercase()?.contains(text.lowercase()) == true
                || try {

                    item.id == text.toInt()
                } catch (e: Exception) {
                    false
                }
                || fullName?.equals(text) == true
            ) {
                filterlist.add(item)
            }
        }
        mAdapter?.filterJobList(filterlist)
    }

    private fun setJobData() {
        checkingData()
        mAdapter = JobListingAdapter(
            this,
            mData,
            this
        ) { item, position, invoiceGenerated, isFor ->
            if (isFor == "1") {
                if (invoiceGenerated) {
                    generateInvoice(item.uuid)
                } else {
                    cancelBooking(item)
                }
            } else if (isFor == "0") {
                binding?.rlButton?.visible()
            } else if (isFor == "2") {
                binding?.rlButton?.gone()
            }
        }
        binding?.rvJobs?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding?.rvJobs?.adapter = mAdapter
        mAdapter?.notifyDataSetChanged()
    }

    private fun generateInvoice(uuid: String?) {
        showLoading()
        NetworkClass.callApi(URLApi.getInvoicePDF(job_uuid = uuid), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                hideLoading()
                showToast(message ?: "")
                getJobListCall()
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showToast(error ?: "")
            }

        })
    }

    private fun cancelBooking(item: JobModel) {
        showLoading()

        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val currentDateandTime = sdf.format(Date())

        NetworkClass.callApi(
            URLApi.updateJobStatus(item.uuid, "cancelled", currentDateandTime),
            object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    getJobListCall()
                    showBarToast("Booking Cancelled")
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                }
            })
    }

    private fun checkingData() {
        if (mData.size == 0) {
            binding?.rlNoData?.visible()
            binding?.rvJobs?.gone()
        } else {
            binding?.rlNoData?.gone()
            binding?.rvJobs?.visible()
        }
    }

    private fun initTopBar() {

        binding?.etSearch?.hint = "Type Here"

        if (LocalPreference.shared.user?.user?.profile?.worker_type?.lowercase() == "driver".lowercase()||LocalPreference.shared.user?.user?.profile?.worker_type?.lowercase() == "porter".lowercase()) {
            binding?.cvMain?.gone()
            binding?.topBar?.ivBack?.visible()
            binding?.topBar?.ivLogout?.visible()
            binding?.topBar?.ivSearch?.visible()
            from = intent.getStringExtra("from")
            listingFor = "notAssigned"
            binding?.btnNotAssigned?.text = "Assigned Job"

        } else {
            binding?.cvMain?.visible()
            binding?.btnNotAssigned?.visible()
            binding?.viewNotAssigned?.visible()
            binding?.topBar?.ivLogout?.gone()
            binding?.topBar?.ivSearch?.visible()
            listingFor = "notAssigned"
            from = intent.getStringExtra("from")

        }
        binding?.viewNotAssigned?.visible()
        binding?.btnNotAssigned?.visible()
        binding?.viewInprogress?.gone()
        binding?.viewCompleted?.gone()
        binding?.topBar?.tvText?.text = "Jobs"


    }

    private fun getWorkerJob(uuid: String? = null) {
        showLoading()
        NetworkClass.callApi(URLApi.getJobList(worker_uuid = uuid), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {

                hideLoading()
                mData.clear()
                val json = JSONObject(response ?: "")
                var model = json.optJSONArray("models")
                val data = generateList(model.toString() ?: "", Array<JobModel>::class.java)


                when (listingFor) {
                    "notAssigned" -> {
                        val filterData = data.filter { it.job_status?.lowercase() == "assigned" }
                        mData.addAll(filterData)
                    }
                    "inprogress" -> {
                        val filterData = data.filter {
                            it.job_status?.lowercase() == "active".lowercase()
                        }
                        mData.addAll(filterData)
                    }
                    else -> {
                        val filterData = data.filter {
                            it.job_status?.lowercase() == "completed".lowercase()
                                    || it.job_status?.lowercase() == "cancelled".lowercase()
                        }
                        mData.addAll(filterData)
                    }
                }

                setJobData()
              //  binding?.svJobs?.isRefreshing = false
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }
        })
    }

    override fun onRecycleBeforeDestroy() {

    }

    override fun onBackPressed() {
        if (from == "customer") {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }
        else {
            finish()
        }
    }

    override fun onBecameInvisibleToUser() {

    }

    override fun onBecameVisibleToUser() {
        checkAndSetData()

    }

    private fun checkAndSetData() {
//        mDataSelected.clear()
        binding?.rlButton?.gone()
        mDataSelected.clear()
        if (LocalPreference.shared.user?.user?.profile?.worker_type?.lowercase() == "driver".lowercase()||LocalPreference.shared.user?.user?.profile?.worker_type?.lowercase() == "porter".lowercase()) {
            getWorkerJob(LocalPreference.shared.user?.user?.profile?.uuid)

        } else {
            if (mDataSelected.count() == 0) {
                binding?.rlButton?.gone()
            } else {
                binding?.rlButton?.visible()
            }
            getJobListCall()
        }
    }

    override fun setupLoader() {

    }

    private fun changeViewColor() {
        when (viewStates) {
            "today" -> {
                binding?.tvDay?.setBackgroundColor(getColor(R.color.blue))
                binding?.tvDay?.setTextColor(getColor(R.color.white))
                binding?.tvMonth?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvMonth?.setTextColor(getColor(R.color.cusCol))
                binding?.tvWeekly?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvWeekly?.setTextColor(getColor(R.color.cusCol))
                binding?.tvYear?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvYear?.setTextColor(getColor(R.color.cusCol))
            }
            "current-month" -> {
                binding?.tvMonth?.setBackgroundColor(getColor(R.color.blue))
                binding?.tvMonth?.setTextColor(getColor(R.color.white))
                binding?.tvDay?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvDay?.setTextColor(getColor(R.color.cusCol))
                binding?.tvWeekly?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvWeekly?.setTextColor(getColor(R.color.cusCol))
                binding?.tvYear?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvYear?.setTextColor(getColor(R.color.cusCol))
            }
            "current-year" -> {
                binding?.tvMonth?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvMonth?.setTextColor(getColor(R.color.cusCol))
                binding?.tvDay?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvDay?.setTextColor(getColor(R.color.cusCol))
                binding?.tvWeekly?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvWeekly?.setTextColor(getColor(R.color.cusCol))
                binding?.tvYear?.setBackgroundColor(getColor(R.color.blue))
                binding?.tvYear?.setTextColor(getColor(R.color.white))
            }
            "current-week" -> {
                binding?.tvMonth?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvMonth?.setTextColor(getColor(R.color.cusCol))
                binding?.tvDay?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvDay?.setTextColor(getColor(R.color.cusCol))
                binding?.tvYear?.setBackgroundColor(getColor(R.color.gray))
                binding?.tvYear?.setTextColor(getColor(R.color.cusCol))
                binding?.tvWeekly?.setBackgroundColor(getColor(R.color.blue))
                binding?.tvWeekly?.setTextColor(getColor(R.color.white))
            }
        }
    }

    fun logout() {
        showLoading()
        NetworkClass.callApi(URLApi.signout(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                LocalPreference.shared.removeAll()
                val intent = Intent(this@JobsListing, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                ActivityCompat.finishAffinity(this@JobsListing)
            }

            override fun onErrorResponse(error: String?, response: String?) {
                hideLoading()
                showBarToast(error ?: "")
            }
        })
    }

    override fun openDialog(jobId:String) {
        liabilityDialog(jobId)
    }


    /** Liability Dialog **/
    fun liabilityDialog(jobId: String) {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_liability_dialog)
        val crossImg = dialog.findViewById<AppCompatImageView>(R.id.ivCross)
        val ivCancel = dialog.findViewById<AppCompatImageView>(R.id.ivCancel)
        val checkBox = dialog.findViewById<CheckBox>(R.id.checkBox)
        val note4 = dialog.findViewById<AppCompatTextView>(R.id.note4)
        val rvImage = dialog.findViewById<RecyclerView>(R.id.rvImage)
        val doneBtn = dialog.findViewById<AppCompatButton>(R.id.doneBtn)
        val mSignaturePad = dialog.findViewById<SignaturePad>(R.id.signature_pad_liability)


        crossImg.setOnClickListener { dialog.dismiss() }
        checkBox?.setOnClickListener {

            if (checkBox?.isChecked == true) {
                note4?.visibility = View.VISIBLE
            } else {
                note4?.visibility = View.GONE
            }
        }

        doneBtn.setOnClickListener {
        }



        mSignaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {

            }

            override fun onSigned() {
                doneBtn.setOnClickListener {
                    addSignature(mSignaturePad,jobId)
                }
                ivCancel.setOnClickListener { mSignaturePad.clear() }
            }

            override fun onClear() {

            }
        })



        mImageAdapter =
            WariningImageAdapter(this, AddBooking.mData) { position, forDelete ->
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

        rvImage.layoutManager =
            GridLayoutManager(this, 3)
        rvImage.adapter = mImageAdapter
        mImageAdapter?.notifyDataSetChanged()


        dialog.setCancelable(false)
        dialog.show()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setGravity(Gravity.CENTER)

    }

    private fun deleteImage(position: Int) {
        showLoading()
        NetworkClass.callApi(URLApi.deleteDocument(mMediaData, "booking"),
            object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    mMediaData.removeAt(position - 1)
                    AddBooking.mData.removeAt(position - 1)
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
            AddBooking.mData, "uploadedFiles[]", object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
                    val json = JSONArray(response ?: "")
                    val data = generateList(json.toString(), Array<MediaModel>::class.java)
                    mMediaData.addAll(data)
                    uploaded_files = Gson().toJson(mMediaData)//mMediaData.toString()
                    Log.d("uploaded_files", uploaded_files?.toString() ?: " ")
                    AddBooking.scrollImageList.clear()
                    AddBooking.scrollImageList.addAll(mMediaData)
                    showBarToast(message)
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")
                    AddBooking.mData.clear()
                    mImageAdapter?.notifyDataSetChanged()
                }

            })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                            this,
                            uri
                        )
                    AddBooking.mData.add(file!!)
                }

                this.runOnUiThread {
                    addDocumentCall()
                    mImageAdapter?.notifyDataSetChanged()

                }

            }
            profile_image_selected?.addAll(array)
            Log.d("ProfileTAG", array.toString())
        }
    }

    private fun addSignature(mSignaturePad: SignaturePad, jobId: String) {
        Dexter.withContext(this)
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
                            addSignatureCall(file,jobId)
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

    private fun addSignatureCall(file: File?, jobId: String) {
        showLoading()
        NetworkClass.callFileUploadSingle(URLApi.addDocument(nature = "job"),
            file, "uploadedFiles[]", object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    json = JSONArray(response ?: "")
                    media = json.toString()
                    startJobCall(media,jobId)
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    showBarToast(error ?: "")

                }
            })
    }
    private fun startJobCall(media: String?, jobId: String) {
        showLoading()
        NetworkClass.callApi(
            URLApi.startJob(
                job_uuid = jobId,
                signature_media = media,
                boolean =true,
                uploadfile = uploaded_files
            ),
            object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    hideLoading()
//                    warningDialog()

//                    val intent = Intent(this, JobsListing::class.java)
//                    startActivity(intent)
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

}


enum class JOBSSTATE {
    INPROGRESS, COMPLETED
}