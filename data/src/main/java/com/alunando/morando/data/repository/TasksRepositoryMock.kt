package com.alunando.morando.data.repository

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import com.alunando.morando.domain.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import java.util.UUID

/**
 * Implementação mock do TasksRepository para desenvolvimento sem Firebase
 */
class TasksRepositoryMock : TasksRepository {
    private val tasks =
        MutableStateFlow(
            listOf(
                Task(
                    id = "task-1",
                    titulo = "Lavar louça",
                    descricao = "Lavar toda a louça do almoço",
                    tipo = TaskType.DIARIA,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                ),
                Task(
                    id = "task-2",
                    titulo = "Fazer café",
                    descricao = "Preparar café da manhã",
                    tipo = TaskType.DIARIA,
                    completa = true,
                    userId = "mock-user",
                    createdAt = Date(),
                ),
                Task(
                    id = "task-3",
                    titulo = "Limpar banheiro",
                    descricao = "Fazer limpeza completa do banheiro",
                    tipo = TaskType.SEMANAL,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                ),
                Task(
                    id = "task-4",
                    titulo = "Trocar roupa de cama",
                    descricao = "Trocar lençóis e fronhas",
                    tipo = TaskType.SEMANAL,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                ),
                // Exemplo de compromisso
                Task(
                    id = "commitment-1",
                    titulo = "Jantar com namorada",
                    descricao = "Jantar romântico no restaurante italiano",
                    tipo = TaskType.COMPROMISSO,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    scheduledDate =
                        Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_MONTH, 3)
                            set(Calendar.HOUR_OF_DAY, 20)
                            set(Calendar.MINUTE, 0)
                        }.time,
                ),
                // Sub-tarefas do compromisso
                Task(
                    id = "subtask-1",
                    titulo = "Lavar as roupas",
                    descricao = "Lavar a roupa que vou usar",
                    tipo = TaskType.COMPROMISSO,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    parentTaskId = "commitment-1",
                ),
                Task(
                    id = "subtask-2",
                    titulo = "Fazer reserva",
                    descricao = "Ligar e fazer reserva no restaurante",
                    tipo = TaskType.COMPROMISSO,
                    completa = true,
                    userId = "mock-user",
                    createdAt = Date(),
                    parentTaskId = "commitment-1",
                ),
                Task(
                    id = "subtask-3",
                    titulo = "Separar roupa",
                    descricao = "Escolher e separar roupa para usar",
                    tipo = TaskType.COMPROMISSO,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    parentTaskId = "commitment-1",
                ),
            ),
        )

    override fun getTasks(): Flow<List<Task>> = tasks

    override fun getTasksByType(type: TaskType): Flow<List<Task>> =
        tasks.map { taskList ->
            when (type) {
                TaskType.COMPROMISSO -> taskList.filter { it.tipo == TaskType.COMPROMISSO }
                else -> taskList.filter { it.tipo == type && it.parentTaskId == null }
            }
        }

    override suspend fun getTaskById(taskId: String): Result<Task> {
        val task = tasks.value.find { it.id == taskId }
        return if (task != null) {
            Result.Success(task)
        } else {
            Result.Error(Exception("Tarefa não encontrada"))
        }
    }

    override suspend fun addTask(task: Task): Result<Task> {
        val newTask = task.copy(id = if (task.id.isEmpty()) UUID.randomUUID().toString() else task.id)
        tasks.value = tasks.value + newTask
        return Result.Success(newTask)
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        tasks.value = tasks.value.map { if (it.id == task.id) task else it }
        return Result.Success(Unit)
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        // Deletar a tarefa e suas sub-tarefas
        tasks.value =
            tasks.value.filter {
                it.id != taskId && it.parentTaskId != taskId
            }
        return Result.Success(Unit)
    }

    override suspend fun markTaskComplete(
        taskId: String,
        complete: Boolean,
    ): Result<Unit> {
        tasks.value =
            tasks.value.map {
                if (it.id == taskId) {
                    it.copy(completa = complete)
                } else {
                    it
                }
            }
        return Result.Success(Unit)
    }
}
