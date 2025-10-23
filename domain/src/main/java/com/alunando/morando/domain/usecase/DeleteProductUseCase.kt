package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.repository.InventoryRepository

/**
 * Use case para deletar produto
 */
class DeleteProductUseCase(
    private val repository: InventoryRepository,
) {
    suspend operator fun invoke(productId: String): Result<Unit> = repository.deleteProduct(productId)
}
