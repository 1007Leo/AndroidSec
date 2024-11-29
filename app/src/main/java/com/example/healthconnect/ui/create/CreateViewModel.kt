package com.example.healthconnect.ui.create

import android.content.ClipData.Item
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CreateViewModel: ViewModel() {
    var itemUiState by mutableStateOf(ItemUiState())
        private set

    fun updateUiState(newItemUiState: ItemUiState) {
        itemUiState = newItemUiState.copy()
    }
}

data class ItemUiState(
    val steps: String = "",
    val distance: String = "",
    val weight: String = "",
)