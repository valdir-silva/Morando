package com.alunando.morando.feature.inventory.presentation

import com.alunando.morando.domain.model.Product

/**
 * Intenções do usuário na tela de inventário (MVI)
 */
sealed interface InventoryIntent {
    data object LoadProducts : InventoryIntent

    data class AddProduct(
        val product: Product,
        val imageData: ByteArray? = null,
    ) : InventoryIntent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AddProduct

            if (product != other.product) return false
            if (!imageData.contentEquals(other.imageData)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = product.hashCode()
            result = 31 * result + (imageData?.contentHashCode() ?: 0)
            return result
        }
    }

    data class UpdateProduct(
        val product: Product,
        val imageData: ByteArray? = null,
    ) : InventoryIntent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UpdateProduct

            if (product != other.product) return false
            if (!imageData.contentEquals(other.imageData)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = product.hashCode()
            result = 31 * result + (imageData?.contentHashCode() ?: 0)
            return result
        }
    }

    data class DeleteProduct(
        val productId: String,
    ) : InventoryIntent

    data object OpenAddDialog : InventoryIntent

    data class OpenEditDialog(
        val product: Product,
    ) : InventoryIntent

    data object CloseAddDialog : InventoryIntent

    data object OpenBarcodeScanner : InventoryIntent

    data class OnBarcodeScanned(
        val barcode: String,
    ) : InventoryIntent

    data class FilterByCategory(
        val category: String?,
    ) : InventoryIntent
}
