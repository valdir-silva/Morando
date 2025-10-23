package com.alunando.morando.feature.barcode.presentation

/**
 * Efeitos colaterais da tela de scanner
 */
sealed class BarcodeScannerEffect {
    data class NavigateBackWithResult(val barcode: String) : BarcodeScannerEffect()
    data object NavigateBack : BarcodeScannerEffect()
}

