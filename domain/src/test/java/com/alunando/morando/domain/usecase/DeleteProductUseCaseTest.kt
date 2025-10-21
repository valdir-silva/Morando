package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.repository.InventoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteProductUseCaseTest {
    private lateinit var useCase: DeleteProductUseCase
    private lateinit var repository: InventoryRepository

    @Before
    fun setup() {
        repository = mockk()
        useCase = DeleteProductUseCase(repository)
    }

    @Test
    fun `invoke should return success when repository succeeds`() =
        runTest {
            // Given
            val productId = "1"
            coEvery { repository.deleteProduct(any()) } returns Result.Success(Unit)

            // When
            val result = useCase(productId)

            // Then
            assertTrue(result is Result.Success)
            coVerify { repository.deleteProduct(productId) }
        }

    @Test
    fun `invoke should return error when repository fails`() =
        runTest {
            // Given
            val productId = "1"
            val exception = Exception("Erro ao deletar")
            coEvery { repository.deleteProduct(any()) } returns Result.Error(exception)

            // When
            val result = useCase(productId)

            // Then
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
            coVerify { repository.deleteProduct(productId) }
        }
}

