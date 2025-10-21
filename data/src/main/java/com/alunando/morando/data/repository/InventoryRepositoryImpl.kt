package com.alunando.morando.data.repository

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementação do repositório de inventário
 * TODO: Implementar com Firebase Firestore e Storage
 */
class InventoryRepositoryImpl : InventoryRepository {

    override fun getProducts(): Flow<List<Product>> {
        return flowOf(emptyList())
    }

    override suspend fun getProductById(productId: String): Result<Product> {
        return Result.Error(Exception("Not implemented yet"))
    }

    override fun getProductsByCategory(category: String): Flow<List<Product>> {
        return flowOf(emptyList())
    }

    override suspend fun getProductByBarcode(barcode: String): Result<Product?> {
        return Result.Success(null)
    }

    override suspend fun addProduct(product: Product): Result<Product> {
        return Result.Error(Exception("Not implemented yet"))
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return Result.Error(Exception("Not implemented yet"))
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return Result.Error(Exception("Not implemented yet"))
    }

    override suspend fun uploadProductImage(productId: String, imageData: ByteArray): Result<String> {
        return Result.Error(Exception("Not implemented yet"))
    }

    override fun getProductsNeedingReplenishment(): Flow<List<Product>> {
        return flowOf(emptyList())
    }
}

