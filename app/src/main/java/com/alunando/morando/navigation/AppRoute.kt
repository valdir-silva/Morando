package com.alunando.morando.navigation

/**
 * Rotas de navegação do app
 */
sealed class AppRoute(
    val route: String
) {
    data object Home : AppRoute("home")

    data object Tasks : AppRoute("tasks")

    data object Inventory : AppRoute("inventory")

    data object BarcodeScanner : AppRoute("barcode_scanner")

    data object Shopping : AppRoute("shopping")
}
