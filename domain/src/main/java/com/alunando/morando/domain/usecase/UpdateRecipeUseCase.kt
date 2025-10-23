package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.repository.CookingRepository

/**
 * Use case para atualizar uma receita existente
 */
class UpdateRecipeUseCase(
    private val repository: CookingRepository,
) {
    suspend operator fun invoke(recipe: Recipe): Result<Unit> = repository.updateRecipe(recipe)
}
