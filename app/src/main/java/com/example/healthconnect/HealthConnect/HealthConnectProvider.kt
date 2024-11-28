package com.example.healthconnect.HealthConnect

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

class HealthConnectProvider {
    var permissionsGranted = false
    val PERMISSIONS =
        setOf(
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getWritePermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(BloodPressureRecord::class),
            HealthPermission.getWritePermission(BloodPressureRecord::class),
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class),
            HealthPermission.getReadPermission(WeightRecord::class),
            HealthPermission.getWritePermission(WeightRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
            HealthPermission.getWritePermission(DistanceRecord::class),
        )

    private var _client: HealthConnectClient? = null

    val client: HealthConnectClient?
        get() = _client

    var items = mutableListOf<HealthItem>()

    fun createClient(context: Context) {
        val availabilityStatus = HealthConnectClient.getSdkStatus(context, "com.google.android.apps.healthdata")
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            _client = null
        }
        _client = HealthConnectClient.getOrCreate(context)
    }

    suspend fun insertSteps() {
        try {
            val stepsRecord = StepsRecord(
                count = 120,
                startTime = Instant.EPOCH,
                endTime = Instant.ofEpochSecond(10000),
                startZoneOffset = null,
                endZoneOffset = null,
            )
            _client?.insertRecords(listOf(stepsRecord))
        } catch (e: Exception) {
            // Run error handling here
            print(e.message)

        }
    }

    suspend fun readStepsByTimeRange(): Long {
        try {
            val response =
                _client!!.readRecords(
                    ReadRecordsRequest(
                        StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(Instant.EPOCH, Instant.ofEpochSecond(10000))
                    )
                )
            for (stepRecord in response.records) {
                // Process each step record
                return stepRecord.count
            }
        } catch (e: Exception) {
            // Run error handling here.
        }
        return 0
    }
}