package com.example.lab5.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.lab5.ui.edit.EditDestination
import com.example.lab5.ui.edit.EditScreen
import com.example.lab5.ui.home.HomeDestination
import com.example.lab5.ui.home.HomeScreen
import com.example.lab5.ui.home.HomeViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val homeViewModel = HomeViewModel()
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier,
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToEdit = { navController.navigate(EditDestination.route) },
                viewModel = homeViewModel,
            )
        }
        composable(route = EditDestination.route) {
            EditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                homeViewModel = homeViewModel,
            )
        }
    }
}