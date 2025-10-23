package com.alunando.morando.feature.inventory.presentation

/**
 * Efeitos colaterais da tela de inventário (MVI)
 */
sealed interface InventoryEffect {
    data class ShowToast(
        val message: String,
    ) : InventoryEffect

    data class ShowError(
        val message: String,
    ) : InventoryEffect

    data object NavigateToBarcodeScanner : InventoryEffect
}
