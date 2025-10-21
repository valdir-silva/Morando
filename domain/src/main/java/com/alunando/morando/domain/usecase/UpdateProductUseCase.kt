package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.repository.InventoryRepository

/**
 * Use case para atualizar produto existente
 */
class UpdateProductUseCase(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(product: Product): Result<Unit> {
        return repository.updateProduct(product)
    }
}

