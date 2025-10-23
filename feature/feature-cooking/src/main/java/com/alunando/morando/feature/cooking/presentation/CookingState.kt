package com.alunando.morando.feature.cooking.presentation

import com.alunando.morando.domain.model.CookingSession
import com.alunando.morando.domain.model.IngredientAvailability
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.model.RecipeCategory
import com.alunando.morando.domain.model.StoveType

/**
 * Estado da feature Cooking
 */
data class CookingState(
    val recipes: List<Recipe> = emptyList(),
    val filteredRecipes: List<Recipe> = emptyList(),
    val selectedCategory: RecipeCategory? = null,
    val selectedRecipe: Recipe? = null,
    val cookingSession: CookingSession? = null,
    val ingredientsAvailability: Map<String, IngredientAvailability> = emptyMap(),
    val currentStoveType: StoveType = StoveType.INDUCTION,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    /**
     * Verifica se está em uma sessão ativa de cozinha
     */
    fun isInCookingSession(): Boolean = cookingSession != null

    /**
     * Verifica se o cronômetro está rodando
     */
    fun isTimerRunning(): Boolean = cookingSession?.timerRunning == true
}
