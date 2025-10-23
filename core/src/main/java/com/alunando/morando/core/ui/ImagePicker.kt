package com.alunando.morando.core.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import coil.compose.AsyncImage
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Componente para selecionar e capturar imagens
 */
@Composable
fun ImagePicker(
    imageUri: Uri? = null,
    imageUrl: String? = null,
    onImageSelected: (ByteArray) -> Unit,
    onImageRemoved: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Launcher para capturar foto da câmera
    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
        ) { success ->
            if (success && tempImageUri != null) {
                val imageBytes = uriToCompressedByteArray(context, tempImageUri!!)
                imageBytes?.let { onImageSelected(it) }
                bitmap = uriToBitmap(context, tempImageUri!!)
            }
        }

    // Launcher para selecionar da galeria
    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                val imageBytes = uriToCompressedByteArray(context, it)
                imageBytes?.let { bytes -> onImageSelected(bytes) }
                bitmap = uriToBitmap(context, it)
            }
        }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Preview da imagem
        Box(
            modifier =
                Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            when {
                bitmap != null -> {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Imagem selecionada",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                    )
                }

                imageUri != null -> {
                    // Imagem local
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Imagem do produto",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                    )
                }

                !imageUrl.isNullOrEmpty() -> {
                    // Imagem remota
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagem do produto",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop,
                    )
                }

                else -> {
                    Icon(
                        painter = painterResource(id = com.alunando.morando.core.R.drawable.ic_photo_library),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Botão de remover
            if (bitmap != null || imageUri != null || !imageUrl.isNullOrEmpty()) {
                IconButton(
                    onClick = {
                        bitmap = null
                        onImageRemoved()
                    },
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remover imagem",
                        tint = MaterialTheme.colorScheme.error,
                        modifier =
                            Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    RoundedCornerShape(50),
                                ).padding(4.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botões de ação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedButton(
                onClick = {
                    val uri = createTempImageUri(context)
                    tempImageUri = uri
                    cameraLauncher.launch(uri)
                },
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    painter = painterResource(id = com.alunando.morando.core.R.drawable.ic_photo_camera),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Câmera")
            }

            OutlinedButton(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    painter = painterResource(id = com.alunando.morando.core.R.drawable.ic_photo_library),
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Galeria")
            }
        }
    }
}

/**
 * Cria URI temporária para captura de foto
 */
private fun createTempImageUri(context: Context): Uri {
    val tempFile =
        File
            .createTempFile(
                "temp_image_${System.currentTimeMillis()}",
                ".jpg",
                context.cacheDir,
            ).apply {
                createNewFile()
                deleteOnExit()
            }

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile,
    )
}

/**
 * Converte URI para Bitmap com correção de rotação
 */
@Suppress("MagicNumber")
private fun uriToBitmap(
    context: Context,
    uri: Uri,
): Bitmap? =
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Corrige rotação baseada no EXIF
        val exifInputStream = context.contentResolver.openInputStream(uri)
        val exif = exifInputStream?.let { ExifInterface(it) }
        exifInputStream?.close()

        val rotation =
            when (exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }

        if (rotation != 0f) {
            val matrix = Matrix().apply { postRotate(rotation) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

/**
 * Converte URI para ByteArray comprimido (JPEG 80% qualidade, max 1024x1024)
 */
@Suppress("MagicNumber")
private fun uriToCompressedByteArray(
    context: Context,
    uri: Uri,
): ByteArray? {
    return try {
        val bitmap = uriToBitmap(context, uri) ?: return null

        // Redimensiona se necessário
        val maxSize = 1024
        val ratio = maxSize.toFloat() / maxOf(bitmap.width, bitmap.height)
        val resizedBitmap =
            if (ratio < 1) {
                val newWidth = (bitmap.width * ratio).toInt()
                val newHeight = (bitmap.height * ratio).toInt()
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }

        // Comprime para JPEG
        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.toByteArray()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
