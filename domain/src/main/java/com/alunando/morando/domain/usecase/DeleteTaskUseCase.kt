package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.repository.TasksRepository

/**
 * Use case para deletar uma tarefa
 */
class DeleteTaskUseCase(
    private val repository: TasksRepository,
) {
    suspend operator fun invoke(taskId: String): Result<Unit> = repository.deleteTask(taskId)
}
