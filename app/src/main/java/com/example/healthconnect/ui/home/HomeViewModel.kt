package com.example.healthconnect.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.Instant

class HomeViewModel: ViewModel() {
    var selectionStart: Instant? = null
    var selectionEnd: Instant? = null

}