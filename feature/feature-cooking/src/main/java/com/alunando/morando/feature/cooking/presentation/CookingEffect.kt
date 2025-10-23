package com.alunando.morando.feature.cooking.presentation

/**
 * Efeitos colaterais (side effects) da feature Cooking
 */
sealed class CookingEffect {
    data class ShowToast(val message: String) : CookingEffect()

    data class ShowError(val message: String) : CookingEffect()

    data object NavigateBack : CookingEffect()

    data class NavigateToRecipeDetail(val recipeId: String) : CookingEffect()

    data class NavigateToCookingSession(val recipeId: String) : CookingEffect()

    data class NavigateToRecipeForm(val recipeId: String? = null) : CookingEffect()

    data object NavigateToStoveSettings : CookingEffect()

    data object TimerFinished : CookingEffect()

    data object CookingCompleted : CookingEffect()
}
