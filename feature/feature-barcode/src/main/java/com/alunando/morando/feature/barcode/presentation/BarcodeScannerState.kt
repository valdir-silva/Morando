package com.alunando.morando.feature.barcode.presentation

/**
 * Estado da tela de scanner de código de barras
 */
data class BarcodeScannerState(
    val hasCameraPermission: Boolean = false,
    val isScanning: Boolean = false,
    val scannedBarcode: String? = null,
    val error: String? = null,
)
