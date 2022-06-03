package com.kodextech.project.kodexlib.utils


import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import com.kodextech.project.kodexlib.R
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun Context.getTempFile(
    fileName: String = "File_${UUID.randomUUID()}",
    fileExtension: String
): File {
    return File.createTempFile(fileName, fileExtension, cacheDir)
}

fun Context.getFileName(uri: Uri): String? {
    return contentResolver?.getFileName(uri)
}

fun Context.saveImageToExternalStorageAndGetFileUri(
    bitmap: Bitmap?,
    fileName: String = "Time_${Date().time}",
    fileExtension: String = ".png"
): Uri? {
    var uri: Uri? = null
    var outputStream: FileOutputStream? = null
    try {
        val mPath = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${this.getString(R.string.app_name)}_${fileName}${fileExtension}"
        )
        outputStream = FileOutputStream(mPath)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        uri = Uri.fromFile(mPath)
        uri = FileProvider.getUriForFile(
            this,
            this.packageName.toString() + ".provider",
            uri.toFile()
        )
    } catch (e: Exception) {
        Log.e(
            this::class.java.simpleName,
            "Exception while saving image to external storage: ${e.message}"
        )
    } finally {
        outputStream?.close()
    }
    return uri
}

fun Context.shareUri(type: String = "image/*", photoURI: Uri?) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = type
    shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI)
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    startActivity(Intent.createChooser(shareIntent, "Share"))
}

fun Context.getBitmapFromView(view: View, height: Int, width: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val bgDrawable = view.background
    if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
    view.draw(canvas)
    return bitmap
}

/*// Scroll Able Content*/
fun Context.getBitmapFromView(view: ViewGroup): Bitmap {
    val bitmap = Bitmap.createBitmap(
        view.getChildAt(0).width,
        view.getChildAt(0).height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    val bgDrawable = view.background
    if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
    view.draw(canvas)
    return bitmap
}

fun ContentResolver.getFileName(fileUri: Uri): String {

    var name = ""
    if (fileUri.scheme.toString() == "content") {
        val returnCursor = this.query(fileUri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
    } else if (fileUri.scheme.toString() == "file") {
        name = fileUri.lastPathSegment.toString()
    } else {
        name = "unknwn_" + fileUri.lastPathSegment.toString()
    }
    return name
}