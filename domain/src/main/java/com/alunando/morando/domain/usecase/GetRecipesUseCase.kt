package com.alunando.morando.domain.usecase

import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.repository.CookingRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case para obter todas as receitas
 */
class GetRecipesUseCase(
    private val repository: CookingRepository,
) {
    operator fun invoke(): Flow<List<Recipe>> = repository.getRecipes()
}
