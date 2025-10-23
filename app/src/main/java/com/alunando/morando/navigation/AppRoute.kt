package com.alunando.morando.navigation

/**
 * Rotas de navegação do app
 */
sealed class AppRoute(
    val route: String,
) {
    data object Login : AppRoute("login")

    data object Home : AppRoute("home")

    data object Tasks : AppRoute("tasks")

    data object Inventory : AppRoute("inventory")

    data object AddProduct : AppRoute("inventory/add")

    data object BarcodeScanner : AppRoute("barcode_scanner")

    data object Shopping : AppRoute("shopping")

    data object Cooking : AppRoute("cooking")

    data object RecipeDetail : AppRoute("cooking/recipe/{recipeId}") {
        fun createRoute(recipeId: String) = "cooking/recipe/$recipeId"
    }

    data object CookingSession : AppRoute("cooking/session/{recipeId}") {
        fun createRoute(recipeId: String) = "cooking/session/$recipeId"
    }

    data object RecipeForm : AppRoute("cooking/form?recipeId={recipeId}") {
        fun createRoute(recipeId: String? = null) =
            if (recipeId != null) {
                "cooking/form?recipeId=$recipeId"
            } else {
                "cooking/form"
            }
    }

    data object StoveSettings : AppRoute("cooking/stove-settings")
}
