package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.repository.InventoryRepository

/**
 * Use case para fazer upload de imagem do produto
 */
class UploadProductImageUseCase(
    private val repository: InventoryRepository,
) {
    suspend operator fun invoke(
        productId: String,
        imageData: ByteArray,
    ): Result<String> = repository.uploadProductImage(productId, imageData)
}
