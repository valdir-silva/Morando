package com.alunando.morando.feature.inventory.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.alunando.morando.core.ui.ImagePicker
import com.alunando.morando.domain.model.Product
import com.alunando.morando.feature.inventory.presentation.InventoryEffect
import com.alunando.morando.feature.inventory.presentation.InventoryIntent
import com.alunando.morando.feature.inventory.presentation.InventoryViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tela de gerenciamento de estoque de produtos
 */
@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun InventoryScreen(
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = koinViewModel(),
    onNavigateToBarcodeScanner: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Observa efeitos
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is InventoryEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is InventoryEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is InventoryEffect.NavigateToBarcodeScanner -> {
                    onNavigateToBarcodeScanner()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(InventoryIntent.OpenAddDialog) },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar produto")
            }
        },
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                state.products.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center),
                        onAddClick = { viewModel.handleIntent(InventoryIntent.OpenAddDialog) },
                    )
                }

                else -> {
                    ProductList(
                        products = state.products,
                        onDeleteProduct = { productId ->
                            viewModel.handleIntent(InventoryIntent.DeleteProduct(productId))
                        },
                        onEditProduct = { product ->
                            viewModel.handleIntent(InventoryIntent.OpenEditDialog(product))
                        },
                    )
                }
            }
        }

        // Dialog de adicionar produto
        if (state.showAddDialog) {
            AddProductDialog(
                onDismiss = { viewModel.handleIntent(InventoryIntent.CloseAddDialog) },
                onConfirm = { product, imageData ->
                    viewModel.handleIntent(InventoryIntent.AddProduct(product, imageData))
                },
                onScanBarcode = {
                    viewModel.handleIntent(InventoryIntent.OpenBarcodeScanner)
                },
                scannedProduct = state.scannedProduct,
            )
        }

        // Dialog de editar produto
        state.editingProduct?.let { editingProduct ->
            if (state.showEditDialog) {
                EditProductDialog(
                    product = editingProduct,
                    onDismiss = { viewModel.handleIntent(InventoryIntent.CloseAddDialog) },
                    onConfirm = { product, imageData ->
                        viewModel.handleIntent(InventoryIntent.UpdateProduct(product, imageData))
                    },
                )
            }
        }
    }
}

@Composable
private fun ProductList(
    products: List<Product>,
    onDeleteProduct: (String) -> Unit,
    onEditProduct: (Product) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onDelete = { onDeleteProduct(product.id) },
                onEdit = { onEditProduct(product) },
            )
        }
    }
}

