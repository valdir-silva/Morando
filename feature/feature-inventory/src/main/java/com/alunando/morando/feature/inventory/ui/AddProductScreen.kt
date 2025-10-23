package com.alunando.morando.feature.inventory.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alunando.morando.core.ui.ImagePicker
import com.alunando.morando.domain.model.Product
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Tela para adicionar novo produto
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    onNavigateBack: () -> Unit,
    onSaveProduct: (Product, ByteArray?) -> Unit,
    onNavigateToBarcodeScanner: () -> Unit,
    scannedBarcode: String? = null,
) {
    var nome by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var codigoBarras by remember { mutableStateOf(scannedBarcode ?: "") }
    var valor by remember { mutableStateOf("") }
    var detalhes by remember { mutableStateOf("") }
    var dataCompra by remember { mutableStateOf(Date()) }
    var dataVencimento by remember { mutableStateOf<Date?>(null) }
    var imageData by remember { mutableStateOf<ByteArray?>(null) }

    // Atualiza código de barras quando scanear
    scannedBarcode?.let { barcode ->
        if (codigoBarras != barcode) {
            codigoBarras = barcode
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar Produto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            // Nome do produto
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do Produto *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categoria
            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoria") },
                placeholder = { Text("Ex: Alimentos, Bebidas, Limpeza") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Código de barras com botão de scanner
            OutlinedTextField(
                value = codigoBarras,
                onValueChange = { codigoBarras = it },
                label = { Text("Código de Barras") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = onNavigateToBarcodeScanner) {
                        Icon(Icons.Default.AddCircle, "Escanear código")
                    }
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Valor
            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text("Valor (R$)") },
                placeholder = { Text("0,00") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Data de compra
            DatePickerField(
                label = "Data de Compra",
                date = dataCompra,
                onDateSelected = { dataCompra = it },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Data de validade
            DatePickerField(
                label = "Data de Validade",
                date = dataVencimento,
                onDateSelected = { dataVencimento = it },
                optional = true,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Detalhes
            OutlinedTextField(
                value = detalhes,
                onValueChange = { detalhes = it },
                label = { Text("Detalhes/Observações") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Seletor de imagem
            ImagePicker(
                onImageSelected = { bytes ->
                    imageData = bytes
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão de salvar
            Button(
                onClick = {
                    if (nome.isNotBlank()) {
                        val product =
                            Product(
                                nome = nome,
                                categoria = categoria,
                                codigoBarras = codigoBarras,
                                valor = valor.replace(",", ".").toDoubleOrNull() ?: 0.0,
                                detalhes = detalhes,
                                dataCompra = dataCompra,
                                dataVencimento = dataVencimento,
                                createdAt = Date(),
                            )
                        onSaveProduct(product, imageData)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nome.isNotBlank(),
            ) {
                Text("Salvar Produto")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campo obrigatório
            Text(
                text = "* Campo obrigatório",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DatePickerField(
    label: String,
    date: Date?,
    onDateSelected: (Date) -> Unit,
    optional: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val displayDate = date?.let { dateFormat.format(it) } ?: if (optional) "" else dateFormat.format(Date())

    OutlinedTextField(
        value = displayDate,
        onValueChange = { },
        label = { Text(label + if (optional) "" else " *") },
        modifier = modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            OutlinedButton(onClick = { showDatePicker = true }) {
                Text(if (date == null && optional) "Selecionar" else "Alterar")
            }
        },
    )

    if (showDatePicker) {
        SimpleDatePickerDialog(
            initialDate = date ?: Date(),
            onDateSelected = {
                onDateSelected(it)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
}

@Composable
private fun SimpleDatePickerDialog(
    initialDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit,
) {
    val calendar = Calendar.getInstance().apply { time = initialDate }
    var day by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH).toString()) }
    var month by remember { mutableStateOf((calendar.get(Calendar.MONTH) + 1).toString()) }
    var year by remember { mutableStateOf(calendar.get(Calendar.YEAR).toString()) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecionar Data") },
        text = {
            Column {
                OutlinedTextField(
                    value = day,
                    onValueChange = { if (it.length <= 2) day = it },
                    label = { Text("Dia") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = month,
                    onValueChange = { if (it.length <= 2) month = it },
                    label = { Text("Mês") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = year,
                    onValueChange = { if (it.length <= 4) year = it },
                    label = { Text("Ano") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                try {
                    val selectedCalendar =
                        Calendar.getInstance().apply {
                            set(Calendar.YEAR, year.toIntOrNull() ?: 2025)
                            set(Calendar.MONTH, (month.toIntOrNull() ?: 1) - 1)
                            set(Calendar.DAY_OF_MONTH, day.toIntOrNull() ?: 1)
                        }
                    onDateSelected(selectedCalendar.time)
                } catch (e: Exception) {
                    onDismiss()
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}
