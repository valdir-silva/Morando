package com.alunando.morando.feature.barcode.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para scanner de código de barras (MVI)
 */
class BarcodeScannerViewModel : ViewModel() {
    private val _state = MutableStateFlow(BarcodeScannerState())
    val state: StateFlow<BarcodeScannerState> = _state.asStateFlow()

    private val _effect = Channel<BarcodeScannerEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: BarcodeScannerIntent) {
        when (intent) {
            is BarcodeScannerIntent.SetCameraPermission -> setCameraPermission(intent.granted)
            is BarcodeScannerIntent.OnBarcodeDetected -> onBarcodeDetected(intent.barcode)
            is BarcodeScannerIntent.ClearError -> clearError()
            is BarcodeScannerIntent.NavigateBack -> navigateBack()
        }
    }

    private fun setCameraPermission(granted: Boolean) {
        _state.value =
            _state.value.copy(
                hasCameraPermission = granted,
                isScanning = granted,
            )
    }

    private fun onBarcodeDetected(barcode: String) {
        _state.value =
            _state.value.copy(
                scannedBarcode = barcode,
                isScanning = false,
            )
        viewModelScope.launch {
            _effect.send(BarcodeScannerEffect.NavigateBackWithResult(barcode))
        }
    }

    private fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.send(BarcodeScannerEffect.NavigateBack)
        }
    }
}
