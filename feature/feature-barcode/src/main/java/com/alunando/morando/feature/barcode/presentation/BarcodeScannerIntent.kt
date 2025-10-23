package com.alunando.morando.feature.barcode.presentation

/**
 * Intenções do usuário na tela de scanner
 */
sealed class BarcodeScannerIntent {
    data class SetCameraPermission(
        val granted: Boolean,
    ) : BarcodeScannerIntent()

    data class OnBarcodeDetected(
        val barcode: String,
    ) : BarcodeScannerIntent()

    data object ClearError : BarcodeScannerIntent()

    data object NavigateBack : BarcodeScannerIntent()
}
