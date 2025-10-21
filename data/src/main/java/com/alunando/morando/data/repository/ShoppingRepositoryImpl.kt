package com.alunando.morando.data.repository

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.ShoppingItem
import com.alunando.morando.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementação do repositório de lista de compras
 * TODO: Implementar com Firebase Firestore
 */
class ShoppingRepositoryImpl : ShoppingRepository {

    override fun getShoppingItems(): Flow<List<ShoppingItem>> {
        return flowOf(emptyList())
    }

    override suspend fun getShoppingItemById(itemId: String): Result<ShoppingItem> {
        return Result.Error(Exception("Not implemented yet"))
    }

    override suspend fun addShoppingItem(item: ShoppingItem): Result<ShoppingItem> {
        return Result.Error(Exception("Not implemented yet"))
    }

    override suspend fun updateShoppingItem(item: ShoppingItem): Result<Unit> {
        return Result.Error(Exception("Not implemented yet"))
    }

    override suspend fun deleteShoppingItem(itemId: String): Result<Unit> {
        return Result.Error(Exception("Not implemented yet"))
    }

    override suspend fun markItemPurchased(itemId: String, purchased: Boolean): Result<Unit> {
        return Result.Error(Exception("Not implemented yet"))
    }

    override suspend fun clearPurchasedItems(): Result<Unit> {
        return Result.Error(Exception("Not implemented yet"))
    }
}

