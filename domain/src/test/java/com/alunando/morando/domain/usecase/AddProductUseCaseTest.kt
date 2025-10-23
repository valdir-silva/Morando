package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.repository.InventoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class AddProductUseCaseTest {
    private lateinit var useCase: AddProductUseCase
    private lateinit var repository: InventoryRepository

    private val mockProduct =
        Product(
            id = "1",
            nome = "Produto 1",
            categoria = "Categoria 1",
            valor = 10.0,
            createdAt = Date(),
        )

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddProductUseCase(repository)
    }

    @Test
    fun `invoke should return success when repository succeeds`() =
        runTest {
            // Given
            coEvery { repository.addProduct(any()) } returns Result.Success(mockProduct)

            // When
            val result = useCase(mockProduct)

            // Then
            assertTrue(result is Result.Success)
            assertEquals(mockProduct, (result as Result.Success).data)
            coVerify { repository.addProduct(mockProduct) }
        }

    @Test
    fun `invoke should return error when repository fails`() =
        runTest {
            // Given
            val exception = Exception("Erro ao adicionar")
            coEvery { repository.addProduct(any()) } returns Result.Error(exception)

            // When
            val result = useCase(mockProduct)

            // Then
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
            coVerify { repository.addProduct(mockProduct) }
        }
}
