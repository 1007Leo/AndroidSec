package com.example.healthconnect.data

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord

data class HealthItem(
//    val bloodPressure: BloodPressureRecord,
//    val heartRate: HeartRateRecord,
    val steps: StepsRecord,
    val distance: DistanceRecord,
    val weight: WeightRecord,
)