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
 * Rotas de navegação do app
 */
sealed class Route(val route: String) {
    data object Home : Route("home")
    data object Tasks : Route("tasks")
    data object Inventory : Route("inventory")
    data object Shopping : Route("shopping")
}

/**
 * Configuração de navegação do app
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Route.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Tela inicial
        composable(Route.Home.route) {
            HomeScreen(
                onNavigateToTasks = {
                    navController.navigate(Route.Tasks.route)
                },
                onNavigateToInventory = {
                    navController.navigate(Route.Inventory.route)
                },
                onNavigateToShopping = {
                    navController.navigate(Route.Shopping.route)
                }
            )
        }

        // Tarefas
        composable(Route.Tasks.route) {
            TasksScreen(
                viewModel = koinViewModel()
            )
        }

        // Estoque
        composable(Route.Inventory.route) {
            InventoryScreen()
        }

        // Lista de compras
        composable(Route.Shopping.route) {
            ShoppingScreen()
        }
    }
}

