package com.alunando.morando.data.repository

import com.alunando.morando.core.Result
import com.alunando.morando.data.datasource.TasksRemoteDataSource
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import com.alunando.morando.domain.repository.TasksRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Implementação do repositório de tarefas
 */
class TasksRepositoryImpl(
    private val remoteDataSource: TasksRemoteDataSource,
) : TasksRepository {
    override fun getTasks(): Flow<List<Task>> = remoteDataSource.getTasks()

    override fun getTasksByType(type: TaskType): Flow<List<Task>> = remoteDataSource.getTasksByType(type)

    override fun getTasksForDate(date: Date): Flow<List<Task>> = remoteDataSource.getTasksForDate(date)

    override fun getTasksForDateRange(
        startDate: Date,
        endDate: Date,
    ): Flow<List<Task>> = remoteDataSource.getTasksForDateRange(startDate, endDate)

    override fun getSubTasks(parentTaskId: String): Flow<List<Task>> = remoteDataSource.getSubTasks(parentTaskId)

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getTaskById(taskId: String): Result<Task> =
        try {
            val task = remoteDataSource.getTaskById(taskId)
            if (task != null) {
                Result.Success(task)
            } else {
                Result.Error(Exception("Tarefa não encontrada"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun addTask(task: Task): Result<Task> =
        try {
            val newTask = remoteDataSource.addTask(task)
            Result.Success(newTask)
        } catch (e: Exception) {
            Result.Error(e)
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun updateTask(task: Task): Result<Unit> =
        try {
            remoteDataSource.updateTask(task)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun deleteTask(taskId: String): Result<Unit> =
        try {
            remoteDataSource.deleteTask(taskId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun markTaskComplete(
        taskId: String,
        complete: Boolean,
    ): Result<Unit> =
        try {
            remoteDataSource.markTaskComplete(taskId, complete)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
