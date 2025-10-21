package com.alunando.morando.feature.barcode.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel para scanner de código de barras
 * Implementar com ML Kit e CameraX
 */
class BarcodeScannerViewModel : ViewModel() {
    private val _barcode = MutableStateFlow<String?>(null)
    val barcode: StateFlow<String?> = _barcode.asStateFlow()

    fun onBarcodeScanned(code: String) {
        _barcode.value = code
    }

    fun resetBarcode() {
        _barcode.value = null
    }
}
