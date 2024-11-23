package com.example.lab5

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.lab5.ui.theme.Lab5Theme
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    var grantedWritePermissions = false

    private val WRITE_PERMISSION_REQUEST = 1111

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            WRITE_PERMISSION_REQUEST ->
                if (resultCode == RESULT_OK) {
                    grantedWritePermissions = true
                } else {
                    grantedWritePermissions = false
                }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    fun grantPermissions(context: Context, uri: Uri) {
        val urisToModify = listOf(uri)
        val editPendingIntent = MediaStore.createWriteRequest(context.contentResolver, urisToModify)

        ActivityCompat.startIntentSenderForResult(
            this,
            editPendingIntent.intentSender,
            WRITE_PERMISSION_REQUEST,
            null,
            0,
            0,
            0,
            null,
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()
        val permissionsToRequest = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_IMAGES)
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(android.Manifest.permission.ACCESS_MEDIA_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // All permissions are granted
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab5Theme {
                ImageMetadataEditApp()
            }
        }
    }
}