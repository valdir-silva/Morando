package com.alunando.morando.feature.tasks.presentation

import com.alunando.morando.domain.model.Task
import java.util.Calendar
import java.util.Date

/**
 * Estado da tela de tarefas
 */
data class TasksState(
    val tasks: List<Task> = emptyList(),
    val tasksByDate: Map<Date, List<Task>> = emptyMap(),
    val subTasksMap: Map<String, List<Task>> = emptyMap(),
    val selectedDate: Date = Date(),
    val dateRange: Pair<Date, Date> = getDefaultDateRange(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val editingTask: Task? = null,
    val showDeleteDialog: Boolean = false,
    val taskToDelete: Task? = null,
)

/**
 * Calcula o período padrão: últimos 7 dias + próximos 60 dias
 */
private fun getDefaultDateRange(): Pair<Date, Date> {
    val today = Calendar.getInstance()
    val startDate =
        Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            add(Calendar.DAY_OF_MONTH, -7)
        }.time
    val endDate =
        Calendar.getInstance().apply {
            timeInMillis = today.timeInMillis
            add(Calendar.DAY_OF_MONTH, 60)
        }.time
    return startDate to endDate
}
