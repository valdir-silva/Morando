package com.alunando.morando.feature.tasks.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import com.alunando.morando.feature.tasks.presentation.TasksIntent
import com.alunando.morando.feature.tasks.presentation.TasksViewModel
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Tela principal de tarefas
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
            // Tabs para tipo de tarefa
            TabRow(
                selectedTabIndex =
                    when (state.selectedType) {
                        TaskType.DIARIA -> 0
                        TaskType.SEMANAL -> 1
                        TaskType.COMPROMISSO -> 2
                    },
            ) {
                Tab(
                    selected = state.selectedType == TaskType.DIARIA,
                    onClick = { viewModel.handleIntent(TasksIntent.SelectTaskType(TaskType.DIARIA)) },
                    text = { Text("Diárias") },
                )
                Tab(
                    selected = state.selectedType == TaskType.SEMANAL,
                    onClick = { viewModel.handleIntent(TasksIntent.SelectTaskType(TaskType.SEMANAL)) },
                    text = { Text("Semanais") },
                )
                Tab(
                    selected = state.selectedType == TaskType.COMPROMISSO,
                    onClick = { viewModel.handleIntent(TasksIntent.SelectTaskType(TaskType.COMPROMISSO)) },
                    text = { Text("Compromissos") },
                )
            }

            // Lista de tarefas
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.tasks) { task ->
                        TaskItem(
                            task = task,
                            allTasks = state.tasks,
                            isExpanded = state.expandedCommitments.contains(task.id),
                            onToggleComplete = { taskId, complete ->
                                viewModel.handleIntent(
                                    TasksIntent.ToggleTaskComplete(taskId, complete),
                                )
                            },
                            onDelete = {
                                viewModel.handleIntent(TasksIntent.DeleteTask(task.id))
                            },
                            onExpandClick = {
                                viewModel.handleIntent(TasksIntent.ExpandCommitment(task.id))
                            },
                        )
                    }
                }
            }
        }

        // Dialog de criação/edição
        if (state.showCreateDialog) {
            val commitments = state.tasks.filter { it.tipo == TaskType.COMPROMISSO && it.parentTaskId == null }
            TaskFormDialog(
                onDismiss = { viewModel.handleIntent(TasksIntent.HideCreateDialog) },
                onSave = { task ->
                    viewModel.handleIntent(TasksIntent.CreateTask(task))
                },
                existingTask = state.editingTask,
                commitments = commitments,
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    allTasks: List<Task>,
    isExpanded: Boolean,
    onToggleComplete: (String, Boolean) -> Unit,
    onDelete: () -> Unit,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val subTasks = allTasks.filter { it.parentTaskId == task.id }
    val completedSubTasks = subTasks.count { it.completa }
    val isCommitment = task.tipo == TaskType.COMPROMISSO && task.parentTaskId == null

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Tarefa principal
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isCommitment) { if (isCommitment) onExpandClick() }
                        .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = task.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            textDecoration = if (task.completa) TextDecoration.LineThrough else null,
                        )
                        // Badge de progresso para compromissos
                        if (isCommitment && subTasks.isNotEmpty()) {
                            Badge {
                                Text("$completedSubTasks/${subTasks.size}")
                            }
                        }
                    }
                    if (task.descricao.isNotEmpty()) {
                        Text(
                            text = task.descricao,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    // Mostrar data para compromissos
                    task.scheduledDate?.let { date ->
                        if (task.tipo == TaskType.COMPROMISSO) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "📅 ${dateFormatter.format(date)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = task.completa,
                        onCheckedChange = { complete ->
                            onToggleComplete(task.id, complete)
                        },
                    )
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                    // Ícone de expandir para compromissos
                    if (isCommitment) {
                        Icon(
                            imageVector =
                                if (isExpanded) {
                                    Icons.Default.KeyboardArrowUp
                                } else {
                                    Icons.Default.KeyboardArrowDown
                                },
                            contentDescription = if (isExpanded) "Recolher" else "Expandir",
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }

            // Sub-tarefas (quando expandido)
            AnimatedVisibility(
                visible = isExpanded && isCommitment,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 32.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (subTasks.isEmpty()) {
                        Text(
                            text = "Nenhuma sub-tarefa",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
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
}

@Composable
private fun SubTaskItem(
    task: Task,
    onToggleComplete: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
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
        Spacer(modifier = Modifier.width(8.dp))
        Checkbox(
            checked = task.completa,
            onCheckedChange = onToggleComplete,
        )
    }
}
