package com.example.lab5.ui.home

import android.content.ContentResolver
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.lab5.MainActivity
import com.example.lab5.TopAppBar
import com.example.lab5.ui.navigation.NavigationDestination
import com.example.lab5.R

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToEdit: () -> Unit,
    viewModel: HomeViewModel,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var imageUri by remember { mutableStateOf(viewModel.imageUri) }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold (
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = stringResource(HomeDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val activity = context as MainActivity
                    activity.grantedWritePermissions = false
                    showDialog = true
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(15.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.back_button)
                )
            }
        },
    ) { innerPadding ->
        HomeBody(
            navigateToEdit = navigateToEdit,
            viewModel = viewModel,
            contentPadding = innerPadding,
            imageUri = imageUri
        )

        if (showDialog) {
            ImagePickerDialog(
                onImageSelected = { uri ->
                    imageUri = uri
                },
                onDismiss = { showDialog = false },
            )
        }
    }
}

@Composable
fun HomeBody(
    navigateToEdit: () -> Unit,
    viewModel: HomeViewModel,
    imageUri: Uri?,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    var exifData by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageUri?.let { uri ->

            exifData = viewModel.getExifData(context, uri)
            viewModel.imageUri = uri
            viewModel.exifData = exifData

            AsyncImage(
                model = uri,
                contentDescription = null,
                modifier = Modifier.padding(contentPadding),
            )

            exifData.forEach { (key, value) ->
                when (key) {
                    ExifInterface.TAG_DATETIME -> Text("Creation Date: $value")
                    ExifInterface.TAG_MAKE -> Text("Device: $value")
                    ExifInterface.TAG_MODEL -> Text("Device Model: $value")
                    ExifInterface.TAG_GPS_LATITUDE -> Text("Latitude: $value")
                    ExifInterface.TAG_GPS_LONGITUDE -> Text("Longitude: $value")
                }
            }

            Button(
                onClick = navigateToEdit,
            ) {
                Text("Edit tags")
            }
        }
    }
}

@Composable
fun loadImages(contentResolver: ContentResolver): List<Uri> {
    val imageUris = remember { mutableStateListOf<Uri>() }

    val projection = arrayOf(
        MediaStore.Images.Media._ID,
    )
    val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

    val cursor: Cursor? = contentResolver.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrder,
    )

    cursor?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        while (it.moveToNext()) {
            val uri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                it.getString(idColumn)
            )
            imageUris.add(MediaStore.setRequireOriginal(uri))
        }
    }

    return imageUris
}

@Composable
fun ImagePickerDialog(onImageSelected: (Uri) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val imageUris = loadImages(contentResolver)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select an Image") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(imageUris.size) { index ->
                    val uri = imageUris[index]
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(1.dp)
                            .size(100.dp)
                            .clip(RectangleShape)
                            .clickable {
                                onImageSelected(uri)
                                onDismiss()
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}