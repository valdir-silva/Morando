package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.repository.CookingRepository

/**
 * Use case para remover uma receita
 */
class DeleteRecipeUseCase(
    private val repository: CookingRepository,
) {
    suspend operator fun invoke(id: String): Result<Unit> = repository.deleteRecipe(id)
}
