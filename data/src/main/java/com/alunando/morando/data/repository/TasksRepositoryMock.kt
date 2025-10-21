package com.alunando.morando.data.repository

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import com.alunando.morando.domain.repository.TasksRepository
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implementação mock do TasksRepository para desenvolvimento sem Firebase
 */
class TasksRepositoryMock : TasksRepository {
    private val mockTasks =
        listOf(
            Task(
                id = UUID.randomUUID().toString(),
                titulo = "Lavar louça",
                descricao = "Lavar toda a louça do almoço",
                tipo = TaskType.DIARIA,
                completa = false,
                userId = "mock-user",
                createdAt = Date()
            ),
            Task(
                id = UUID.randomUUID().toString(),
                titulo = "Fazer café",
                descricao = "Preparar café da manhã",
                tipo = TaskType.DIARIA,
                completa = true,
                userId = "mock-user",
                createdAt = Date()
            ),
            Task(
                id = UUID.randomUUID().toString(),
                titulo = "Limpar banheiro",
                descricao = "Fazer limpeza completa do banheiro",
                tipo = TaskType.SEMANAL,
                completa = false,
                userId = "mock-user",
                createdAt = Date()
            ),
            Task(
                id = UUID.randomUUID().toString(),
                titulo = "Trocar roupa de cama",
                descricao = "Trocar lençóis e fronhas",
                tipo = TaskType.SEMANAL,
                completa = false,
                userId = "mock-user",
                createdAt = Date()
            )
        )

    override fun getTasks(): Flow<List<Task>> = flowOf(mockTasks)

    override fun getTasksByType(type: TaskType): Flow<List<Task>> = flowOf(mockTasks.filter { it.tipo == type })

    override suspend fun getTaskById(taskId: String): Result<Task> {
        val task = mockTasks.find { it.id == taskId }
        return if (task != null) {
            Result.Success(task)
        } else {
            Result.Error(Exception("Tarefa não encontrada"))
        }
    }

    override suspend fun addTask(task: Task): Result<Task> =
        Result.Success(task.copy(id = UUID.randomUUID().toString()))

    override suspend fun updateTask(task: Task): Result<Unit> = Result.Success(Unit)

    override suspend fun deleteTask(taskId: String): Result<Unit> = Result.Success(Unit)

    override suspend fun markTaskComplete(
        taskId: String,
        complete: Boolean
    ): Result<Unit> = Result.Success(Unit)
}
