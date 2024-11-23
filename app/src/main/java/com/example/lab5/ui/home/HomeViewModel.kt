package com.example.lab5.ui.home

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.ViewModel


class HomeViewModel() : ViewModel() {
    var imageUri: Uri? = null
    lateinit var exifData: Map<String, String>

    fun getExifData(context: Context, uri: Uri): Map<String, String> {
        val exifData = mutableMapOf<String, String>()

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val exifInterface = ExifInterface(inputStream)
            exifData[ExifInterface.TAG_DATETIME] = exifInterface.getAttribute(ExifInterface.TAG_DATETIME) ?: ""
            exifData[ExifInterface.TAG_GPS_LATITUDE] = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE) ?: ""
            exifData[ExifInterface.TAG_GPS_LONGITUDE] = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) ?: ""
            exifData[ExifInterface.TAG_MAKE] = exifInterface.getAttribute(ExifInterface.TAG_MAKE) ?: ""
            exifData[ExifInterface.TAG_MODEL] = exifInterface.getAttribute(ExifInterface.TAG_MODEL) ?: ""
            inputStream.close()
        }

        return exifData
    }
}