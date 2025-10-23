package com.alunando.morando.feature.inventory.presentation

import com.alunando.morando.domain.model.Product

/**
 * Intenções do usuário na tela de inventário (MVI)
 */
sealed interface InventoryIntent {
    data object LoadProducts : InventoryIntent
    data class AddProduct(val product: Product, val imageData: ByteArray? = null) : InventoryIntent
    data class UpdateProduct(val product: Product, val imageData: ByteArray? = null) : InventoryIntent
    data class DeleteProduct(val productId: String) : InventoryIntent
    data object OpenAddDialog : InventoryIntent
    data class OpenEditDialog(val product: Product) : InventoryIntent
    data object CloseAddDialog : InventoryIntent
    data object OpenBarcodeScanner : InventoryIntent
    data class OnBarcodeScanned(val barcode: String) : InventoryIntent
    data class FilterByCategory(val category: String?) : InventoryIntent
}
