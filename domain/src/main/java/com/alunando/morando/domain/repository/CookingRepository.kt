package com.alunando.morando.domain.repository

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.model.RecipeCategory
import com.alunando.morando.domain.model.StoveType
import kotlinx.coroutines.flow.Flow

/**
 * Repository para gerenciar receitas
 */
interface CookingRepository {
    /**
     * Obtém todas as receitas do usuário
     */
    fun getRecipes(): Flow<List<Recipe>>

    /**
     * Obtém receitas filtradas por categoria
     */
    fun getRecipesByCategory(category: RecipeCategory): Flow<List<Recipe>>

    /**
     * Obtém uma receita específica por ID
     */
    suspend fun getRecipeById(id: String): Result<Recipe>

    /**
     * Adiciona uma nova receita
     */
    suspend fun addRecipe(recipe: Recipe): Result<Unit>

    /**
     * Atualiza uma receita existente
     */
    suspend fun updateRecipe(recipe: Recipe): Result<Unit>

    /**
     * Remove uma receita
     */
    suspend fun deleteRecipe(id: String): Result<Unit>

    /**
     * Obtém o tipo de fogão preferido do usuário
     */
    suspend fun getUserStovePreference(): StoveType

    /**
     * Salva o tipo de fogão preferido do usuário
     */
    suspend fun saveUserStovePreference(stoveType: StoveType): Result<Unit>
}
