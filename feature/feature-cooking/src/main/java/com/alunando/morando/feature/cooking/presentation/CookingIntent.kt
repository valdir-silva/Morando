package com.alunando.morando.feature.cooking.presentation

import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.model.RecipeCategory
import com.alunando.morando.domain.model.StoveType

/**
 * Intents do usuário para a feature Cooking
 */
sealed class CookingIntent {
    // Lista de receitas
    data object LoadRecipes : CookingIntent()

    data class FilterByCategory(val category: RecipeCategory?) : CookingIntent()

    // Detalhes e seleção
    data class SelectRecipe(val id: String) : CookingIntent()

    data object ClearSelection : CookingIntent()

    // Sessão de cozinha
    data class StartCooking(val recipeId: String) : CookingIntent()

    data object NextStep : CookingIntent()

    data object PreviousStep : CookingIntent()

    data class ToggleMiseEnPlaceStep(val stepIndex: Int) : CookingIntent()

    data object StartMiseEnPlacePhase : CookingIntent()

    data object StartCookingPhase : CookingIntent()

    // Cronômetro
    data class StartTimer(val seconds: Int) : CookingIntent()

    data object PauseTimer : CookingIntent()

    data object ResumeTimer : CookingIntent()

    data object StopTimer : CookingIntent()

    data object TickTimer : CookingIntent()

    // Controle de sessão
    data object StopCooking : CookingIntent()

    // CRUD de receitas
    data class CreateRecipe(val recipe: Recipe) : CookingIntent()

    data class UpdateRecipe(val recipe: Recipe) : CookingIntent()

    data class DeleteRecipe(val id: String) : CookingIntent()

    // Configurações
    data class SelectStoveType(val type: StoveType) : CookingIntent()

    data object LoadStovePreference : CookingIntent()

    // Verificação de ingredientes
    data class CheckIngredients(val recipeId: String) : CookingIntent()
}
