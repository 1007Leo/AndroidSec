package com.example.inventory.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.launch

data class SettingsUiState (
    val privateDataOption: Boolean = false,
    val shareOption: Boolean = false,
    val defaultCountOption: Boolean = false,
    val defaultCountValue: String = "0",
    val isEntryValid: Boolean = true,
)

class SettingsViewModel(context: Context) : ViewModel() {

    var settingsUiState by mutableStateOf(SettingsUiState())
        private set

    fun updateUiState(settings: SettingsUiState) {
        settingsUiState = settings.copy()
    }

    private val sharedPreferences = createEncryptedSharedPreferences(context)

    private val KEY_PRIVATE_DATA_OPTION = "private_data_option"
    private val KEY_SHARE_OPTION = "share_option"
    private val KEY_DEFAULT_COUNT_OPTION = "default_count_option"
    private val KEY_DEFAULT_COUNT_VALUE = "default_count_value"

    var privateDataOption: Boolean
        get() = sharedPreferences.getBoolean(KEY_PRIVATE_DATA_OPTION, false)
        set(value) {
            saveOption(KEY_PRIVATE_DATA_OPTION, value)
        }

    var shareOption: Boolean
        get() = sharedPreferences.getBoolean(KEY_SHARE_OPTION, false)
        set(value) {
            saveOption(KEY_SHARE_OPTION, value)
        }

    var defaultCountOption: Boolean
        get() = sharedPreferences.getBoolean(KEY_DEFAULT_COUNT_OPTION, false)
        set(value) {
            saveOption(KEY_DEFAULT_COUNT_OPTION, value)
        }

    var defaultCountValue: String
        get() = sharedPreferences.getString(KEY_DEFAULT_COUNT_VALUE, "0")?: "0"
        set(value) {
            saveOption(KEY_DEFAULT_COUNT_VALUE, value)
        }

    init {
        settingsUiState = SettingsUiState(privateDataOption, shareOption, defaultCountOption, defaultCountValue, validateCount(defaultCountValue))
    }

    private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "settings_prefs",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveSettings() {
        privateDataOption = settingsUiState.privateDataOption
        shareOption = settingsUiState.shareOption
        defaultCountOption = settingsUiState.defaultCountOption
        defaultCountValue = settingsUiState.defaultCountValue
    }

    private fun saveOption(key: String, value: Boolean) {
        viewModelScope.launch {
            sharedPreferences.edit().putBoolean(key, value).apply()
        }
    }
    private fun saveOption(key: String, value: String) {
        viewModelScope.launch {
            sharedPreferences.edit().putString(key, value).apply()
        }
    }

    companion object {
        fun validateCount(count: String): Boolean {
            val floatRegex = "^(0|[1-9][0-9]*)\$".toRegex()
            return floatRegex.matches(count)
        }
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}