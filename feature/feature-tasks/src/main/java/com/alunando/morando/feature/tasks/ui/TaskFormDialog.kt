package com.alunando.morando.feature.tasks.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Dialog para criar/editar tarefas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormDialog(
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    existingTask: Task? = null,
    commitments: List<Task> = emptyList(),
    modifier: Modifier = Modifier,
) {
    var titulo by remember { mutableStateOf(existingTask?.titulo ?: "") }
    var descricao by remember { mutableStateOf(existingTask?.descricao ?: "") }
    var tipo by remember { mutableStateOf(existingTask?.tipo ?: TaskType.DIARIA) }
    var selectedDate by remember { mutableStateOf(existingTask?.scheduledDate ?: Date()) }
    var selectedParentId by remember { mutableStateOf(existingTask?.parentTaskId) }
    var expandedTypeMenu by remember { mutableStateOf(false) }
    var expandedParentMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingTask == null) "Nova Tarefa" else "Editar Tarefa") },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Título
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                // Descrição
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                )

                // Tipo de tarefa
                ExposedDropdownMenuBox(
                    expanded = expandedTypeMenu,
                    onExpandedChange = { expandedTypeMenu = !expandedTypeMenu },
                ) {
                    OutlinedTextField(
                        value = tipo.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeMenu) },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTypeMenu,
                        onDismissRequest = { expandedTypeMenu = false },
                    ) {
                        TaskType.entries.forEach { taskType ->
                            DropdownMenuItem(
                                text = { Text(taskType.name) },
                                onClick = {
                                    tipo = taskType
                                    expandedTypeMenu = false
                                },
                            )
                        }
                    }
                }

                // Se for COMPROMISSO, mostrar seletor de data/hora
                if (tipo == TaskType.COMPROMISSO) {
                    OutlinedTextField(
                        value = dateFormatter.format(selectedDate),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Data e Hora") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            TextButton(onClick = { showDatePicker = true }) {
                                Text("Alterar")
                            }
                        },
                    )

                    // Seletor de compromisso pai (para sub-tarefas)
                    if (commitments.isNotEmpty()) {
                        ExposedDropdownMenuBox(
                            expanded = expandedParentMenu,
                            onExpandedChange = { expandedParentMenu = !expandedParentMenu },
                        ) {
                            OutlinedTextField(
                                value =
                                    selectedParentId?.let { parentId ->
                                        commitments.find { it.id == parentId }?.titulo ?: "Nenhum"
                                    } ?: "Nenhum",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Compromisso Pai (opcional)") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expandedParentMenu,
                                    )
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                            )
                            ExposedDropdownMenu(
                                expanded = expandedParentMenu,
                                onDismissRequest = { expandedParentMenu = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Nenhum") },
                                    onClick = {
                                        selectedParentId = null
                                        expandedParentMenu = false
                                    },
                                )
                                commitments.forEach { commitment ->
                                    DropdownMenuItem(
                                        text = { Text(commitment.titulo) },
                                        onClick = {
                                            selectedParentId = commitment.id
                                            expandedParentMenu = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank()) {
                        val task =
                            Task(
                                id = existingTask?.id ?: UUID.randomUUID().toString(),
                                titulo = titulo,
                                descricao = descricao,
                                tipo = tipo,
                                completa = existingTask?.completa ?: false,
                                userId = existingTask?.userId ?: "",
                                createdAt = existingTask?.createdAt ?: Date(),
                                parentTaskId = if (tipo == TaskType.COMPROMISSO) selectedParentId else null,
                                scheduledDate = if (tipo == TaskType.COMPROMISSO) selectedDate else null,
                            )
                        onSave(task)
                    }
                },
                enabled = titulo.isNotBlank(),
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

    // Date picker simplificado (usando um diálogo básico por hora)
    if (showDatePicker) {
        SimpleDatePickerDialog(
            currentDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
}

/**
 * Dialog simplificado para seleção de data/hora
 * TODO: Usar Material3 DatePicker quando disponível
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
    var hour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY).toString()) }
    var minute by remember { mutableStateOf(calendar.get(Calendar.MINUTE).toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecionar Data e Hora") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = { day = it },
                        label = { Text("Dia") },
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = month,
                        onValueChange = { month = it },
                        label = { Text("Mês") },
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Ano") },
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = hour,
                        onValueChange = { hour = it },
                        label = { Text("Hora") },
                        modifier = Modifier.weight(1f),
                    )
                    OutlinedTextField(
                        value = minute,
                        onValueChange = { minute = it },
                        label = { Text("Min") },
                        modifier = Modifier.weight(1f),
                    )
                }
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
                                set(Calendar.HOUR_OF_DAY, hour.toInt())
                                set(Calendar.MINUTE, minute.toInt())
                                set(Calendar.SECOND, 0)
                            }
                        onDateSelected(newCalendar.time)
                    } catch (e: Exception) {
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
