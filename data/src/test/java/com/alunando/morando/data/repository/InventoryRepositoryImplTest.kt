package com.alunando.morando.data.repository

import com.alunando.morando.core.Result
import com.alunando.morando.data.datasource.InventoryRemoteDataSource
import com.alunando.morando.domain.model.Product
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Date
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InventoryRepositoryImplTest {
    private lateinit var repository: InventoryRepositoryImpl
    private lateinit var remoteDataSource: InventoryRemoteDataSource

    private val mockProducts =
        listOf(
            Product(
                id = "1",
                nome = "Produto 1",
                categoria = "Categoria 1",
                valor = 10.0,
                createdAt = Date()
            ),
            Product(
                id = "2",
                nome = "Produto 2",
                categoria = "Categoria 2",
                valor = 20.0,
                createdAt = Date()
            )
        )

    @Before
    fun setup() {
        remoteDataSource = mockk()
        repository = InventoryRepositoryImpl(remoteDataSource)
    }

    @Test
    fun `getProducts should return flow of products from remote data source`() =
        runTest {
            // Given
            every { remoteDataSource.getProducts() } returns flowOf(mockProducts)

            // When
            val result = repository.getProducts().first()

            // Then
            assertEquals(mockProducts, result)
            verify { remoteDataSource.getProducts() }
        }

    @Test
    fun `getProductById should return success when product exists`() =
        runTest {
            // Given
            val productId = "1"
            val product = mockProducts[0]
            coEvery { remoteDataSource.getProductById(any()) } returns product

            // When
            val result = repository.getProductById(productId)

            // Then
            assertTrue(result is Result.Success)
            assertEquals(product, (result as Result.Success).data)
            coVerify { remoteDataSource.getProductById(productId) }
        }

    @Test
    fun `getProductById should return error when product not found`() =
        runTest {
            // Given
            val productId = "999"
            coEvery { remoteDataSource.getProductById(any()) } returns null

            // When
            val result = repository.getProductById(productId)

            // Then
            assertTrue(result is Result.Error)
            assertTrue((result as Result.Error).exception.message == "Produto não encontrado")
            coVerify { remoteDataSource.getProductById(productId) }
        }

    @Test
    fun `addProduct should return success when product is added`() =
        runTest {
            // Given
            val product = mockProducts[0]
            coEvery { remoteDataSource.addProduct(any()) } returns product

            // When
            val result = repository.addProduct(product)

            // Then
            assertTrue(result is Result.Success)
            assertEquals(product, (result as Result.Success).data)
            coVerify { remoteDataSource.addProduct(product) }
        }

    @Test
    fun `addProduct should return error when exception occurs`() =
        runTest {
            // Given
            val product = mockProducts[0]
            val exception = Exception("Erro ao adicionar")
            coEvery { remoteDataSource.addProduct(any()) } throws exception

            // When
            val result = repository.addProduct(product)

            // Then
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
            coVerify { remoteDataSource.addProduct(product) }
        }

    @Test
    fun `deleteProduct should return success when product is deleted`() =
        runTest {
            // Given
            val productId = "1"
            coEvery { remoteDataSource.deleteProduct(any()) } returns Unit

            // When
            val result = repository.deleteProduct(productId)

            // Then
            assertTrue(result is Result.Success)
            coVerify { remoteDataSource.deleteProduct(productId) }
        }

    @Test
    fun `deleteProduct should return error when exception occurs`() =
        runTest {
            // Given
            val productId = "1"
            val exception = Exception("Erro ao deletar")
            coEvery { remoteDataSource.deleteProduct(any()) } throws exception

            // When
            val result = repository.deleteProduct(productId)

            // Then
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
            coVerify { remoteDataSource.deleteProduct(productId) }
        }

    @Test
    fun `uploadProductImage should return success with image URL`() =
        runTest {
            // Given
            val productId = "1"
            val imageData = byteArrayOf(1, 2, 3)
            val imageUrl = "https://example.com/image.jpg"
            coEvery { remoteDataSource.uploadProductImage(any(), any()) } returns imageUrl

            // When
            val result = repository.uploadProductImage(productId, imageData)

            // Then
            assertTrue(result is Result.Success)
            assertEquals(imageUrl, (result as Result.Success).data)
            coVerify { remoteDataSource.uploadProductImage(productId, imageData) }
        }

    @Test
    fun `uploadProductImage should return error when exception occurs`() =
        runTest {
            // Given
            val productId = "1"
            val imageData = byteArrayOf(1, 2, 3)
            val exception = Exception("Erro ao fazer upload")
            coEvery { remoteDataSource.uploadProductImage(any(), any()) } throws exception

            // When
            val result = repository.uploadProductImage(productId, imageData)

            // Then
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
            coVerify { remoteDataSource.uploadProductImage(productId, imageData) }
        }
}

