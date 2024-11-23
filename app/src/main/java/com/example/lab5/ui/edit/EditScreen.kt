package com.example.lab5.ui.edit

import android.media.ExifInterface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.lab5.ui.home.HomeViewModel
import com.example.lab5.ui.navigation.NavigationDestination
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lab5.MainActivity
import com.example.lab5.R.string
import com.example.lab5.TopAppBar

object EditDestination : NavigationDestination {
    override val route = "edit"
    override val titleRes = string.edit_screen_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    homeViewModel: HomeViewModel,
    editViewModel: EditViewModel = EditViewModel(),
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
            homeViewModel = homeViewModel,
            editViewModel = editViewModel,
            contentPadding = innerPadding,
        )
    }
}

@Composable
fun EditBody(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    homeViewModel: HomeViewModel,
    editViewModel: EditViewModel = EditViewModel(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current

    var date by remember { mutableStateOf(homeViewModel.exifData[ExifInterface.TAG_DATETIME] ?: "") }
    var latitude by remember { mutableStateOf(homeViewModel.exifData[ExifInterface.TAG_GPS_LATITUDE] ?: "") }
    var longitude by remember { mutableStateOf(homeViewModel.exifData[ExifInterface.TAG_GPS_LONGITUDE] ?: "") }
    var device by remember { mutableStateOf(homeViewModel.exifData[ExifInterface.TAG_MAKE] ?: "") }
    var model by remember { mutableStateOf(homeViewModel.exifData[ExifInterface.TAG_MODEL] ?: "") }

    var pendingGrantingPermission by remember { mutableStateOf(false) }
    val activity = context as MainActivity
    if (!activity.grantedWritePermissions && !pendingGrantingPermission) {
        pendingGrantingPermission = true
        homeViewModel.imageUri?.let {
            activity.grantPermissions(context, it)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(modifier = Modifier.padding(contentPadding), value = date, onValueChange = { date = it }, label = { Text("Creation Date") })
        OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitude") })
        OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitude") })
        OutlinedTextField(value = device, onValueChange = { device = it }, label = { Text("Device") })
        OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Device Model") })

        Button(onClick = {
            val newExifData = mapOf(
                ExifInterface.TAG_DATETIME to date,
                ExifInterface.TAG_GPS_LATITUDE to latitude,
                ExifInterface.TAG_GPS_LONGITUDE to longitude,
                ExifInterface.TAG_MAKE to device,
                ExifInterface.TAG_MODEL to model
            )
            homeViewModel.imageUri?.let {
                if (activity.grantedWritePermissions) {
                    editViewModel.saveExifData(context, it, newExifData)
                }
            }
            onNavigateUp()
        }) {
            Text("Save tags")
        }
    }
}