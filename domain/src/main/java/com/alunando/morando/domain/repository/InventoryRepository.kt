package com.alunando.morando.domain.repository

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de inventário/estoque
 */
interface InventoryRepository {

    /**
     * Busca todos os produtos do usuário
     */
    fun getProducts(): Flow<List<Product>>

    /**
     * Busca produto por ID
     */
    suspend fun getProductById(productId: String): Result<Product>

    /**
     * Busca produtos por categoria
     */
    fun getProductsByCategory(category: String): Flow<List<Product>>

    /**
     * Busca produto por código de barras
     */
    suspend fun getProductByBarcode(barcode: String): Result<Product?>

    /**
     * Adiciona novo produto
     */
    suspend fun addProduct(product: Product): Result<Product>

    /**
     * Atualiza produto existente
     */
    suspend fun updateProduct(product: Product): Result<Unit>

    /**
     * Deleta produto
     */
    suspend fun deleteProduct(productId: String): Result<Unit>

    /**
     * Upload de foto do produto
     */
    suspend fun uploadProductImage(productId: String, imageData: ByteArray): Result<String>

    /**
     * Busca produtos que estão acabando ou vencidos
     */
    fun getProductsNeedingReplenishment(): Flow<List<Product>>
}
