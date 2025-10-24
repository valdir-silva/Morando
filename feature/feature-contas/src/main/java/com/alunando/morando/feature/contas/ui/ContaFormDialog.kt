package com.alunando.morando.feature.contas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alunando.morando.feature.contas.domain.model.Conta
import com.alunando.morando.feature.contas.domain.model.ContaCategoria
import com.alunando.morando.feature.contas.domain.model.ContaRecorrencia
import com.alunando.morando.feature.contas.domain.model.ContaStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Dialog para criar/editar contas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContaFormDialog(
    onDismiss: () -> Unit,
    onSave: (Conta) -> Unit,
    existingConta: Conta? = null,
    modifier: Modifier = Modifier,
) {
    var nome by remember { mutableStateOf(existingConta?.nome ?: "") }
    var descricao by remember { mutableStateOf(existingConta?.descricao ?: "") }
    var valorStr by remember { mutableStateOf(existingConta?.valor?.toString() ?: "") }
    var categoria by remember { mutableStateOf(existingConta?.categoria ?: ContaCategoria.OUTROS) }
    var recorrencia by remember { mutableStateOf(existingConta?.recorrencia ?: ContaRecorrencia.NENHUMA) }
    var dataVencimento by remember { mutableStateOf(existingConta?.dataVencimento ?: Date()) }

    var expandedCategoriaMenu by remember { mutableStateOf(false) }
    var expandedRecorrenciaMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingConta == null) "Nova Conta" else "Editar Conta") },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Nome
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome da Conta") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                // Descrição
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                )

                // Valor
                OutlinedTextField(
                    value = valorStr,
                    onValueChange = { valorStr = it },
                    label = { Text("Valor (R$)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )

                // Categoria
                ExposedDropdownMenuBox(
                    expanded = expandedCategoriaMenu,
                    onExpandedChange = { expandedCategoriaMenu = !expandedCategoriaMenu },
                ) {
                    OutlinedTextField(
                        value = getCategoriaLabel(categoria),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedCategoriaMenu,
                            )
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategoriaMenu,
                        onDismissRequest = { expandedCategoriaMenu = false },
                    ) {
                        ContaCategoria.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(getCategoriaLabel(cat)) },
                                onClick = {
                                    categoria = cat
                                    expandedCategoriaMenu = false
                                },
                            )
                        }
                    }
                }

                // Recorrência
                ExposedDropdownMenuBox(
                    expanded = expandedRecorrenciaMenu,
                    onExpandedChange = { expandedRecorrenciaMenu = !expandedRecorrenciaMenu },
                ) {
                    OutlinedTextField(
                        value = getRecorrenciaLabel(recorrencia),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Recorrência") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedRecorrenciaMenu,
                            )
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRecorrenciaMenu,
                        onDismissRequest = { expandedRecorrenciaMenu = false },
                    ) {
                        ContaRecorrencia.entries.forEach { rec ->
                            DropdownMenuItem(
                                text = { Text(getRecorrenciaLabel(rec)) },
                                onClick = {
                                    recorrencia = rec
                                    expandedRecorrenciaMenu = false
                                },
                            )
                        }
                    }
                }

                // Data de vencimento
                OutlinedTextField(
                    value = dateFormatter.format(dataVencimento),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data de Vencimento") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        TextButton(onClick = { showDatePicker = true }) {
                            Text("Alterar")
                        }
                    },
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nome.isNotBlank() && valorStr.isNotBlank()) {
                        val conta =
                            Conta(
                                id = existingConta?.id ?: UUID.randomUUID().toString(),
                                nome = nome,
                                descricao = descricao,
                                valor = valorStr.toDoubleOrNull() ?: 0.0,
                                dataVencimento = dataVencimento,
                                categoria = categoria,
                                recorrencia = recorrencia,
                                status = existingConta?.status ?: ContaStatus.PENDENTE,
                                userId = existingConta?.userId ?: "",
                                createdAt = existingConta?.createdAt ?: Date(),
                                dataPagamento = existingConta?.dataPagamento,
                            )
                        onSave(conta)
                    }
                },
                enabled = nome.isNotBlank() && valorStr.isNotBlank(),
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

    // Date picker
    if (showDatePicker) {
        SimpleDatePickerDialog(
            currentDate = dataVencimento,
            onDateSelected = { date ->
                dataVencimento = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
}

private fun getCategoriaLabel(categoria: ContaCategoria): String =
    when (categoria) {
        ContaCategoria.MORADIA -> "Moradia"
        ContaCategoria.ALIMENTACAO -> "Alimentação"
        ContaCategoria.TRANSPORTE -> "Transporte"
        ContaCategoria.SAUDE -> "Saúde"
        ContaCategoria.EDUCACAO -> "Educação"
        ContaCategoria.LAZER -> "Lazer"
        ContaCategoria.OUTROS -> "Outros"
    }

private fun getRecorrenciaLabel(recorrencia: ContaRecorrencia): String =
    when (recorrencia) {
        ContaRecorrencia.NENHUMA -> "Nenhuma"
        ContaRecorrencia.MENSAL -> "Mensal"
        ContaRecorrencia.ANUAL -> "Anual"
    }

/**
 * Dialog simplificado para seleção de data
 */
@Composable
private fun SimpleDatePickerDialog(
    currentDate: Date,
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit,
) {
    val calendar = Calendar.getInstance().apply { time = currentDate }
    var day by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH).toString()) }
    var month by remember { mutableStateOf((calendar.get(Calendar.MONTH) + 1).toString()) }
    var year by remember { mutableStateOf(calendar.get(Calendar.YEAR).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecionar Data") },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = day,
                    onValueChange = { if (it.length <= 2) day = it },
                    label = { Text("Dia") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    value = month,
                    onValueChange = { if (it.length <= 2) month = it },
                    label = { Text("Mês") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { if (it.length <= 4) year = it },
                    label = { Text("Ano") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val newCalendar =
                            Calendar.getInstance().apply {
                                set(Calendar.YEAR, year.toInt())
                                set(Calendar.MONTH, month.toInt() - 1)
                                set(Calendar.DAY_OF_MONTH, day.toInt())
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                        onDateSelected(newCalendar.time)
                    } catch (_: Exception) {
                        // Ignorar erro de parsing
                    }
                },
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}
