package com.alunando.morando.feature.tasks.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alunando.morando.domain.model.RecurrenceType
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Dialog para criar/editar tarefas ou compromissos
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormDialog(
    selectedDate: Date,
    onDismiss: () -> Unit,
    onSaveTask: (Task) -> Unit,
    onSaveCommitment: (Task, List<Task>) -> Unit,
    onUpdateTask: (Task) -> Unit = {},
    onUpdateCommitment: (Task, List<Task>) -> Unit = { _, _ -> },
    existingTask: Task? = null,
    existingSubTasks: List<Task> = emptyList(),
    modifier: Modifier = Modifier,
) {
    var isCommitment by remember {
        mutableStateOf(existingTask?.tipo == TaskType.COMMITMENT)
    }
    var titulo by remember { mutableStateOf(existingTask?.titulo ?: "") }
    var descricao by remember { mutableStateOf(existingTask?.descricao ?: "") }
    var recurrence by remember {
        mutableStateOf(existingTask?.recurrence ?: RecurrenceType.NONE)
    }
    var taskDate by remember { mutableStateOf(existingTask?.scheduledDate ?: selectedDate) }
    var expandedRecurrenceMenu by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    // Sub-tarefas (apenas para compromissos)
    val subTasks =
        remember(existingTask) {
            mutableStateListOf<SubTaskData>().apply {
                // Carrega sub-tarefas existentes se estiver editando um compromisso
                if (existingTask?.tipo == TaskType.COMMITMENT) {
                    addAll(
                        existingSubTasks.map { task ->
                            SubTaskData(
                                id = task.id,
                                titulo = task.titulo,
                                descricao = task.descricao,
                            )
                        },
                    )
                }
            }
        }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (existingTask == null) {
                    if (isCommitment) "Novo Compromisso" else "Nova Tarefa"
                } else {
                    if (isCommitment) "Editar Compromisso" else "Editar Tarefa"
                },
            )
        },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Seletor de tipo (Tarefa / Compromisso)
                if (existingTask == null) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        SegmentedButton(
                            selected = !isCommitment,
                            onClick = { isCommitment = false },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        ) {
                            Text("Tarefa")
                        }
                        SegmentedButton(
                            selected = isCommitment,
                            onClick = { isCommitment = true },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        ) {
                            Text("Compromisso")
                        }
                    }
                }

                // Título
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text(if (isCommitment) "Nome do Compromisso" else "Nome da Tarefa") },
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
                    maxLines = 4,
                )

                // Data
                OutlinedTextField(
                    value = dateFormatter.format(taskDate),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(if (isCommitment) "Data do Compromisso" else "Data da Tarefa") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        TextButton(onClick = { showDatePicker = true }) {
                            Text("Alterar")
                        }
                    },
                )

                // Recorrência
                ExposedDropdownMenuBox(
                    expanded = expandedRecurrenceMenu,
                    onExpandedChange = { expandedRecurrenceMenu = !expandedRecurrenceMenu },
                ) {
                    OutlinedTextField(
                        value = getRecurrenceLabel(recurrence),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Recorrência") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedRecurrenceMenu,
                            )
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRecurrenceMenu,
                        onDismissRequest = { expandedRecurrenceMenu = false },
                    ) {
                        RecurrenceType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(getRecurrenceLabel(type)) },
                                onClick = {
                                    recurrence = type
                                    expandedRecurrenceMenu = false
                                },
                            )
                        }
                    }
                }

                // Sub-tarefas (apenas para compromissos)
                if (isCommitment) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tarefas do Compromisso",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Adicione as tarefas que devem ser concluídas até a data do compromisso",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    // Lista de sub-tarefas
                    subTasks.forEachIndexed { index, subTask ->
                        SubTaskInput(
                            titulo = subTask.titulo,
                            descricao = subTask.descricao,
                            onTituloChange = { subTasks[index] = subTask.copy(titulo = it) },
                            onDescricaoChange = { subTasks[index] = subTask.copy(descricao = it) },
                            onDelete = { subTasks.removeAt(index) },
                        )
                    }

                    // Botão adicionar sub-tarefa
                    OutlinedButton(
                        onClick = { subTasks.add(SubTaskData(id = "", titulo = "", descricao = "")) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Adicionar Tarefa")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank()) {
                        if (isCommitment) {
                            val commitment =
                                Task(
                                    id = existingTask?.id ?: UUID.randomUUID().toString(),
                                    titulo = titulo,
                                    descricao = descricao,
                                    tipo = TaskType.COMMITMENT,
                                    recurrence = recurrence,
                                    completa = existingTask?.completa ?: false,
                                    userId = existingTask?.userId ?: "",
                                    createdAt = existingTask?.createdAt ?: Date(),
                                    parentTaskId = null,
                                    scheduledDate = taskDate,
                                )
                            val subTasksList =
                                subTasks
                                    .filter { it.titulo.isNotBlank() }
                                    .map { subTaskData ->
                                        Task(
                                            id = subTaskData.id.ifEmpty { UUID.randomUUID().toString() },
                                            titulo = subTaskData.titulo,
                                            descricao = subTaskData.descricao,
                                            tipo = TaskType.NORMAL,
                                            recurrence = RecurrenceType.NONE,
                                            completa = false,
                                            userId = "",
                                            createdAt = Date(),
                                            parentTaskId = null,
                                            scheduledDate = null,
                                        )
                                    }
                            // Chama update ou save dependendo se está editando
                            if (existingTask != null) {
                                onUpdateCommitment(commitment, subTasksList)
                            } else {
                                onSaveCommitment(commitment, subTasksList)
                            }
                        } else {
                            val task =
                                Task(
                                    id = existingTask?.id ?: UUID.randomUUID().toString(),
                                    titulo = titulo,
                                    descricao = descricao,
                                    tipo = TaskType.NORMAL,
                                    recurrence = recurrence,
                                    completa = existingTask?.completa ?: false,
                                    userId = existingTask?.userId ?: "",
                                    createdAt = existingTask?.createdAt ?: Date(),
                                    parentTaskId = null,
                                    scheduledDate = taskDate,
                                )
                            // Chama update ou save dependendo se está editando
                            if (existingTask != null) {
                                onUpdateTask(task)
                            } else {
                                onSaveTask(task)
                            }
                        }
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

    // Date picker
    if (showDatePicker) {
        SimpleDatePickerDialog(
            currentDate = taskDate,
            onDateSelected = { date ->
                taskDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
}

@Composable
private fun SubTaskInput(
    titulo: String,
    descricao: String,
    onTituloChange: (String) -> Unit,
    onDescricaoChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Tarefa",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remover tarefa",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
            OutlinedTextField(
                value = titulo,
                onValueChange = onTituloChange,
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = descricao,
                onValueChange = onDescricaoChange,
                label = { Text("Descrição (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
            )
        }
    }
}

private fun getRecurrenceLabel(recurrence: RecurrenceType): String =
    when (recurrence) {
        RecurrenceType.NONE -> "Nenhuma"
        RecurrenceType.DAILY -> "Diária"
        RecurrenceType.WEEKLY -> "Semanal"
        RecurrenceType.MONTHLY -> "Mensal"
    }

/**
 * Data class para gerenciar sub-tarefas no formulário
 */
private data class SubTaskData(
    val id: String = "",
    val titulo: String,
    val descricao: String,
)

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
                )
                OutlinedTextField(
                    value = month,
                    onValueChange = { if (it.length <= 2) month = it },
                    label = { Text("Mês") },
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { if (it.length <= 4) year = it },
                    label = { Text("Ano") },
                    modifier = Modifier.weight(1f),
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
