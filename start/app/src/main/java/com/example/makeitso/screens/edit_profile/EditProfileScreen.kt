package com.example.makeitso.screens.edit_profile

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.makeitso.common.composable.ActionToolbar
import com.example.makeitso.common.composable.BasicField
import com.example.makeitso.common.composable.BasicFieldImmutable
import com.example.makeitso.common.composable.RegularCardEditor
import com.example.makeitso.common.ext.card
import com.example.makeitso.common.ext.fieldModifier
import com.example.makeitso.common.ext.spacer
import com.example.makeitso.common.ext.toolbarActions
import com.example.makeitso.model.User
import com.example.makeitso.model.service.impl.AccountServiceImpl.Companion.MAIL_LOGIN_TYPE
import com.example.makeitso.theme.MakeItSoTheme
import com.google.android.material.datepicker.MaterialDatePicker
import com.example.makeitso.R.string as AppText
import com.example.makeitso.R.drawable as AppIcon

@Composable
@ExperimentalMaterialApi
fun EditProfileScreen(
    popUpScreen: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user
    val activity = LocalContext.current as AppCompatActivity

    EditProfileScreenContent(
        user = user,
        onDoneClick = { viewModel.onDoneClick(popUpScreen) },
        onNameChange = viewModel::onNameChange,
        onBirthDateChange = viewModel::onBirthDateChane,
        activity = activity
    )
}

@Composable
@ExperimentalMaterialApi
fun EditProfileScreenContent(
    modifier: Modifier = Modifier,
    user: User,
    onDoneClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onBirthDateChange: (Long) -> Unit,
    activity: AppCompatActivity?
) {
    Column(
        modifier = modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionToolbar(
            title = AppText.edit_profile,
            modifier = Modifier.toolbarActions(),
            secondActionIcon = null,
            secondAction = {},
            endActionIcon = AppIcon.ic_check,
            endAction = { onDoneClick() }
        )

        Spacer(modifier = Modifier.spacer())

        val fieldModifier = Modifier.fieldModifier()

        BasicField(AppText.user_name, user.name, onNameChange, fieldModifier)
        CardEditors(user, onBirthDateChange, activity)

        Spacer(modifier = Modifier.spacer())

        BasicFieldImmutable(AppText.user_login, user.login, fieldModifier)
        BasicFieldImmutable(AppText.user_auth_method, user.authMethod, fieldModifier)
    }
}

@ExperimentalMaterialApi
@Composable
private fun CardEditors(
    user: User,
    onDateChange: (Long) -> Unit,
    activity: AppCompatActivity?
) {
    RegularCardEditor(AppText.user_birth_date, AppIcon.ic_calendar, user.birthdate, Modifier.card()) {
        showDatePicker(activity, onDateChange)
    }
}

private fun showDatePicker(activity: AppCompatActivity?, onDateChange: (Long) -> Unit) {
    val picker = MaterialDatePicker.Builder.datePicker().build()

    activity?.let {
        picker.show(it.supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { timeInMillis -> onDateChange(timeInMillis) }
    }
}

@Preview(showBackground = true)
@ExperimentalMaterialApi
@Composable
fun EditProfileScreenPreview() {
    val user = User(
        name = "Name Surname",
        login = "test@mail.ru",
        authMethod = MAIL_LOGIN_TYPE,
    )

    MakeItSoTheme {
        EditProfileScreenContent(
            user = user,
            onDoneClick = { },
            onNameChange = { },
            onBirthDateChange = { },
            activity = null
        )
    }
}