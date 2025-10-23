package com.alunando.morando.data.repository

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.ShoppingItem
import com.alunando.morando.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementação do repositório de lista de compras
 * Implementar com Firebase Firestore
 */
class ShoppingRepositoryImpl : ShoppingRepository {
    override fun getShoppingItems(): Flow<List<ShoppingItem>> = flowOf(emptyList())

    override suspend fun getShoppingItemById(itemId: String): Result<ShoppingItem> =
        Result.Error(Exception("Not implemented yet"))

    override suspend fun addShoppingItem(item: ShoppingItem): Result<ShoppingItem> =
        Result.Error(Exception("Not implemented yet"))

    override suspend fun updateShoppingItem(item: ShoppingItem): Result<Unit> =
        Result.Error(Exception("Not implemented yet"))

    override suspend fun deleteShoppingItem(itemId: String): Result<Unit> =
        Result.Error(Exception("Not implemented yet"))

    override suspend fun markItemPurchased(
        itemId: String,
        purchased: Boolean,
    ): Result<Unit> = Result.Error(Exception("Not implemented yet"))

    override suspend fun clearPurchasedItems(): Result<Unit> = Result.Error(Exception("Not implemented yet"))
}
