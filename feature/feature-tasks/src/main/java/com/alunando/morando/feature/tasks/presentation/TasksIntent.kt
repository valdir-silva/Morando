package com.alunando.morando.feature.tasks.presentation

import com.alunando.morando.domain.model.Task

/**
 * Intenções (ações) da tela de tarefas
 */
sealed interface TasksIntent {
    data object LoadTasks : TasksIntent

    data class ToggleTaskComplete(
        val taskId: String,
        val complete: Boolean,
    ) : TasksIntent

    data class SelectTaskType(
        val type: com.alunando.morando.domain.model.TaskType,
    ) : TasksIntent

    data class CreateTask(
        val task: Task,
    ) : TasksIntent

    data class DeleteTask(
        val taskId: String,
    ) : TasksIntent

    data class ExpandCommitment(
        val taskId: String,
    ) : TasksIntent

    data object ShowCreateDialog : TasksIntent

    data object HideCreateDialog : TasksIntent
}
