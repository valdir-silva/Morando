package com.alunando.morando.feature.tasks.presentation

/**
 * Intenções (ações) da tela de tarefas
 */
sealed interface TasksIntent {
    data object LoadTasks : TasksIntent
    data class ToggleTaskComplete(val taskId: String, val complete: Boolean) : TasksIntent
    data class SelectTaskType(val type: com.alunando.morando.domain.model.TaskType) : TasksIntent
}

