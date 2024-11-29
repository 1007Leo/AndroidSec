package com.example.healthconnect.data

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
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import com.example.healthconnect.ui.create.ItemUiState
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

    suspend fun insertItem(item: ItemUiState) {
        try {
            val records = listOf(
                getStepsRecord(item.steps.toLong()),
                getDistanceRecord(item.distance.toDouble()),
                getWeightRecord(item.weight.toDouble()))
            _client?.insertRecords(records)
        } catch (e: Exception) {

        }
    }

    private fun getStepsRecord(steps: Long): StepsRecord {
        return StepsRecord(
            count = steps,
            startTime = Instant.now(),
            endTime = Instant.ofEpochSecond(Instant.now().epochSecond + 1),
            startZoneOffset = null,
            endZoneOffset = null,
        )
    }

    private fun getDistanceRecord(distanceKilometers: Double): DistanceRecord {
        return DistanceRecord(
            distance = Length.kilometers(distanceKilometers),
            startTime = Instant.now(),
            endTime = Instant.ofEpochSecond(Instant.now().epochSecond + 1),
            startZoneOffset = null,
            endZoneOffset = null,
        )
    }

    private fun getWeightRecord(weightKilograms: Double): WeightRecord {
        return WeightRecord(
            weight = Mass.kilograms(weightKilograms),
            time = Instant.now(),
            zoneOffset = null,
        )
    }

    suspend fun insertSteps(steps: Long) {
        try {
            val stepsRecord = StepsRecord(
                count = steps,
                startTime = Instant.now(),
                endTime = Instant.ofEpochSecond(10000),
                startZoneOffset = null,
                endZoneOffset = null,
            )
            _client?.insertRecords(listOf(stepsRecord))
        } catch (e: Exception) {
            // Run error handling here
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