package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.repository.TasksRepository

/**
 * Use case para adicionar nova tarefa
 */
class AddTaskUseCase(
    private val repository: TasksRepository
) {
    suspend operator fun invoke(task: Task): Result<Task> {
        return repository.addTask(task)
    }
}

