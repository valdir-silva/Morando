package com.alunando.morando.domain.usecase

import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Use case para buscar tarefas de um período de datas
 * Inclui tarefas com scheduledDate no período e tarefas recorrentes
 */
class GetTasksForDateRangeUseCase(
    private val repository: TasksRepository,
) {
    operator fun invoke(
        startDate: Date,
        endDate: Date,
    ): Flow<List<Task>> = repository.getTasksForDateRange(startDate, endDate)
}
