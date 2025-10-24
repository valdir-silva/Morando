package com.alunando.morando.feature.tasks.presentation

import com.alunando.morando.domain.model.Task
import java.util.Date

/**
 * Estado da tela de tarefas
 */
data class TasksState(
    val tasks: List<Task> = emptyList(),
    val subTasksMap: Map<String, List<Task>> = emptyMap(),
    val selectedDate: Date = Date(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val editingTask: Task? = null,
)
