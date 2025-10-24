package com.alunando.morando.domain.usecase.cooking

import com.alunando.morando.core.Result
import com.alunando.morando.domain.repository.CookingRepository

/**
 * Use case para fazer upload da imagem de uma receita
 */
class UploadRecipeImageUseCase(
    private val repository: CookingRepository,
) {
    suspend operator fun invoke(
        recipeId: String,
        imageData: ByteArray,
    ): Result<String> = repository.uploadRecipeImage(recipeId, imageData)
}
