package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.repository.CookingRepository

/**
 * Use case para adicionar uma nova receita
 */
class AddRecipeUseCase(
    private val repository: CookingRepository,
) {
    suspend operator fun invoke(recipe: Recipe): Result<Unit> = repository.addRecipe(recipe)
}
