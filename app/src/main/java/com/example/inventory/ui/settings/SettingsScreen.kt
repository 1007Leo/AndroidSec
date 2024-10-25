package com.example.inventory.ui.settings


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.inventory.InventoryTopAppBar
import com.example.inventory.R
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object SettingsDestination : NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: SettingsViewModel,
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            InventoryTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                canNavigateBack = canNavigateBack,
                canNavigateSettings = false,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        SettingsBody(
            settingsUiState = viewModel.settingsUiState,
            onSettingsValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveSettings()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding()
                )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun SettingsBody(
    settingsUiState: SettingsUiState,
    onSettingsValueChange: (SettingsUiState) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    ) {
        SettingsInput(
            settingsUiState = settingsUiState,
            onValueChange = onSettingsValueChange
        )
        Button(
            onClick = onSaveClick,
            enabled = settingsUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_action))
        }
    }
}

@Composable
fun SettingsInput(
    settingsUiState: SettingsUiState,
    onValueChange: (SettingsUiState) -> Unit = {},
) {
    Column {
        CheckboxWithLabel(
            checked = settingsUiState.privateDataOption,
            onCheckedChange = {
                onValueChange(settingsUiState.copy(privateDataOption = it))
            },
            label = stringResource(R.string.hide_private_data)
        )
        CheckboxWithLabel(
            checked = settingsUiState.shareOption,
            onCheckedChange = {
                onValueChange(settingsUiState.copy(shareOption = it))
            },
            label = stringResource(R.string.forbid_share)
        )
        CheckboxWithLabel(
            checked = settingsUiState.defaultCountOption,
            onCheckedChange = {
                onValueChange(settingsUiState.copy(defaultCountOption = it))
            },
            label = stringResource(R.string.default_item_count)
        )
        OutlinedTextField(
            value = settingsUiState.defaultCountValue,
            onValueChange = {
                onValueChange(settingsUiState.copy(
                    defaultCountValue = it,
                    isEntryValid = SettingsViewModel.validateCount(it)))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            isError = !settingsUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth(),
            enabled = settingsUiState.defaultCountOption,
            singleLine = true
        )
    }
}

@Composable
fun CheckboxWithLabel(checked: Boolean, onCheckedChange: (Boolean) -> Unit, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = label)
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun SettingsScreenPreview() {
//    InventoryTheme {
//        SettingsBody(
//            viewModel = viewModel(),
//            onSaveClick = {}
//        )
//    }
//}
