package com.alunando.morando.feature.barcode.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Tela de scanner de código de barras
 * Implementar CameraX Preview e ML Kit Barcode Scanning
 */
@Composable
@Suppress("UnusedParameter")
fun BarcodeScannerScreen(onBarcodeScanned: (String) -> Unit) {
    // Placeholder para implementação futura
    // onBarcodeScanned será usado quando implementarmos o scanner
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Implementar Camera Preview com ML Kit")
    }
}
