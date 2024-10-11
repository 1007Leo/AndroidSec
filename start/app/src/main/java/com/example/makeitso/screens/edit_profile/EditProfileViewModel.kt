package com.example.makeitso.screens.edit_profile

import androidx.compose.runtime.mutableStateOf
import com.example.makeitso.model.User
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    logService: LogService,
    private val accountService: AccountService,
) : MakeItSoViewModel(logService) {

    val user = mutableStateOf(User())

    init {
        if (accountService.hasUser) {
            launchCatching {
                user.value = accountService.getCurrentUserData()
            }
        }
    }

    fun onNameChange(newValue: String) {
        user.value = user.value.copy(name = newValue)
    }

    fun onBirthDateChane(newValue: Long) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC))
        calendar.timeInMillis = newValue
        val newDueDate = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(calendar.time)
        user.value = user.value.copy(birthdate = newDueDate)
    }

    fun onDoneClick(popUpScreen: () -> Unit) {
        launchCatching {
            val editedUser = user.value
            if (editedUser.userId.isBlank()) {
                accountService.saveCurrentUserData(editedUser)
            } else {
                accountService.updateCurrentUserData(editedUser)
            }
            popUpScreen()
        }
    }
    companion object {
        private const val UTC = "UTC"
        private const val DATE_FORMAT = "EEE, d MMM yyyy"
    }
}