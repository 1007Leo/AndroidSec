package com.example.healthconnect.ui.home

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthconnect.data.HealthConnectProvider
import com.example.healthconnect.ui.navigation.NavigationDestination
import com.example.healthconnect.R
import com.example.healthconnect.TopAppBar
import com.example.healthconnect.ui.common.DateRangePickerModal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.Locale

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
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        DateRangePickerSection(viewModel)
//        Button(
//            onClick = navigateToEdit,
//            modifier = Modifier.padding(contentPadding),
//        ) {
//            Text("Edit")
//        }
//
//        Button(
//            onClick = {
//                runBlocking {
//                    withContext(Dispatchers.IO) {
//                        healthConnectProvider.insertSteps()
//                    }
//                }
//            },
//            //modifier = Modifier.padding(contentPadding),
//        ) {
//            Text("Add item")
//        }
//        Button(
//            onClick = {
//                runBlocking {
//                    withContext(Dispatchers.IO) {
//                        val cnt = healthConnectProvider.readStepsByTimeRange()
//                        print(cnt)
//                    }
//                }
//            },
//            //modifier = Modifier.padding(contentPadding),
//        ) {
//            Text("Get item")
//        }
    }
}

@Composable
fun DateRangePickerSection(
    viewModel: HomeViewModel
) {
    var selectedRangeString by remember { mutableStateOf("Select time period") }
    var showPicker by remember { mutableStateOf(false)}
    selectedRangeString = datePairToString(Pair(viewModel.selectionStart?.epochSecond, viewModel.selectionEnd?.epochSecond))
    OutlinedButton(
        onClick = {showPicker = true},
        modifier = Modifier.fillMaxWidth().padding(5.dp),
        content = {Text(selectedRangeString) },
    )
    if (showPicker) {
        DateRangePickerModal(
            onDateRangeSelected = {
                selectedRangeString = datePairToString(it)
                it.first?.let { start ->
                    viewModel.selectionStart = Instant.ofEpochSecond(start)
                }
                it.second?.let { end ->
                    viewModel.selectionEnd = Instant.ofEpochSecond(end)
                }

                showPicker = false
            },
            onDismiss = {showPicker = false}
        )
    }
}

fun datePairToString(datePair: Pair<Long?, Long?>): String {
    if (datePair.first == null || datePair.second == null)
        return "Select time period"
    val formattedDateFirst = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(datePair.first)
    val formattedDateSecond = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(datePair.second)
    return "From $formattedDateFirst to $formattedDateSecond"
}