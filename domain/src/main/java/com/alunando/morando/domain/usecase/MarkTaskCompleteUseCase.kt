package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.repository.TasksRepository

/**
 * Use case para marcar tarefa como completa/incompleta
 */
class MarkTaskCompleteUseCase(
    private val repository: TasksRepository,
) {
    suspend operator fun invoke(
        taskId: String,
        complete: Boolean,
    ): Result<Unit> = repository.markTaskComplete(taskId, complete)
}
