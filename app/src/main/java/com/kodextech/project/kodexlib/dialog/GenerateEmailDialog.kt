package com.kodextech.project.kodexlib.dialog

import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.kodextech.project.kodexlib.R
import com.kodextech.project.kodexlib.base.BaseDialogueFragment
import com.kodextech.project.kodexlib.databinding.GenerateEmailDialogBinding
import com.kodextech.project.kodexlib.model.MediaModel
import com.kodextech.project.kodexlib.network.NetworkClass
import com.kodextech.project.kodexlib.network.Response
import com.kodextech.project.kodexlib.network.URLApi
import com.kodextech.project.kodexlib.utils.generateList
import org.json.JSONArray
import java.io.*

class GenerateEmailDialog : BaseDialogueFragment() {

    private var binding: GenerateEmailDialogBinding? = null
    private var mWorkerData = ArrayList<String>()
    private var mData = ArrayList<File>()
    private var workerType: String? = null
    private var subject: String? = null
    private var body: String? = null
    private var file: File? = null
    private var fileResponsePath: String? = null
    private var json: JSONArray? = null
    private var media: String? = null


    //image pick code
    private val IMAGE_PICK_CODE = 1000;

    //Permission code
    private val PERMISSION_CODE = 1001;


    override fun onSetupArguments() {

    }

    override fun onBindItemListenerOrViewVariables() {

    }

    override fun setupContentViewWithBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.generate_email_dialog, container, false)
        isCancelable = false

        binding?.spService?.hint = "Services"


        getWorkerTypeList()


        binding?.spService?.setOnItemSelectedListener { view, position, id, item ->
            workerType = item.toString()
        }

        binding?.ivCross?.setOnClickListener { dismiss() }
        binding?.btnCancel?.setOnClickListener { dismiss() }
        binding?.btnSend?.setOnClickListener { validation() }

        binding?.ivImage?.setOnClickListener {
            openPickupGallery()
        }

        return binding?.root!!
    }

    private fun openPickupGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }


    private fun validation() {

        subject = binding?.etEmailSubject?.text.toString()
        body = binding?.etMessage?.text.toString()

        if (workerType?.isNullOrEmpty() == true) {
            mActivity.showToast("Please Select Worker Type")
        } else if (subject?.isNullOrEmpty() == true) {
            binding?.etEmailSubject?.error = "Required"
        } else if (body?.isNullOrEmpty() == true) {
            binding?.etMessage?.error = "Required"
        } else {
            if (file == null) {
                generateEmailWithFile(workerType, subject, body, fileResponsePath)
            } else {
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        showLoading()
        NetworkClass.callFileUpload(URLApi.addDocument(nature = "email_medias"),
            mData!!, "uploadedFiles[]", object : Response {
                override fun onSuccessResponse(response: String?, message: String) {

                    json = JSONArray(response ?: "")
                    media = json.toString()
                    Log.d("mediaPic", media!!)
                    val data = generateList(json.toString(), Array<MediaModel>::class.java)
                    fileResponsePath = data?.get(0)?.path;
                    mActivity.showToast(message ?: "")
                    generateEmail(workerType, subject, body, fileResponsePath)

                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    mActivity.showToast(error ?: "")
                    mData.clear()
                }

            })
    }


    private fun generateEmailWithFile(
        workerType: String?,
        subject: String?,
        body: String?,
        fileResponsePath: String?
    ) {


    }

    private fun generateEmail(
        workerType: String?,
        subject: String?,
        body: String?,
        fileResponsePath: String?
    ) {
        mActivity.showLoading()
        NetworkClass.callApi(
            URLApi.generateGeneralEmail(
                worker_type = workerType,
                subject = subject,
                body = body,
                email_media = fileResponsePath
            ), object : Response {
                override fun onSuccessResponse(response: String?, message: String) {
                    mActivity.hideLoading()
                    mActivity.showToast("Email Sent Successfully")
                    dismiss()
                }

                override fun onErrorResponse(error: String?, response: String?) {
                    hideLoading()
                    mActivity.showToast(error ?: "error   error")
                    print(error + " Error Error")
                    print(error + " Error Error")
                }

            })
    }

    private fun getWorkerTypeList() {
        showLoading()
        NetworkClass.callApi(URLApi.getListWorker(), object : Response {
            override fun onSuccessResponse(response: String?, message: String) {
                mActivity.hideLoading()
                mWorkerData.clear()
                val data =
                    generateList(response.toString() ?: "", Array<String>::class.java)
                mWorkerData.addAll(data)
//                val array = ArrayList<String>()
//                array.add("All")
//                array.addAll(mWorkerData)


                binding?.spService?.setItems(mWorkerData)
            }

            override fun onErrorResponse(error: String?, response: String?) {
                mActivity.hideLoading()
                mActivity.showToast(error ?: "")
            }

        })
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
        dialog?.window?.setLayout(
            (getScreenWidth(mActivity) * .9).toInt(),
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
        ): GenerateEmailDialog {
            val args = Bundle()
            val fragment = GenerateEmailDialog()
            fragment.arguments = args
            return fragment
        }

    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            AsyncTask.THREAD_POOL_EXECUTOR.execute {
                file = FileUtilityClass.getFileFromUri(mActivity, uri!!)
                mData.clear()
                mData.add(file!!)
                mActivity.runOnUiThread {
                    binding?.ivImage?.setImageURI(uri)
                }

            }

        }


    }

    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? {
        val wrapper = ContextWrapper(requireContext())

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, fileNameToSave)

        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return file
    }

}


object FileUtilityClass {
    fun getFileFromUri(mContxt: Context, selectedImageUri: Uri): File? {
//        val selectedImageUri = result!![0].uri
        val parcelFileDescriptor =
            mContxt.contentResolver.openFileDescriptor(selectedImageUri, "r", null)
                ?: return null
        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(mContxt.cacheDir, mContxt.contentResolver.getFileName(selectedImageUri))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        return file
    }

}


fun ContentResolver.getFileName(fileUri: Uri): String {
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}