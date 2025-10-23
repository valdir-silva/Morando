package com.alunando.morando.domain.usecase

import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import com.alunando.morando.domain.repository.TasksRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case para buscar tarefas semanais
 */
class GetWeeklyTasksUseCase(
    private val repository: TasksRepository,
) {
    operator fun invoke(): Flow<List<Task>> = repository.getTasksByType(TaskType.SEMANAL)
}
