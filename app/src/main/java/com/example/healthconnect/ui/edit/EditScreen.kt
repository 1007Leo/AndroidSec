package com.example.healthconnect.ui.edit

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.healthconnect.ui.navigation.NavigationDestination
import com.example.healthconnect.R
import com.example.healthconnect.TopAppBar

object EditDestination : NavigationDestination {
    override val route = "edit"
    override val titleRes = R.string.edit_screen_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    editViewModel: EditViewModel,
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
            contentPadding = innerPadding,
        )
    }
}

@Composable
fun EditBody(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    editViewModel: EditViewModel = EditViewModel(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

}