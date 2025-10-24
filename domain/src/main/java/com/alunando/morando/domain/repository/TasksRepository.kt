package com.alunando.morando.domain.repository

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Interface do repositório de tarefas
 */
interface TasksRepository {
    /**
     * Busca todas as tarefas do usuário
     */
    fun getTasks(): Flow<List<Task>>

    /**
     * Busca tarefas por tipo
     */
    fun getTasksByType(type: TaskType): Flow<List<Task>>

    /**
     * Busca tarefas para uma data específica, incluindo recorrentes
     */
    fun getTasksForDate(date: Date): Flow<List<Task>>

    /**
     * Busca tarefas para um período de datas, incluindo recorrentes
     */
    fun getTasksForDateRange(
        startDate: Date,
        endDate: Date,
    ): Flow<List<Task>>

    /**
     * Busca sub-tarefas de um compromisso
     */
    fun getSubTasks(parentTaskId: String): Flow<List<Task>>

    /**
     * Busca tarefa por ID
     */
    suspend fun getTaskById(taskId: String): Result<Task>

    /**
     * Adiciona nova tarefa
     */
    suspend fun addTask(task: Task): Result<Task>

    /**
     * Atualiza tarefa existente
     */
    suspend fun updateTask(task: Task): Result<Unit>

    /**
     * Deleta tarefa
     */
    suspend fun deleteTask(taskId: String): Result<Unit>

    /**
     * Marca tarefa como completa/incompleta
     */
    suspend fun markTaskComplete(
        taskId: String,
        complete: Boolean,
    ): Result<Unit>
}
