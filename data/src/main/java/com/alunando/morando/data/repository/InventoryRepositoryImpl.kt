package com.alunando.morando.data.repository

import com.alunando.morando.core.Result
import com.alunando.morando.data.datasource.InventoryRemoteDataSource
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementação do repositório de inventário com Firebase
 */
class InventoryRepositoryImpl(
    private val remoteDataSource: InventoryRemoteDataSource,
) : InventoryRepository {
    override fun getProducts(): Flow<List<Product>> = remoteDataSource.getProducts()

    override suspend fun getProductById(productId: String): Result<Product> =
        try {
            val product = remoteDataSource.getProductById(productId)
            if (product != null) {
                Result.Success(product)
            } else {
                Result.Error(Exception("Produto não encontrado"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }

    override fun getProductsByCategory(category: String): Flow<List<Product>> =
        remoteDataSource.getProductsByCategory(category)

    override suspend fun getProductByBarcode(barcode: String): Result<Product?> =
        try {
            val product = remoteDataSource.getProductByBarcode(barcode)
            Result.Success(product)
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun addProduct(product: Product): Result<Product> =
        try {
            val addedProduct = remoteDataSource.addProduct(product)
            Result.Success(addedProduct)
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun updateProduct(product: Product): Result<Unit> =
        try {
            remoteDataSource.updateProduct(product)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun deleteProduct(productId: String): Result<Unit> =
        try {
            remoteDataSource.deleteProduct(productId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun uploadProductImage(
        productId: String,
        imageData: ByteArray,
    ): Result<String> =
        try {
            val imageUrl = remoteDataSource.uploadProductImage(productId, imageData)
            Result.Success(imageUrl)
        } catch (e: Exception) {
            Result.Error(e)
        }

    override fun getProductsNeedingReplenishment(): Flow<List<Product>> =
        remoteDataSource.getProductsNeedingReplenishment()

    override suspend fun getProductInfoFromBarcode(barcode: String): Result<Product?> =
        try {
            val product = remoteDataSource.searchProductByBarcode(barcode)
            Result.Success(product)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
