package com.example.healthconnect.data

import androidx.health.connect.client.records.Record
import kotlinx.coroutines.flow.MutableStateFlow

class ItemsRepository() {
    val itemsListState = MutableStateFlow<List<Record>>(listOf())

    fun getRecordById(id: String): Record? {
        for (record in itemsListState.value) {
            if (record.metadata.id == id)
                return record
        }
        return null
    }
}