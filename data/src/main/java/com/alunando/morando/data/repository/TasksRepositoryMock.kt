package com.alunando.morando.data.repository

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.RecurrenceType
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
                // Tarefa normal para hoje
                Task(
                    id = "task-1",
                    titulo = "Lavar louça",
                    descricao = "Lavar toda a louça do almoço",
                    tipo = TaskType.NORMAL,
                    recurrence = RecurrenceType.NONE,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    scheduledDate = Date(),
                ),
                // Tarefa normal concluída para hoje
                Task(
                    id = "task-2",
                    titulo = "Fazer café",
                    descricao = "Preparar café da manhã",
                    tipo = TaskType.NORMAL,
                    recurrence = RecurrenceType.NONE,
                    completa = true,
                    userId = "mock-user",
                    createdAt = Date(),
                    scheduledDate = Date(),
                ),
                // Tarefa com recorrência diária
                Task(
                    id = "task-3",
                    titulo = "Tomar vitaminas",
                    descricao = "Tomar suplementos vitam únicos",
                    tipo = TaskType.NORMAL,
                    recurrence = RecurrenceType.DAILY,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    scheduledDate = Date(),
                ),
                // Tarefa com recorrência semanal
                Task(
                    id = "task-4",
                    titulo = "Limpar banheiro",
                    descricao = "Fazer limpeza completa do banheiro",
                    tipo = TaskType.NORMAL,
                    recurrence = RecurrenceType.WEEKLY,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    scheduledDate =
                        Calendar.getInstance().apply {
                            set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                        }.time,
                ),
                // Tarefa com recorrência mensal
                Task(
                    id = "task-5",
                    titulo = "Pagar contas",
                    descricao = "Pagar todas as contas do mês",
                    tipo = TaskType.NORMAL,
                    recurrence = RecurrenceType.MONTHLY,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    scheduledDate =
                        Calendar.getInstance().apply {
                            set(Calendar.DAY_OF_MONTH, 5)
                        }.time,
                ),
                // Compromisso para daqui 3 dias
                Task(
                    id = "commitment-1",
                    titulo = "Jantar romântico",
                    descricao = "Jantar com a namorada no restaurante italiano",
                    tipo = TaskType.COMMITMENT,
                    recurrence = RecurrenceType.NONE,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    scheduledDate =
                        Calendar.getInstance().apply {
                            add(Calendar.DAY_OF_MONTH, 3)
                        }.time,
                ),
                // Sub-tarefas do compromisso
                Task(
                    id = "subtask-1-1",
                    titulo = "Fazer reserva no restaurante",
                    descricao = "Ligar e fazer reserva para 2 pessoas às 20h",
                    tipo = TaskType.NORMAL,
                    recurrence = RecurrenceType.NONE,
                    completa = true,
                    userId = "mock-user",
                    createdAt = Date(),
                    parentTaskId = "commitment-1",
                ),
                Task(
                    id = "subtask-1-2",
                    titulo = "Lavar roupa social",
                    descricao = "Lavar e passar a roupa que vou usar",
                    tipo = TaskType.NORMAL,
                    recurrence = RecurrenceType.NONE,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    parentTaskId = "commitment-1",
                ),
                Task(
                    id = "subtask-1-3",
                    titulo = "Comprar presente",
                    descricao = "Comprar um pequeno presente para ela",
                    tipo = TaskType.NORMAL,
                    recurrence = RecurrenceType.NONE,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    parentTaskId = "commitment-1",
                ),
                // Compromisso recorrente mensal
                Task(
                    id = "commitment-2",
                    titulo = "Reunião mensal da família",
                    descricao = "Almoço de família no primeiro domingo do mês",
                    tipo = TaskType.COMMITMENT,
                    recurrence = RecurrenceType.MONTHLY,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    scheduledDate =
                        Calendar.getInstance().apply {
                            set(Calendar.DAY_OF_MONTH, 1)
                            while (get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                                add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }.time,
                ),
                // Sub-tarefas do compromisso recorrente
                Task(
                    id = "subtask-2-1",
                    titulo = "Avisar a família",
                    descricao = "Mandar mensagem confirmando presença",
                    tipo = TaskType.NORMAL,
                    recurrence = RecurrenceType.NONE,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    parentTaskId = "commitment-2",
                ),
                Task(
                    id = "subtask-2-2",
                    titulo = "Levar sobremesa",
                    descricao = "Comprar ou fazer uma sobremesa",
                    tipo = TaskType.NORMAL,
                    recurrence = RecurrenceType.NONE,
                    completa = false,
                    userId = "mock-user",
                    createdAt = Date(),
                    parentTaskId = "commitment-2",
                ),
            ),
        )

    override fun getTasks(): Flow<List<Task>> = tasks

    override fun getTasksByType(type: TaskType): Flow<List<Task>> =
        tasks.map { taskList ->
            taskList.filter { it.tipo == type && it.parentTaskId == null }
        }

    override fun getTasksForDate(date: Date): Flow<List<Task>> =
        tasks.map { taskList ->
            taskList
                .filter { it.parentTaskId == null }
                .filter { task ->
                    shouldShowTaskOnDate(task, date)
                }
        }

    override fun getTasksForDateRange(
        startDate: Date,
        endDate: Date,
    ): Flow<List<Task>> =
        tasks.map { taskList ->
            taskList
                .filter { it.parentTaskId == null }
                .filter { task ->
                    shouldShowTaskInDateRange(task, startDate, endDate)
                }
        }

    override fun getSubTasks(parentTaskId: String): Flow<List<Task>> =
        tasks.map { taskList ->
            taskList.filter { it.parentTaskId == parentTaskId }
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

    /**
     * Verifica se uma tarefa deve ser exibida em uma data específica
     */
    private fun shouldShowTaskOnDate(
        task: Task,
        date: Date,
    ): Boolean {
        val taskDate = task.scheduledDate ?: return false
        val targetCal = Calendar.getInstance().apply { time = date }
        val taskCal = Calendar.getInstance().apply { time = taskDate }

        return when (task.recurrence) {
            RecurrenceType.NONE -> {
                // Tarefa sem recorrência: mostra apenas na data agendada
                isSameDay(taskCal, targetCal)
            }
            RecurrenceType.DAILY -> {
                // Tarefa diária: mostra se a data alvo >= data inicial
                !targetCal.before(taskCal)
            }
            RecurrenceType.WEEKLY -> {
                // Tarefa semanal: mostra se é o mesmo dia da semana e data alvo >= data inicial
                !targetCal.before(taskCal) &&
                    taskCal.get(Calendar.DAY_OF_WEEK) == targetCal.get(Calendar.DAY_OF_WEEK)
            }
            RecurrenceType.MONTHLY -> {
                // Tarefa mensal: mostra se é o mesmo dia do mês e data alvo >= data inicial
                !targetCal.before(taskCal) &&
                    taskCal.get(Calendar.DAY_OF_MONTH) == targetCal.get(Calendar.DAY_OF_MONTH)
            }
        }
    }

    private fun isSameDay(
        cal1: Calendar,
        cal2: Calendar,
    ): Boolean =
        cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)

    /**
     * Verifica se uma tarefa deve aparecer em algum dia do período
     */
    private fun shouldShowTaskInDateRange(
        task: Task,
        startDate: Date,
        endDate: Date,
    ): Boolean {
        val taskDate = task.scheduledDate ?: return false
        val startCal = Calendar.getInstance().apply { time = startDate }
        val endCal = Calendar.getInstance().apply { time = endDate }
        val taskCal = Calendar.getInstance().apply { time = taskDate }

        return when (task.recurrence) {
            RecurrenceType.NONE -> {
                // Tarefa sem recorrência: verifica se está no período
                !taskCal.before(startCal) && !taskCal.after(endCal)
            }
            RecurrenceType.DAILY, RecurrenceType.WEEKLY, RecurrenceType.MONTHLY -> {
                // Tarefas recorrentes: mostra se começou antes ou durante o período
                !taskCal.after(endCal)
            }
        }
    }
}
