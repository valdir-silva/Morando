package com.alunando.morando.feature.tasks.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.alunando.morando.domain.model.RecurrenceType
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import com.alunando.morando.feature.tasks.presentation.TasksIntent
import com.alunando.morando.feature.tasks.presentation.TasksViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Tela principal de tarefas - Timeline/Agenda
 */
@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    // Carrega as tarefas quando a tela é exibida
    LaunchedEffect(Unit) {
        viewModel.handleIntent(TasksIntent.LoadTasks)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(TasksIntent.ShowCreateDialog) },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Tarefa")
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Header com navegação de data
            DateNavigationHeader(
                selectedDate = state.selectedDate,
                onPrevDay = { viewModel.handleIntent(TasksIntent.NavigateToPrevDay) },
                onNextDay = { viewModel.handleIntent(TasksIntent.NavigateToNextDay) },
                onToday = { viewModel.handleIntent(TasksIntent.NavigateToToday) },
            )

            // Lista de tarefas
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Nenhuma tarefa para este dia",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.tasks) { task ->
                        if (task.tipo == TaskType.COMMITMENT) {
                            CommitmentCard(
                                commitment = task,
                                subTasks = state.subTasksMap[task.id] ?: emptyList(),
                                onToggleComplete = { taskId, complete ->
                                    viewModel.handleIntent(
                                        TasksIntent.ToggleTaskComplete(taskId, complete),
                                    )
                                },
                                onDelete = {
                                    viewModel.handleIntent(TasksIntent.DeleteTask(task.id))
                                },
                            )
                        } else {
                            TaskCard(
                                task = task,
                                onToggleComplete = { complete ->
                                    viewModel.handleIntent(
                                        TasksIntent.ToggleTaskComplete(task.id, complete),
                                    )
                                },
                                onDelete = {
                                    viewModel.handleIntent(TasksIntent.DeleteTask(task.id))
                                },
                            )
                        }
                    }

                    // Espaçamento no final para o FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Dialog de criação/edição
        if (state.showCreateDialog) {
            TaskFormDialog(
                selectedDate = state.selectedDate,
                onDismiss = { viewModel.handleIntent(TasksIntent.HideCreateDialog) },
                onSaveTask = { task ->
                    viewModel.handleIntent(TasksIntent.CreateTask(task))
                },
                onSaveCommitment = { commitment, subTasks ->
                    viewModel.handleIntent(TasksIntent.CreateCommitment(commitment, subTasks))
                },
                existingTask = state.editingTask,
            )
        }
    }
}

@Composable
fun DateNavigationHeader(
    selectedDate: Date,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("pt", "BR"))
    val isToday = isSameDay(selectedDate, Date())

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onPrevDay) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Dia anterior",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = dateFormatter.format(selectedDate).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                    if (!isToday) {
                        TextButton(onClick = onToday) {
                            Icon(
                                painter = painterResource(id = com.alunando.morando.feature.tasks.R.drawable.ic_today),
                                contentDescription = null,
                                modifier = Modifier.padding(end = 4.dp),
                            )
                            Text("Hoje")
                        }
                    }
                }

                IconButton(onClick = onNextDay) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Próximo dia",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onToggleComplete: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = task.completa,
                    onCheckedChange = onToggleComplete,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = task.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (task.completa) TextDecoration.LineThrough else null,
                    )
                    if (task.descricao.isNotEmpty()) {
                        Text(
                            text = task.descricao,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    if (task.recurrence != RecurrenceType.NONE) {
                        Text(
                            text = "🔄 ${getRecurrenceLabel(task.recurrence)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Excluir",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
fun CommitmentCard(
    commitment: Task,
    subTasks: List<Task>,
    onToggleComplete: (String, Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val completedCount = subTasks.count { it.completa }
    val totalCount = subTasks.size

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Header do compromisso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "📅",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Column {
                            Text(
                                text = commitment.titulo,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            if (commitment.descricao.isNotEmpty()) {
                                Text(
                                    text = commitment.descricao,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }
                        }
                    }
                    Text(
                        text = "$completedCount de $totalCount tarefas concluídas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(top = 4.dp, start = 32.dp),
                    )
                    if (commitment.recurrence != RecurrenceType.NONE) {
                        Text(
                            text = "🔄 ${getRecurrenceLabel(commitment.recurrence)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 32.dp),
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir compromisso",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }

            // Sub-tarefas (sempre visíveis)
            if (subTasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    subTasks.forEach { subTask ->
                        SubTaskItem(
                            task = subTask,
                            onToggleComplete = { complete ->
                                onToggleComplete(subTask.id, complete)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubTaskItem(
    task: Task,
    onToggleComplete: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = task.completa,
                onCheckedChange = onToggleComplete,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = task.titulo,
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (task.completa) TextDecoration.LineThrough else null,
                )
                if (task.descricao.isNotEmpty()) {
                    Text(
                        text = task.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

private fun getRecurrenceLabel(recurrence: RecurrenceType): String =
    when (recurrence) {
        RecurrenceType.NONE -> ""
        RecurrenceType.DAILY -> "Diária"
        RecurrenceType.WEEKLY -> "Semanal"
        RecurrenceType.MONTHLY -> "Mensal"
    }

private fun isSameDay(
    date1: Date,
    date2: Date,
): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
