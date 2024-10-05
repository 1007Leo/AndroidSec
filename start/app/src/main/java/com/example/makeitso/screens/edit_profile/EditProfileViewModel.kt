package com.example.makeitso.screens.edit_profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.example.makeitso.model.User
import com.example.makeitso.model.service.AccountService
import com.example.makeitso.model.service.LogService
import com.example.makeitso.model.service.StorageService
import com.example.makeitso.screens.MakeItSoViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    //savedStateHandle: SavedStateHandle,
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

    fun onDoneClick(popUpScreen: () -> Unit) {
//        launchCatching {
//            val editedTask = task.value
//            if (editedTask.id.isBlank()) {
//                storageService.save(editedTask)
//            } else {
//                storageService.update(editedTask)
//            }
//            popUpScreen()
//        }
    }
}