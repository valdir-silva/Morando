package com.alunando.morando.feature.tasks.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import com.alunando.morando.feature.tasks.presentation.TasksIntent
import com.alunando.morando.feature.tasks.presentation.TasksViewModel

/**
 * Tela principal de tarefas
 */
@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    // Carrega as tarefas quando a tela é exibida
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.handleIntent(TasksIntent.LoadTasks)
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Tabs para tipo de tarefa
        TabRow(selectedTabIndex = if (state.selectedType == TaskType.DIARIA) 0 else 1) {
            Tab(
                selected = state.selectedType == TaskType.DIARIA,
                onClick = { viewModel.handleIntent(TasksIntent.SelectTaskType(TaskType.DIARIA)) },
                text = { Text("Diárias") }
            )
            Tab(
                selected = state.selectedType == TaskType.SEMANAL,
                onClick = { viewModel.handleIntent(TasksIntent.SelectTaskType(TaskType.SEMANAL)) },
                text = { Text("Semanais") }
            )
        }

        // Lista de tarefas
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.tasks) { task ->
                    TaskItem(
                        task = task,
                        onToggleComplete = { complete ->
                            viewModel.handleIntent(
                                TasksIntent.ToggleTaskComplete(task.id, complete)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.titulo,
                    style = MaterialTheme.typography.titleMedium
                )
                if (task.descricao.isNotEmpty()) {
                    Text(
                        text = task.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Checkbox(
                checked = task.completa,
                onCheckedChange = onToggleComplete
            )
        }
    }
}
