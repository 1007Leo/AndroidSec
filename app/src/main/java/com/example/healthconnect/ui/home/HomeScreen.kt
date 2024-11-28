package com.example.healthconnect.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.healthconnect.HealthConnect.HealthConnectProvider
import com.example.healthconnect.ui.navigation.NavigationDestination
import com.example.healthconnect.R
import com.example.healthconnect.TopAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (
    navigateToEdit: () -> Unit,
    navigateToCreate: () -> Unit,
    viewModel: HomeViewModel,
    healthConnectProvider: HealthConnectProvider,
) {
    Scaffold (
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
//                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToCreate,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(15.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.new_button)
                )
            }
        },
    ) { innerPadding ->
        HomeBody(
            navigateToEdit = navigateToEdit,
            navigateToNew = navigateToCreate,
            viewModel = viewModel,
            healthConnectProvider = healthConnectProvider,
            contentPadding = innerPadding,
        )
    }
}

@Composable
fun HomeBody(
    navigateToEdit: () -> Unit,
    navigateToNew: () -> Unit,
    viewModel: HomeViewModel = HomeViewModel(),
    healthConnectProvider: HealthConnectProvider,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            onClick = navigateToEdit,
            modifier = Modifier.padding(contentPadding),
        ) {
            Text("Edit")
        }

        Button(
            onClick = {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        healthConnectProvider.insertSteps()
                    }
                }
            },
            //modifier = Modifier.padding(contentPadding),
        ) {
            Text("Add item")
        }
        Button(
            onClick = {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        val cnt = healthConnectProvider.readStepsByTimeRange()
                        print(cnt)
                    }
                }
            },
            //modifier = Modifier.padding(contentPadding),
        ) {
            Text("Get item")
        }
    }
}