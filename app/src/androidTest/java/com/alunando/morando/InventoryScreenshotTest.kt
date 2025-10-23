package com.alunando.morando

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.usecase.AddProductUseCase
import com.alunando.morando.domain.usecase.DeleteProductUseCase
import com.alunando.morando.domain.usecase.GetProductInfoFromBarcodeUseCase
import com.alunando.morando.domain.usecase.GetProductsUseCase
import com.alunando.morando.domain.usecase.UpdateProductUseCase
import com.alunando.morando.domain.usecase.UploadProductImageUseCase
import com.alunando.morando.feature.inventory.presentation.InventoryViewModel
import com.alunando.morando.feature.inventory.ui.InventoryScreen
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy
import tools.fastlane.screengrab.locale.LocaleTestRule
import java.util.Calendar
import java.util.Date

/**
 * Testes de screenshots para Fastlane Screengrab
 * Execute com: fastlane screenshots
 */
@RunWith(AndroidJUnit4::class)
class InventoryScreenshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var addProductUseCase: AddProductUseCase
    private lateinit var updateProductUseCase: UpdateProductUseCase
    private lateinit var deleteProductUseCase: DeleteProductUseCase
    private lateinit var uploadProductImageUseCase: UploadProductImageUseCase
    private lateinit var getProductInfoFromBarcodeUseCase: GetProductInfoFromBarcodeUseCase

    private val mockProducts =
        listOf(
            Product(
                id = "1",
                nome = "Leite Integral",
                categoria = "Laticínios",
                codigoBarras = "7891234567890",
                fotoUrl = "https://via.placeholder.com/200",
                dataCompra = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -5) }.time,
                valor = 4.50,
                detalhes = "Leite integral 1L",
                dataVencimento = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 3) }.time,
                createdAt = Date(),
            ),
            Product(
                id = "2",
                nome = "Arroz Branco",
                categoria = "Grãos",
                codigoBarras = "7891234567891",
                fotoUrl = "https://via.placeholder.com/200",
                dataCompra = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -10) }.time,
                valor = 25.90,
                detalhes = "Arroz branco tipo 1, 5kg",
                dataVencimento = Calendar.getInstance().apply { add(Calendar.MONTH, 6) }.time,
                createdAt = Date(),
            ),
            Product(
                id = "3",
                nome = "Feijão Preto",
                categoria = "Grãos",
                codigoBarras = "7891234567892",
                fotoUrl = "https://via.placeholder.com/200",
                dataCompra = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -8) }.time,
                valor = 8.90,
                detalhes = "Feijão preto 1kg",
                dataVencimento = Calendar.getInstance().apply { add(Calendar.MONTH, 12) }.time,
                createdAt = Date(),
            ),
            Product(
                id = "4",
                nome = "Tomate",
                categoria = "Hortifruti",
                fotoUrl = "https://via.placeholder.com/200",
                dataCompra = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }.time,
                valor = 6.50,
                detalhes = "Tomate fresco 1kg",
                dataVencimento = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 5) }.time,
                createdAt = Date(),
            ),
            Product(
                id = "5",
                nome = "Pão de Forma",
                categoria = "Padaria",
                codigoBarras = "7891234567894",
                fotoUrl = "https://via.placeholder.com/200",
                dataCompra = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
                valor = 5.80,
                detalhes = "Pão de forma integral 500g",
                dataVencimento = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 2) }.time,
                createdAt = Date(),
            ),
        )

    companion object {
        @ClassRule
        @JvmField
        val localeTestRule = LocaleTestRule()
    }

    @Before
    fun setup() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
        getProductsUseCase = mockk()
        addProductUseCase = mockk()
        updateProductUseCase = mockk()
        deleteProductUseCase = mockk()
        uploadProductImageUseCase = mockk()
        getProductInfoFromBarcodeUseCase = mockk()
    }

    @Test
    fun screenshot_01_inventory_empty_state() {
        // Given
        every { getProductsUseCase() } returns flowOf(emptyList())
        val viewModel =
            InventoryViewModel(
                getProductsUseCase,
                addProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
                uploadProductImageUseCase,
                getProductInfoFromBarcodeUseCase,
            )

        // When
        composeTestRule.setContent {
            InventoryScreen(viewModel = viewModel)
        }

        // Wait for UI to settle
        composeTestRule.waitForIdle()

        // Then
        Screengrab.screenshot("01_inventory_empty_state")
    }

    @Test
    fun screenshot_02_inventory_list_with_products() {
        // Given
        every { getProductsUseCase() } returns flowOf(mockProducts)
        val viewModel =
            InventoryViewModel(
                getProductsUseCase,
                addProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
                uploadProductImageUseCase,
                getProductInfoFromBarcodeUseCase,
            )

        // When
        composeTestRule.setContent {
            InventoryScreen(viewModel = viewModel)
        }

        // Wait for UI to settle
        composeTestRule.waitForIdle()

        // Then
        Screengrab.screenshot("02_inventory_list_with_products")
    }

    @Test
    fun screenshot_03_inventory_add_product_dialog() {
        // Given
        every { getProductsUseCase() } returns flowOf(mockProducts)
        val viewModel =
            InventoryViewModel(
                getProductsUseCase,
                addProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
                uploadProductImageUseCase,
                getProductInfoFromBarcodeUseCase,
            )

        // When
        composeTestRule.setContent {
            InventoryScreen(viewModel = viewModel)
        }

        // Wait for UI to settle
        composeTestRule.waitForIdle()

        // Open add dialog
        composeTestRule
            .onNodeWithContentDescription("Adicionar produto")
            .performClick()

        // Wait for dialog to open
        composeTestRule.waitForIdle()

        // Then
        Screengrab.screenshot("03_inventory_add_product_dialog")
    }

    @Test
    fun screenshot_04_inventory_products_expiring_soon() {
        // Given - Only products that are expiring soon
        val expiringProducts =
            mockProducts.filter { it.isProximoVencimento() || it.isVencido() }
        every { getProductsUseCase() } returns flowOf(expiringProducts)
        val viewModel =
            InventoryViewModel(
                getProductsUseCase,
                addProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
                uploadProductImageUseCase,
                getProductInfoFromBarcodeUseCase,
            )

        // When
        composeTestRule.setContent {
            InventoryScreen(viewModel = viewModel)
        }

        // Wait for UI to settle
        composeTestRule.waitForIdle()

        // Then
        Screengrab.screenshot("04_inventory_products_expiring_soon")
    }

    @Test
    fun screenshot_05_inventory_empty_with_fab() {
        // Given
        every { getProductsUseCase() } returns flowOf(emptyList())
        val viewModel =
            InventoryViewModel(
                getProductsUseCase,
                addProductUseCase,
                updateProductUseCase,
                deleteProductUseCase,
                uploadProductImageUseCase,
                getProductInfoFromBarcodeUseCase,
            )

        // When
        composeTestRule.setContent {
            InventoryScreen(viewModel = viewModel)
        }

        // Wait for UI to settle
        composeTestRule.waitForIdle()

        // Verify FAB is visible
        composeTestRule
            .onNodeWithContentDescription("Adicionar produto")
            .assertExists()

        // Then
        Screengrab.screenshot("05_inventory_empty_with_fab")
    }
}
