package com.alunando.morando.domain.usecase

import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.repository.TasksRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case para buscar sub-tarefas de um compromisso
 */
class GetSubTasksUseCase(
    private val repository: TasksRepository,
) {
    operator fun invoke(parentTaskId: String): Flow<List<Task>> = repository.getSubTasks(parentTaskId)
}

