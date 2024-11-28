package com.example.healthconnect.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.healthconnect.HealthConnect.HealthConnectProvider
import com.example.healthconnect.ui.create.CreateDestination
import com.example.healthconnect.ui.create.CreateScreen
import com.example.healthconnect.ui.create.CreateViewModel
import com.example.healthconnect.ui.edit.EditDestination
import com.example.healthconnect.ui.edit.EditScreen
import com.example.healthconnect.ui.edit.EditViewModel
import com.example.healthconnect.ui.home.HomeDestination
import com.example.healthconnect.ui.home.HomeScreen
import com.example.healthconnect.ui.home.HomeViewModel

@Composable
fun AppNavHost(
    healthConnectProvider: HealthConnectProvider,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier,
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToEdit = { navController.navigate(EditDestination.route) },
                navigateToCreate = { navController.navigate(CreateDestination.route) },
                viewModel = HomeViewModel(),
                healthConnectProvider = healthConnectProvider,
            )
        }
        composable(route = EditDestination.route) {
            EditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                editViewModel = EditViewModel(),
            )
        }
        composable(route = CreateDestination.route) {
            CreateScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                createViewModel = CreateViewModel(),
            )
        }
    }
}