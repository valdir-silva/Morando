package com.alunando.morando.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alunando.morando.feature.inventory.ui.InventoryScreen
import com.alunando.morando.feature.shopping.ui.ShoppingScreen
import com.alunando.morando.feature.tasks.ui.TasksScreen
import com.alunando.morando.ui.home.HomeScreen
import org.koin.androidx.compose.koinViewModel

/**
 * Configuração de navegação do app
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = AppRoute.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Tela inicial
        composable(AppRoute.Home.route) {
            HomeScreen(
                onNavigateToTasks = {
                    navController.navigate(AppRoute.Tasks.route)
                },
                onNavigateToInventory = {
                    navController.navigate(AppRoute.Inventory.route)
                },
                onNavigateToShopping = {
                    navController.navigate(AppRoute.Shopping.route)
                }
            )
        }

        // Tarefas
        composable(AppRoute.Tasks.route) {
            TasksScreen(
                viewModel = koinViewModel()
            )
        }

        // Estoque
        composable(AppRoute.Inventory.route) {
            InventoryScreen()
        }

        // Lista de compras
        composable(AppRoute.Shopping.route) {
            ShoppingScreen()
        }
    }
}
