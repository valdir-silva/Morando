package com.alunando.morando.feature.tasks.presentation

import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType

/**
 * Estado da tela de tarefas
 */
data class TasksState(
    val tasks: List<Task> = emptyList(),
    val selectedType: TaskType = TaskType.DIARIA,
    val isLoading: Boolean = false,
    val error: String? = null,
)
