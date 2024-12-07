package com.example.healthconnect.data

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.deleteRecords
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import java.time.Instant

class HealthConnectProvider {
    var permissionsGranted = false
    val PERMISSIONS =
        setOf(
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

    fun createClient(context: Context) {
        val availabilityStatus = HealthConnectClient.getSdkStatus(context, "com.google.android.apps.healthdata")
        if (availabilityStatus == HealthConnectClient.SDK_UNAVAILABLE) {
            _client = null
        }
        _client = HealthConnectClient.getOrCreate(context)
    }

    suspend fun insertSteps(steps: Long) {
        try {
            val sr = StepsRecord(
                count = steps,
                startTime = Instant.now(),
                endTime = Instant.ofEpochSecond(Instant.now().epochSecond + 1),
                startZoneOffset = null,
                endZoneOffset = null,
            )
            _client?.insertRecords(listOf(sr))
        } catch (e: Exception) {

        }
    }
    suspend fun insertDistance(distanceKilometers: Double) {
        try {
            val dr = DistanceRecord(
                distance = Length.kilometers(distanceKilometers),
                startTime = Instant.now(),
                endTime = Instant.ofEpochSecond(Instant.now().epochSecond + 1),
                startZoneOffset = null,
                endZoneOffset = null,
            )
            _client?.insertRecords(listOf(dr))
        } catch (e: Exception) {

        }
    }
    suspend fun insertWeight(weightKilograms: Double) {
        try {
            val wr = WeightRecord(
                weight = Mass.kilograms(weightKilograms),
                time = Instant.now(),
                zoneOffset = null,
            )
            _client?.insertRecords(listOf(wr))
        } catch (e: Exception) {

        }
    }

    suspend fun readRecordsByTimeRange(start: Instant, end: Instant): List<Record> {
        val res = mutableListOf<Record>()
        try {
            res.addAll(readStepsByTimeRange(start, end))
            res.addAll(readDistanceByTimeRange(start, end))
            res.addAll(readWeightByTimeRange(start, end))
        } catch (e: Exception) {
            // Run error handling here.
        }
        return res
    }

    private suspend fun readStepsByTimeRange(start: Instant, end: Instant): List<StepsRecord> {
        val res = mutableListOf<StepsRecord>()
        try {
            val response =
                _client!!.readRecords(
                    ReadRecordsRequest(
                        StepsRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(start, end)
                    )
                )
            for (stepRecord in response.records) {
                res.add(stepRecord)
            }
        } catch (e: Exception) {
            // Run error handling here.
        }
        return res
    }
    private suspend fun readDistanceByTimeRange(start: Instant, end: Instant): List<DistanceRecord> {
        val res = mutableListOf<DistanceRecord>()
        try {
            val response =
                _client!!.readRecords(
                    ReadRecordsRequest(
                        DistanceRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(start, end)
                    )
                )
            for (distanceRecord in response.records) {
                res.add(distanceRecord)
            }
        } catch (e: Exception) {
            // Run error handling here.
        }
        return res
    }
    private suspend fun readWeightByTimeRange(start: Instant, end: Instant): List<WeightRecord> {
        val res = mutableListOf<WeightRecord>()
        try {
            val response =
                _client!!.readRecords(
                    ReadRecordsRequest(
                        WeightRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(start, end)
                    )
                )
            for (weightRecord in response.records) {
                res.add(weightRecord)
            }
        } catch (e: Exception) {
            // Run error handling here.
        }
        return res
    }

    suspend fun updateRecord(newRecord: Record) {
        try {
            _client!!.updateRecords(listOf(newRecord))
        } catch (e: Exception) {

        }
    }

    suspend fun deleteRecord(record: Record) {
        try {
            when (record::class) {
                StepsRecord::class -> {
                    _client!!.deleteRecords<StepsRecord>(listOf(record.metadata.id), emptyList())
                }
                WeightRecord::class -> {
                    _client!!.deleteRecords<WeightRecord>(listOf(record.metadata.id), emptyList())
                }
                DistanceRecord::class -> {
                    _client!!.deleteRecords<DistanceRecord>(listOf(record.metadata.id), emptyList())
                }
            }
        } catch (e: Exception) {

        }
    }
}