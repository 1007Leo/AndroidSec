package com.example.healthconnect.ui.home

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.records.DistanceRecord
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
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthconnect.ui.AppViewModelProvider

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen (
    navigateToEdit: (String) -> Unit,
    navigateToCreate: () -> Unit,
    viewModel: HomeViewModel  = viewModel(factory = AppViewModelProvider.Factory),
    healthConnectProvider: HealthConnectProvider,
) {
    val homeUiState = viewModel.homeUiState.collectAsState()

    Scaffold (
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
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
        if (viewModel.selectionStart.value != Instant.MIN && viewModel.selectionEnd.value != Instant.MAX) {
            runBlocking {
                withContext(Dispatchers.IO) {
                    val records = healthConnectProvider.readRecordsByTimeRange(
                        viewModel.selectionStart.value,
                        viewModel.selectionEnd.value
                    )
                    viewModel.updateUiState(records)
                }
            }
        }
        HomeBody(
            navigateToEdit = navigateToEdit,
            viewModel = viewModel,
            homeUiState = homeUiState,
            healthConnectProvider = healthConnectProvider,
            contentPadding = innerPadding,
        )
    }
}

@Composable
fun HomeBody(
    navigateToEdit: (String) -> Unit,
    viewModel: HomeViewModel,
    homeUiState: State<List<Record>>,
    healthConnectProvider: HealthConnectProvider,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

    Column (
        modifier = Modifier.fillMaxSize().padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        DateRangePickerSection(viewModel, healthConnectProvider)
        RecordsList(
            recordList = homeUiState.value,
            onRecordClick = { navigateToEdit(it.metadata.id) },
        )
    }
}

@Composable
fun DateRangePickerSection(
    viewModel: HomeViewModel,
    healthConnectProvider: HealthConnectProvider,
) {
    var selectedRangeString by remember { mutableStateOf("Select time period") }
    var showPicker by remember { mutableStateOf(false)}

    var dateStart by remember { viewModel.selectionStart }
    var dateEnd by remember { viewModel.selectionEnd }

    selectedRangeString = datePairToString(
        Pair(dateStart.epochSecond, dateEnd.epochSecond)
    )

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
                    dateStart = Instant.ofEpochSecond((start/1000))
                }
                it.second?.let { end ->
                    dateEnd = Instant.ofEpochSecond((end/1000))
                }
                runBlocking {
                    withContext(Dispatchers.IO) {
                        val records = healthConnectProvider.readRecordsByTimeRange(
                            dateStart,
                            dateEnd,
                        )
                        viewModel.updateUiState(records)
                    }
                }
                showPicker = false
            },
            onDismiss = {showPicker = false}
        )
    }
}

fun datePairToString(datePair: Pair<Long?, Long?>): String {
    if (datePair.first == Instant.MIN.epochSecond || datePair.second == Instant.MAX.epochSecond)
        return "Select time period"
    val formattedDateFirst = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(datePair.first!!*1000)
    val formattedDateSecond = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(datePair.second!!*1000)
    return "From $formattedDateFirst to $formattedDateSecond"
}

@Composable
private fun RecordsList(
    recordList: List<Record>,
    onRecordClick: (Record) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(items = recordList, key = {it.metadata.id}) { record ->
            RecordItem(
                record = record,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { onRecordClick(record) }
            )
        }
    }
}

@Composable
fun RecordItem(record: Record, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        val data = mutableListOf<String>()
        when (record) {
            is StepsRecord -> {
                data.add(0, "Steps")
                data.add(1, record.count.toString())
                data.add(2, SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(record.startTime.toEpochMilli()))
            }
            is DistanceRecord -> {
                data.add(0, "Distance")
                data.add(1, record.distance.inKilometers.toString() + " km")
                data.add(2, SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(record.startTime.toEpochMilli()))
            }
            is WeightRecord -> {
                data.add(0, "Weight")
                data.add(1, record.weight.inKilograms.toString() + " kg")
                data.add(2, SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(record.time.toEpochMilli()))
            }
        }
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = data[0],
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = data[2],
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Text(
                text = data[1],
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}