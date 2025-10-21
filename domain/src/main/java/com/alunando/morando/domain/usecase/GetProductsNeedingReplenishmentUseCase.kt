package com.alunando.morando.domain.usecase

import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case para buscar produtos que estão acabando ou próximos do vencimento
 */
class GetProductsNeedingReplenishmentUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return repository.getProductsNeedingReplenishment()
    }
}

