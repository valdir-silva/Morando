package com.alunando.morando.feature.tasks.presentation

/**
 * Efeitos (eventos únicos) da tela de tarefas
 */
sealed interface TasksEffect {
    data class ShowToast(val message: String) : TasksEffect
    data class ShowError(val message: String) : TasksEffect
}

