package com.alunando.morando.feature.inventory.presentation

import app.cash.turbine.test
import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.usecase.AddProductUseCase
import com.alunando.morando.domain.usecase.DeleteProductUseCase
import com.alunando.morando.domain.usecase.GetProductsUseCase
import com.alunando.morando.domain.usecase.UpdateProductUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModelTest {
    private lateinit var viewModel: InventoryViewModel
    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var addProductUseCase: AddProductUseCase
    private lateinit var updateProductUseCase: UpdateProductUseCase
    private lateinit var deleteProductUseCase: DeleteProductUseCase

    private val testDispatcher = StandardTestDispatcher()

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
        Dispatchers.setMain(testDispatcher)
        getProductsUseCase = mockk()
        addProductUseCase = mockk()
        updateProductUseCase = mockk()
        deleteProductUseCase = mockk()

        // Setup default behavior
        every { getProductsUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProducts should update state with products`() =
        runTest {
            // Given
            every { getProductsUseCase() } returns flowOf(mockProducts)
            viewModel =
                InventoryViewModel(
                    getProductsUseCase,
                    addProductUseCase,
                    updateProductUseCase,
                    deleteProductUseCase,
                )

            // When
            advanceUntilIdle()

            // Then
            val state = viewModel.state.value
            assertEquals(mockProducts, state.products)
            assertFalse(state.isLoading)
        }

    @Test
    fun `addProduct should call use case and emit success effect`() =
        runTest {
            // Given
            val newProduct =
                Product(
                    id = "3",
                    nome = "Novo Produto",
                    categoria = "Nova Categoria",
                    valor = 30.0,
                    createdAt = Date(),
                )
            coEvery { addProductUseCase(any()) } returns Result.Success(newProduct)
            viewModel =
                InventoryViewModel(
                    getProductsUseCase,
                    addProductUseCase,
                    updateProductUseCase,
                    deleteProductUseCase,
                )
            advanceUntilIdle()

            // When
            viewModel.effect.test {
                viewModel.handleIntent(InventoryIntent.AddProduct(newProduct))
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(effect is InventoryEffect.ShowToast)
                assertEquals("Produto adicionado com sucesso", (effect as InventoryEffect.ShowToast).message)
                coVerify { addProductUseCase(newProduct) }
            }
        }

    @Test
    fun `addProduct should emit error effect on failure`() =
        runTest {
            // Given
            val newProduct =
                Product(
                    id = "3",
                    nome = "Novo Produto",
                    categoria = "Nova Categoria",
                    valor = 30.0,
                    createdAt = Date(),
                )
            coEvery { addProductUseCase(any()) } returns Result.Error(Exception("Erro ao adicionar"))
            viewModel =
                InventoryViewModel(
                    getProductsUseCase,
                    addProductUseCase,
                    updateProductUseCase,
                    deleteProductUseCase,
                )
            advanceUntilIdle()

            // When
            viewModel.effect.test {
                viewModel.handleIntent(InventoryIntent.AddProduct(newProduct))
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(effect is InventoryEffect.ShowError)
                assertEquals("Erro ao adicionar", (effect as InventoryEffect.ShowError).message)
            }
        }

    @Test
    fun `deleteProduct should call use case and emit success effect`() =
        runTest {
            // Given
            val productId = "1"
            coEvery { deleteProductUseCase(any()) } returns Result.Success(Unit)
            viewModel =
                InventoryViewModel(
                    getProductsUseCase,
                    addProductUseCase,
                    updateProductUseCase,
                    deleteProductUseCase,
                )
            advanceUntilIdle()

            // When
            viewModel.effect.test {
                viewModel.handleIntent(InventoryIntent.DeleteProduct(productId))
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(effect is InventoryEffect.ShowToast)
                assertEquals("Produto removido com sucesso", (effect as InventoryEffect.ShowToast).message)
                coVerify { deleteProductUseCase(productId) }
            }
        }

    @Test
    fun `openAddDialog should update state`() =
        runTest {
            // Given
            viewModel =
                InventoryViewModel(
                    getProductsUseCase,
                    addProductUseCase,
                    updateProductUseCase,
                    deleteProductUseCase,
                )
            advanceUntilIdle()

            // When
            viewModel.handleIntent(InventoryIntent.OpenAddDialog)
            advanceUntilIdle()

            // Then
            assertTrue(viewModel.state.value.showAddDialog)
        }

    @Test
    fun `closeAddDialog should update state`() =
        runTest {
            // Given
            viewModel =
                InventoryViewModel(
                    getProductsUseCase,
                    addProductUseCase,
                    updateProductUseCase,
                    deleteProductUseCase,
                )
            advanceUntilIdle()
            viewModel.handleIntent(InventoryIntent.OpenAddDialog)
            advanceUntilIdle()

            // When
            viewModel.handleIntent(InventoryIntent.CloseAddDialog)
            advanceUntilIdle()

            // Then
            assertFalse(viewModel.state.value.showAddDialog)
        }

    @Test
    fun `openBarcodeScanner should emit navigation effect`() =
        runTest {
            // Given
            viewModel =
                InventoryViewModel(
                    getProductsUseCase,
                    addProductUseCase,
                    updateProductUseCase,
                    deleteProductUseCase,
                )
            advanceUntilIdle()

            // When
            viewModel.effect.test {
                viewModel.handleIntent(InventoryIntent.OpenBarcodeScanner)
                advanceUntilIdle()

                // Then
                val effect = awaitItem()
                assertTrue(effect is InventoryEffect.NavigateToBarcodeScanner)
            }
        }
}
