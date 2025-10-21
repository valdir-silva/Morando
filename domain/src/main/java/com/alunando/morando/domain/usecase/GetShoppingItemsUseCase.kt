package com.alunando.morando.domain.usecase

import com.alunando.morando.domain.model.ShoppingItem
import com.alunando.morando.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case para buscar itens da lista de compras
 */
class GetShoppingItemsUseCase(
    private val repository: ShoppingRepository
) {
    operator fun invoke(): Flow<List<ShoppingItem>> {
        return repository.getShoppingItems()
    }
}

