package com.example.healthconnect.ui.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.records.Record
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.healthconnect.data.ItemsRepository
import java.time.Instant

class HomeViewModel(createSavedStateHandle: SavedStateHandle, itemsRepository: ItemsRepository) : ViewModel() {
    var selectionStart: MutableState<Instant> = mutableStateOf(Instant.MIN)
    var selectionEnd: MutableState<Instant> = mutableStateOf(Instant.MAX)

    val homeUiState = itemsRepository.itemsListState

    fun updateUiState(records: List<Record>) {
        homeUiState.value = records
    }
}