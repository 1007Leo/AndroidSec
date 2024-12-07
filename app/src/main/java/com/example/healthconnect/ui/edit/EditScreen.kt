package com.example.healthconnect.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthconnect.ui.navigation.NavigationDestination
import com.example.healthconnect.R
import com.example.healthconnect.TopAppBar
import com.example.healthconnect.data.HealthConnectProvider
import com.example.healthconnect.ui.AppViewModelProvider
import com.example.healthconnect.ui.create.DataTypes
import com.example.healthconnect.ui.create.EnumDropdownMenu
import com.example.healthconnect.ui.create.ItemInputForm

object EditDestination : NavigationDestination {
    override val route = "edit"
    override val titleRes = R.string.edit_screen_name
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    healthConnectProvider: HealthConnectProvider,
    editViewModel: EditViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    Scaffold (
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = stringResource(EditDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
    ) { innerPadding ->
        EditBody(
            navigateBack = navigateBack,
            onNavigateUp = onNavigateUp,
            editViewModel = editViewModel,
            healthConnectProvider = healthConnectProvider,
            contentPadding = innerPadding,
        )
    }
}

@Composable
fun EditBody(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    editViewModel: EditViewModel,
    healthConnectProvider: HealthConnectProvider,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(contentPadding)
            .padding(start = 5.dp, end = 5.dp)
    ) {

        EnumDropdownMenu(
            items = DataTypes.entries.toTypedArray(),
            enabled = false,
            onItemSelected = {
                editViewModel.updateUiState(editViewModel.itemWithUiState.second.copy(type = it))
            },
            initialSelected = editViewModel.itemWithUiState.second.type
        )
        ItemInputForm(
            editViewModel.itemWithUiState.second,
            editViewModel::updateUiState,
        )
        Button(
            onClick = {
                editViewModel.updateItem(healthConnectProvider)
                onNavigateUp()
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_item_button))
        }
        Button(
            onClick = {
                editViewModel.deleteItem(healthConnectProvider)
                onNavigateUp()
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.delete_item_button))
        }
    }
}