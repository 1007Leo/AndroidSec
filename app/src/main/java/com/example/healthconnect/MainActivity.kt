package com.example.healthconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import com.example.healthconnect.HealthConnect.HealthConnectProvider
import com.example.healthconnect.ui.theme.HealthConnectTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val healthConnectProvider = HealthConnectProvider()

    private val requestPermissionActivityContract = PermissionController.createRequestPermissionResultContract()

    private val requestPermissions = registerForActivityResult(requestPermissionActivityContract) { granted ->
        if (granted.containsAll(healthConnectProvider.PERMISSIONS)) {
            healthConnectProvider.permissionsGranted = true
        } else {
            healthConnectProvider.permissionsGranted = false
        }
    }

    private suspend fun checkPermissions(healthConnectClient: HealthConnectClient) {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        if (granted.containsAll(healthConnectProvider.PERMISSIONS)) {
            // Permissions already granted; proceed with inserting or reading data
        } else {
            requestPermissions.launch(healthConnectProvider.PERMISSIONS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        healthConnectProvider.createClient(this)

        runBlocking {
            withContext(Dispatchers.IO) {
                healthConnectProvider.client?.let {
                    checkPermissions(it)
                }
            }
        }

        enableEdgeToEdge()
        setContent {
            HealthConnectTheme {
                HealthConnectApp(healthConnectProvider)
            }
        }
    }
}
