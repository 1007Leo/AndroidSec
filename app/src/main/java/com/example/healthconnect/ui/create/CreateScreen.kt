package com.example.healthconnect.ui.create

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.healthconnect.R
import com.example.healthconnect.TopAppBar
import com.example.healthconnect.ui.navigation.NavigationDestination

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
            contentPadding = innerPadding,
        )
    }
}

@Composable
fun CreateBody(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    createViewModel: CreateViewModel = CreateViewModel(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {

}