@Suppress("LongMethod", "MagicNumber")
@Composable
private fun ProductCard(
    product: Product,
    onDelete: () -> Unit,
    onEdit: () -> Unit = {},
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(240.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onEdit,
    ) {
        Column {
            // Imagem do produto
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                if (product.fotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = product.fotoUrl,
                        contentDescription = product.nome,
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(
                        text = "📦",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                // Badge de vencimento
                if (product.isVencido() || product.isProximoVencimento()) {
                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(
                                    color =
                                        if (product.isVencido()) {
                                            Color.Red
                                        } else {
                                            Color(0xFFFF9800)
                                        },
                                    shape = RoundedCornerShape(4.dp),
                                ).padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = if (product.isVencido()) "Vencido" else "Vence em breve",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                        )
                    }
                }
            }

            // Informações do produto
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
            ) {
                Text(
                    text = product.nome,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                if (product.categoria.isNotEmpty()) {
                    Text(
                        text = product.categoria,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        if (product.valor > 0) {
                            Text(
                                text =
                                    NumberFormat
                                        .getCurrencyInstance(Locale("pt", "BR"))
                                        .format(product.valor),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }

                        product.dataVencimento?.let { vencimento ->
                            Text(
                                text =
                                    "Venc: ${
                                        SimpleDateFormat(
                                            "dd/MM/yy",
                                            Locale.getDefault(),
                                        ).format(vencimento)
                                    }",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Deletar",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "📦",
            style = MaterialTheme.typography.displayLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhum produto cadastrado",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Adicione produtos para começar a controlar seu estoque",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddClick) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Adicionar Produto")
        }
    }
}

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
private fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (Product, ByteArray?) -> Unit,
    onScanBarcode: () -> Unit,
    scannedProduct: Product? = null,
) {
    var nome by remember(scannedProduct) { mutableStateOf(scannedProduct?.nome ?: "") }
    var categoria by remember(scannedProduct) { mutableStateOf(scannedProduct?.categoria ?: "") }
    var codigoBarras by remember(scannedProduct) { mutableStateOf(scannedProduct?.codigoBarras ?: "") }
    var valor by remember(scannedProduct) {
        mutableStateOf(
            if (scannedProduct?.valor != null && scannedProduct.valor > 0) {
                scannedProduct.valor.toString()
            } else {
                ""
            },
        )
    }
    var detalhes by remember(scannedProduct) { mutableStateOf(scannedProduct?.detalhes ?: "") }
    var imageData by remember { mutableStateOf<ByteArray?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adicionar Produto") },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // ImagePicker
                ImagePicker(
                    imageUrl = scannedProduct?.fotoUrl,
                    onImageSelected = { bytes ->
                        imageData = bytes
                    },
                    onImageRemoved = {
                        imageData = null
                    },
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onScanBarcode,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Escanear Código de Barras")
                }

                if (codigoBarras.isNotEmpty()) {
                    OutlinedTextField(
                        value = codigoBarras,
                        onValueChange = { codigoBarras = it },
                        label = { Text("Código de Barras") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = false,
                    )
                }

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do produto *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoria") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = valor,
                    onValueChange = { valor = it },
                    label = { Text("Valor (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = detalhes,
                    onValueChange = { detalhes = it },
                    label = { Text("Detalhes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nome.isNotBlank()) {
                        val product =
                            Product(
                                nome = nome,
                                categoria = categoria,
                                codigoBarras = codigoBarras,
                                valor = valor.toDoubleOrNull() ?: 0.0,
                                detalhes = detalhes,
                                dataCompra = Date(),
                                createdAt = Date(),
                            )
                        onConfirm(product, imageData)
                    }
                },
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}

@Suppress("LongMethod")
@Composable
private fun EditProductDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (Product, ByteArray?) -> Unit,
) {
    var nome by remember(product) { mutableStateOf(product.nome) }
    var categoria by remember(product) { mutableStateOf(product.categoria) }
    var codigoBarras by remember(product) { mutableStateOf(product.codigoBarras) }
    var valor by remember(product) {
        mutableStateOf(
            if (product.valor > 0) {
                product.valor.toString()
            } else {
                ""
            },
        )
    }
    var detalhes by remember(product) { mutableStateOf(product.detalhes) }
    var imageData by remember { mutableStateOf<ByteArray?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Produto") },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // ImagePicker
                ImagePicker(
                    imageUrl = product.fotoUrl,
                    onImageSelected = { bytes ->
                        imageData = bytes
                    },
                    onImageRemoved = {
                        imageData = null
                    },
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (codigoBarras.isNotEmpty()) {
                    OutlinedTextField(
                        value = codigoBarras,
                        onValueChange = { codigoBarras = it },
                        label = { Text("Código de Barras") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = false,
                    )
                }

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do produto *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoria") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = valor,
                    onValueChange = { valor = it },
                    label = { Text("Valor (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                OutlinedTextField(
                    value = detalhes,
                    onValueChange = { detalhes = it },
                    label = { Text("Detalhes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nome.isNotBlank()) {
                        val updatedProduct =
                            product.copy(
                                nome = nome,
                                categoria = categoria,
                                codigoBarras = codigoBarras,
                                valor = valor.toDoubleOrNull() ?: 0.0,
                                detalhes = detalhes,
                            )
                        onConfirm(updatedProduct, imageData)
                    }
                },
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}
