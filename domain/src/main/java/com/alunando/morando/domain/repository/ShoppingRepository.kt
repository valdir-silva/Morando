package com.alunando.morando.domain.repository

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de lista de compras
 */
interface ShoppingRepository {
    
    /**
     * Busca todos os itens da lista de compras
     */
    fun getShoppingItems(): Flow<List<ShoppingItem>>

    /**
     * Busca item por ID
     */
    suspend fun getShoppingItemById(itemId: String): Result<ShoppingItem>

    /**
     * Adiciona item à lista
     */
    suspend fun addShoppingItem(item: ShoppingItem): Result<ShoppingItem>

    /**
     * Atualiza item da lista
     */
    suspend fun updateShoppingItem(item: ShoppingItem): Result<Unit>

    /**
     * Deleta item da lista
     */
    suspend fun deleteShoppingItem(itemId: String): Result<Unit>

    /**
     * Marca item como comprado/não comprado
     */
    suspend fun markItemPurchased(itemId: String, purchased: Boolean): Result<Unit>

    /**
     * Remove todos os itens comprados
     */
    suspend fun clearPurchasedItems(): Result<Unit>
}

