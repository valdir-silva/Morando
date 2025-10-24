package com.alunando.morando.feature.tasks.presentation

import com.alunando.morando.domain.model.Task
import java.util.Date

/**
 * Intenções (ações) da tela de tarefas
 */
sealed interface TasksIntent {
    data object LoadTasks : TasksIntent

    data class ToggleTaskComplete(
        val taskId: String,
        val complete: Boolean,
    ) : TasksIntent

    data class SelectDate(
        val date: Date,
    ) : TasksIntent

    data object NavigateToToday : TasksIntent

    data object NavigateToNextDay : TasksIntent

    data object NavigateToPrevDay : TasksIntent

    data class CreateTask(
        val task: Task,
    ) : TasksIntent

    data class CreateCommitment(
        val commitment: Task,
        val subTasks: List<Task>,
    ) : TasksIntent

    data class ShowDeleteConfirmation(
        val task: Task,
    ) : TasksIntent

    data object HideDeleteConfirmation : TasksIntent

    data object ConfirmDelete : TasksIntent

    data class DeleteTask(
        val taskId: String,
    ) : TasksIntent

    data class EditTask(
        val task: Task,
    ) : TasksIntent

    data class UpdateTask(
        val task: Task,
    ) : TasksIntent

    data class UpdateCommitment(
        val commitment: Task,
        val subTasks: List<Task>,
    ) : TasksIntent

    data object ShowCreateDialog : TasksIntent

    data object HideCreateDialog : TasksIntent
}
