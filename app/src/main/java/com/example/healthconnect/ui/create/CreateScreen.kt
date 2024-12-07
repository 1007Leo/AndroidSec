package com.example.healthconnect.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.healthconnect.R
import com.example.healthconnect.TopAppBar
import com.example.healthconnect.data.HealthConnectProvider
import com.example.healthconnect.ui.navigation.NavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object CreateDestination : NavigationDestination {
    override val route = "create"
    override val titleRes = R.string.create_screen_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    createViewModel: CreateViewModel,
    healthConnectProvider: HealthConnectProvider,
) {
    Scaffold (
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = stringResource(CreateDestination.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
    ) { innerPadding ->
        CreateBody(
            navigateBack = navigateBack,
            onNavigateUp = onNavigateUp,
            createViewModel = createViewModel,
            healthConnectProvider = healthConnectProvider,
            contentPadding = innerPadding,
        )
    }
}

@Composable
fun CreateBody(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    createViewModel: CreateViewModel = CreateViewModel(),
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
            onItemSelected = { createViewModel.updateUiState(createViewModel.itemUiState.copy(type = it)) }
        )
        ItemInputForm(
            createViewModel.itemUiState,
            createViewModel::updateUiState,
        )
        Button(
            onClick = {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        when (createViewModel.itemUiState.type) {
                            DataTypes.STEPS -> {
                                healthConnectProvider.insertSteps(createViewModel.itemUiState.steps.toLong())
                            }
                            DataTypes.DISTANCE -> {
                                healthConnectProvider.insertDistance(createViewModel.itemUiState.distance.toDouble())
                            }
                            DataTypes.WEIGHT -> {
                                healthConnectProvider.insertWeight(createViewModel.itemUiState.weight.toDouble())
                            }
                        }
                    }
                }
                onNavigateUp()
            },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save_item_button))
        }
    }
}

@Composable
fun EnumDropdownMenu(
    items: Array<DataTypes>,
    modifier: Modifier = Modifier,
    onItemSelected: (DataTypes) -> Unit,
    enabled: Boolean = true,
    initialSelected: DataTypes = DataTypes.STEPS,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(initialSelected) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            content = { Text(selectedItem.displayName) },
            enabled = enabled
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        selectedItem = item
                        onItemSelected(item)
                        expanded = false
                    },
                ) {
                    Text(item.displayName)
                }
            }
        }
    }
}

@Composable
fun ItemInputForm(
    itemUiState: ItemUiState,
    onValueChange: (ItemUiState) -> Unit = {},
) {
    when (itemUiState.type) {
        DataTypes.STEPS -> {
            OutlinedTextField(
                value = itemUiState.steps,
                label = { Text("Steps") },
                onValueChange = {
                    onValueChange(itemUiState.copy(steps = it))
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        DataTypes.DISTANCE -> {
            OutlinedTextField(
                value = itemUiState.distance,
                label = { Text("Distance") },
                onValueChange = {
                    onValueChange(itemUiState.copy(distance = it))
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        DataTypes.WEIGHT -> {
            OutlinedTextField(
                value = itemUiState.weight,
                label = { Text("Weight") },
                onValueChange = {
                    onValueChange(itemUiState.copy(weight = it))
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}