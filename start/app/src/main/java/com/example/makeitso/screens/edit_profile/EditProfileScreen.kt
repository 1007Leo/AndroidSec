package com.example.makeitso.screens.edit_profile

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
@ExperimentalMaterialApi
fun EditProfileScreen(
    popUpScreen: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user
    val activity = LocalContext.current as AppCompatActivity

    EditProfileScreenContent(
//        task = task,
        onDoneClick = { viewModel.onDoneClick(popUpScreen) },
//        onTitleChange = viewModel::onTitleChange,
//        onDescriptionChange = viewModel::onDescriptionChange,
//        onUrlChange = viewModel::onUrlChange,
//        onDateChange = viewModel::onDateChange,
//        onTimeChange = viewModel::onTimeChange,
//        onPriorityChange = viewModel::onPriorityChange,
//        onFlagToggle = viewModel::onFlagToggle,
        activity = activity
    )
}

@Composable
@ExperimentalMaterialApi
fun EditProfileScreenContent(
    modifier: Modifier = Modifier,
//    task: Task,
    onDoneClick: () -> Unit,
//    onTitleChange: (String) -> Unit,
//    onDescriptionChange: (String) -> Unit,
//    onUrlChange: (String) -> Unit,
//    onDateChange: (Long) -> Unit,
//    onTimeChange: (Int, Int) -> Unit,
//    onPriorityChange: (String) -> Unit,
//    onFlagToggle: (String) -> Unit,
    activity: AppCompatActivity?
) {
//    Column(
//        modifier = modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState()),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        ActionToolbar(
//            title = R.string.edit_task,
//            modifier = Modifier.toolbarActions(),
//            secondActionIcon = null,
//            secondAction = {},
//            endActionIcon = R.drawable.ic_check,
//            endAction = { onDoneClick() }
//        )
//
//        Spacer(modifier = Modifier.spacer())
//
//        val fieldModifier = Modifier.fieldModifier()
//        BasicField(R.string.title, task.title, onTitleChange, fieldModifier)
//        BasicField(R.string.description, task.description, onDescriptionChange, fieldModifier)
//        BasicField(R.string.url, task.url, onUrlChange, fieldModifier)
//
//        Spacer(modifier = Modifier.spacer())
//        CardEditors(task, onDateChange, onTimeChange, activity)
//        CardSelectors(task, onPriorityChange, onFlagToggle)
//
//        Spacer(modifier = Modifier.spacer())
//    }
}