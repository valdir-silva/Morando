package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.repository.CookingRepository

/**
 * Use case para obter uma receita específica
 */
class GetRecipeByIdUseCase(
    private val repository: CookingRepository,
) {
    suspend operator fun invoke(id: String): Result<Recipe> = repository.getRecipeById(id)
}
