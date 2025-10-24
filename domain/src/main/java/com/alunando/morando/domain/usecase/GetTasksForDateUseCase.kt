package com.alunando.morando.domain.usecase

import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Use case para buscar tarefas de uma data específica
 * Inclui tarefas com scheduledDate correspondente e tarefas recorrentes
 */
class GetTasksForDateUseCase(
    private val repository: TasksRepository,
) {
    operator fun invoke(date: Date): Flow<List<Task>> = repository.getTasksForDate(date)
}

