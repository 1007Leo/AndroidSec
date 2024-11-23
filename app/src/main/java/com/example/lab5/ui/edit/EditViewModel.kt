package com.example.lab5.ui.edit

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.ViewModel

class EditViewModel() : ViewModel() {
    fun saveExifData(context: Context, uri: Uri, newExifData: Map<String, String>) {
        val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "rw")
        fileDescriptor?.use { parcelFileDescriptor ->
            val exifInterface = ExifInterface(parcelFileDescriptor.fileDescriptor)
            for ((key, value) in newExifData) {
                exifInterface.setAttribute(key, value)
            }
            exifInterface.saveAttributes()
            fileDescriptor.close()
        }
    }
}