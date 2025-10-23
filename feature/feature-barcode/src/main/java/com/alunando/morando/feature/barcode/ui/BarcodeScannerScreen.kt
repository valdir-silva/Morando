package com.alunando.morando.feature.barcode.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.alunando.morando.feature.barcode.presentation.BarcodeScannerEffect
import com.alunando.morando.feature.barcode.presentation.BarcodeScannerIntent
import com.alunando.morando.feature.barcode.presentation.BarcodeScannerViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executors

/**
 * Tela de scanner de código de barras com CameraX e ML Kit
 */
@Composable
@Suppress("LongMethod", "MagicNumber")
fun BarcodeScannerScreen(
    onBarcodeScanned: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: BarcodeScannerViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Solicita permissão da câmera
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            viewModel.handleIntent(BarcodeScannerIntent.SetCameraPermission(isGranted))
        }

    // Verifica permissão ao iniciar
    LaunchedEffect(Unit) {
        val hasPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.handleIntent(BarcodeScannerIntent.SetCameraPermission(true))
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Observa efeitos
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is BarcodeScannerEffect.NavigateBackWithResult -> {
                    onBarcodeScanned(effect.barcode)
                    onNavigateBack()
                }
                is BarcodeScannerEffect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.hasCameraPermission) {
            CameraPreview(
                onBarcodeDetected = { barcode ->
                    if (state.isScanning) {
                        viewModel.handleIntent(BarcodeScannerIntent.OnBarcodeDetected(barcode))
                    }
                },
            )

            // Overlay com guia de escaneamento
            Box(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .size(250.dp)
                        .border(2.dp, Color.White, RoundedCornerShape(16.dp)),
            )

            // Instruções
            Column(
                modifier =
                    Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Aponte para o código de barras",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                RoundedCornerShape(8.dp),
                            ).padding(16.dp),
                )
            }
        } else {
            // Tela de permissão negada
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Permissão da câmera necessária",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Para escanear códigos de barras, precisamos acessar sua câmera.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Conceder Permissão")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Botão voltar
        IconButton(
            onClick = { viewModel.handleIntent(BarcodeScannerIntent.NavigateBack) },
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White,
                modifier =
                    Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .padding(8.dp),
            )
        }
    }
}

@Suppress("MagicNumber", "LongMethod", "TooGenericExceptionCaught")
@Composable
private fun CameraPreview(onBarcodeDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    // Configura o scanner de código de barras
    val barcodeScanner =
        remember {
            val options =
                BarcodeScannerOptions
                    .Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_EAN_13,
                        Barcode.FORMAT_EAN_8,
                        Barcode.FORMAT_UPC_A,
                        Barcode.FORMAT_UPC_E,
                    ).build()
            BarcodeScanning.getClient(options)
        }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
            barcodeScanner.close()
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview =
                Preview
                    .Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

            // Análise de imagem
            val imageAnalysis =
                ImageAnalysis
                    .Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor) { imageProxy ->
                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val image =
                                    InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees,
                                    )

                                barcodeScanner
                                    .process(image)
                                    .addOnSuccessListener { barcodes ->
                                        barcodes.firstOrNull()?.rawValue?.let { barcode ->
                                            onBarcodeDetected(barcode)
                                        }
                                    }.addOnCompleteListener {
                                        imageProxy.close()
                                    }
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

            // Câmera traseira
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis,
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            previewView
        },
        modifier = Modifier.fillMaxSize(),
    )
}
