package com.example.healthconnect.ui.create

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

enum class DataTypes(val displayName: String) {
    STEPS("Steps"),
    DISTANCE("Distance"),
    WEIGHT("Weight")
}

data class ItemUiState(
    val type: DataTypes = DataTypes.STEPS,
    val steps: String = "",
    val distance: String = "",
    val weight: String = "",
)