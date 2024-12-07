package com.example.healthconnect.ui.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.units.Length
import androidx.health.connect.client.units.Mass
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.healthconnect.data.HealthConnectProvider
import com.example.healthconnect.data.ItemsRepository
import com.example.healthconnect.ui.create.DataTypes
import com.example.healthconnect.ui.create.ItemUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class EditViewModel(
    savedStateHandle: SavedStateHandle,
    itemsRepository: ItemsRepository,
) : ViewModel() {
    var itemWithUiState by mutableStateOf(Pair<Record?, ItemUiState>(null, ItemUiState()))
        private set

    private val itemId: String = checkNotNull(savedStateHandle[EditDestination.itemIdArg])


    init {
        var recordType = DataTypes.STEPS
        val record: Record? = itemsRepository.getRecordById(itemId)
        var steps = ""
        var distance = ""
        var weight = ""
        if (record != null) {
            when (record) {
                is StepsRecord -> {
                    recordType = DataTypes.STEPS
                    steps = record.count.toString()
                }
                is WeightRecord -> {
                    recordType = DataTypes.WEIGHT
                    weight = record.weight.inKilograms.toString()
                }
                is DistanceRecord -> {
                    recordType = DataTypes.DISTANCE
                    distance = record.distance.inKilometers.toString()
                }
            }
        }
        val newItemUiState = ItemUiState(
            type = recordType,
            steps = steps,
            weight = weight,
            distance = distance,

        )
        itemWithUiState = itemWithUiState.copy(first = record, second = newItemUiState)
    }

    fun updateUiState(itemUiState: ItemUiState){
        itemWithUiState = itemWithUiState.copy(second = itemUiState)
    }

    fun updateItem(healthConnectProvider: HealthConnectProvider) {
        var record = itemWithUiState.first
        val uiState = itemWithUiState.second
        if (record != null) {
            when (uiState.type) {
                DataTypes.STEPS -> {
                    record = record as StepsRecord
                    record = StepsRecord(
                        count = uiState.steps.toLong(),
                        startTime = record.startTime,
                        endTime = record.endTime,
                        startZoneOffset = record.startZoneOffset,
                        endZoneOffset = record.endZoneOffset,
                        metadata = record.metadata,
                    )
                }
                DataTypes.WEIGHT -> {
                    record = record as WeightRecord
                    record = WeightRecord(
                        weight = Mass.kilograms(uiState.weight.toDouble()),
                        time = record.time,
                        zoneOffset = record.zoneOffset,
                        metadata = record.metadata,
                    )
                }
                DataTypes.DISTANCE -> {
                    record = record as DistanceRecord
                    record = DistanceRecord(
                        distance = Length.kilometers(uiState.distance.toDouble()),
                        startTime = record.startTime,
                        endTime = record.endTime,
                        startZoneOffset = record.startZoneOffset,
                        endZoneOffset = record.endZoneOffset,
                        metadata = record.metadata,
                    )
                }
            }
            itemWithUiState = itemWithUiState.copy(first = record)
        }
        runBlocking {
            withContext(Dispatchers.IO) {
                itemWithUiState.first?.let { healthConnectProvider.updateRecord(it) }
            }
        }
    }

    fun deleteItem(healthConnectProvider: HealthConnectProvider) {
        runBlocking {
            withContext(Dispatchers.IO) {
                itemWithUiState.first?.let { healthConnectProvider.deleteRecord(it) }
            }
        }
    }
}