package com.alunando.morando.feature.inventory.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.usecase.AddProductUseCase
import com.alunando.morando.domain.usecase.DeleteProductUseCase
import com.alunando.morando.domain.usecase.GetProductsUseCase
import com.alunando.morando.domain.usecase.UpdateProductUseCase
import com.alunando.morando.feature.inventory.presentation.InventoryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Date

class InventoryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var addProductUseCase: AddProductUseCase
    private lateinit var updateProductUseCase: UpdateProductUseCase
    private lateinit var deleteProductUseCase: DeleteProductUseCase
    private lateinit var viewModel: InventoryViewModel

    private val mockProducts =
        listOf(
            Product(
                id = "1",
                nome = "Leite Integral",
                categoria = "Laticínios",
                valor = 4.50,
                createdAt = Date(),
            ),
            Product(
                id = "2",
                nome = "Arroz Branco",
                categoria = "Grãos",
                valor = 25.90,
                createdAt = Date(),
            ),
        )

    @Before
    fun setup() {
        getProductsUseCase = mockk()
        addProductUseCase = mockk()
        updateProductUseCase = mockk()
        deleteProductUseCase = mockk()
    }

    @Test
    fun emptyState_isDisplayed_whenNoProducts() {
        // Given
        every { getProductsUseCase() } returns flowOf(emptyList())
        viewModel =
            InventoryViewModel(
                getProductsUseCase,
                addProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
            )

        // When
        composeTestRule.setContent {
            InventoryScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule
            .onNodeWithText("Nenhum produto cadastrado")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Adicione produtos para começar a controlar seu estoque")
            .assertIsDisplayed()
    }

    @Test
    fun productList_isDisplayed_whenProductsExist() {
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
        composeTestRule.setContent {
            InventoryScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule
            .onNodeWithText("Leite Integral")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Arroz Branco")
            .assertIsDisplayed()
    }

    @Test
    fun fab_opensAddDialog_whenClicked() {
        // Given
        every { getProductsUseCase() } returns flowOf(emptyList())
        viewModel =
            InventoryViewModel(
                getProductsUseCase,
                addProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
            )

        // When
        composeTestRule.setContent {
            InventoryScreen(viewModel = viewModel)
        }
        composeTestRule
            .onNodeWithContentDescription("Adicionar produto")
            .performClick()

        // Then
        composeTestRule
            .onNodeWithText("Adicionar Produto")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Nome do produto")
            .assertIsDisplayed()
    }

    @Test
    fun addProductDialog_canEnterProductData() {
        // Given
        every { getProductsUseCase() } returns flowOf(emptyList())
        viewModel =
            InventoryViewModel(
                getProductsUseCase,
                addProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
            )

        // When
        composeTestRule.setContent {
            InventoryScreen(viewModel = viewModel)
        }
        composeTestRule
            .onNodeWithContentDescription("Adicionar produto")
            .performClick()

        // Then - Verify fields are editable
        composeTestRule
            .onNodeWithText("Nome do produto")
            .performTextInput("Novo Produto")
        composeTestRule
            .onNodeWithText("Categoria")
            .performTextInput("Nova Categoria")
        composeTestRule
            .onNodeWithText("Valor (R$)")
            .performTextInput("10.50")
    }

    @Test
    fun addProductDialog_showsBarcodeButton() {
        // Given
        every { getProductsUseCase() } returns flowOf(emptyList())
        viewModel =
            InventoryViewModel(
                getProductsUseCase,
                addProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
            )

        // When
        composeTestRule.setContent {
            InventoryScreen(viewModel = viewModel)
        }
        composeTestRule
            .onNodeWithContentDescription("Adicionar produto")
            .performClick()

        // Then
        composeTestRule
            .onNodeWithText("Escanear Código de Barras")
            .assertIsDisplayed()
    }

    @Test
    fun productCard_displaysCorrectInformation() {
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
        composeTestRule.setContent {
            InventoryScreen(viewModel = viewModel)
        }

        // Then
        composeTestRule
            .onNodeWithText("Leite Integral")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Laticínios")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("R$ 4,50")
            .assertIsDisplayed()
    }
}
