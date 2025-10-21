package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.repository.InventoryRepository

/**
 * Use case para buscar produto por ID
 */
class GetProductByIdUseCase(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(productId: String): Result<Product> {
        return repository.getProductById(productId)
    }
}

