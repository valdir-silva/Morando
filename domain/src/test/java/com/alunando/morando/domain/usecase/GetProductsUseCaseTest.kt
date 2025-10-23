package com.alunando.morando.domain.usecase

import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.repository.InventoryRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Date

class GetProductsUseCaseTest {
    private lateinit var useCase: GetProductsUseCase
    private lateinit var repository: InventoryRepository

    private val mockProducts =
        listOf(
            Product(
                id = "1",
                nome = "Produto 1",
                categoria = "Categoria 1",
                valor = 10.0,
                createdAt = Date(),
            ),
            Product(
                id = "2",
                nome = "Produto 2",
                categoria = "Categoria 2",
                valor = 20.0,
                createdAt = Date(),
            ),
        )

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetProductsUseCase(repository)
    }

    @Test
    fun `invoke should return products from repository`() =
        runTest {
            // Given
            every { repository.getProducts() } returns flowOf(mockProducts)

            // When
            val result = useCase().first()

            // Then
            assertEquals(mockProducts, result)
            verify { repository.getProducts() }
        }

    @Test
    fun `invoke should return empty list when repository returns empty`() =
        runTest {
            // Given
            every { repository.getProducts() } returns flowOf(emptyList())

            // When
            val result = useCase().first()

            // Then
            assertEquals(emptyList<Product>(), result)
            verify { repository.getProducts() }
        }
}
