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

    data class FilterByCategory(
        val category: RecipeCategory?,
    ) : CookingIntent()

    // Detalhes e seleção
    data class SelectRecipe(
        val id: String,
    ) : CookingIntent()

    data object ClearSelection : CookingIntent()

    // Sessão de cozinha
    data class StartCooking(
        val recipeId: String,
    ) : CookingIntent()

    data object NextStep : CookingIntent()

    data object PreviousStep : CookingIntent()

    data class ToggleMiseEnPlaceStep(
        val stepIndex: Int,
    ) : CookingIntent()

    data object StartMiseEnPlacePhase : CookingIntent()

    data object StartCookingPhase : CookingIntent()

    // Cronômetro
    data class StartTimer(
        val seconds: Int,
    ) : CookingIntent()

    data object PauseTimer : CookingIntent()

    data object ResumeTimer : CookingIntent()

    data object StopTimer : CookingIntent()

    data object TickTimer : CookingIntent()

    // Controle de sessão
    data object StopCooking : CookingIntent()

    // CRUD de receitas
    data class CreateRecipe(
        val recipe: Recipe,
        val imageData: ByteArray? = null,
    ) : CookingIntent() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as CreateRecipe

            if (recipe != other.recipe) return false
            if (imageData != null) {
                if (other.imageData == null) return false
                if (!imageData.contentEquals(other.imageData)) return false
            } else if (other.imageData != null) {
                return false
            }

            return true
        }

        override fun hashCode(): Int {
            var result = recipe.hashCode()
            result = 31 * result + (imageData?.contentHashCode() ?: 0)
            return result
        }
    }

    data class UpdateRecipe(
        val recipe: Recipe,
        val imageData: ByteArray? = null,
    ) : CookingIntent() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UpdateRecipe

            if (recipe != other.recipe) return false
            if (imageData != null) {
                if (other.imageData == null) return false
                if (!imageData.contentEquals(other.imageData)) return false
            } else if (other.imageData != null) {
                return false
            }

            return true
        }

        override fun hashCode(): Int {
            var result = recipe.hashCode()
            result = 31 * result + (imageData?.contentHashCode() ?: 0)
            return result
        }
    }

    data class DeleteRecipe(
        val id: String,
    ) : CookingIntent()

    // Configurações
    data class SelectStoveType(
        val type: StoveType,
    ) : CookingIntent()

    data object LoadStovePreference : CookingIntent()

    // Verificação de ingredientes
    data class CheckIngredients(
        val recipeId: String,
    ) : CookingIntent()
}
