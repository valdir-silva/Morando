package com.alunando.morando.feature.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alunando.morando.core.onError
import com.alunando.morando.core.onSuccess
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import com.alunando.morando.domain.repository.TasksRepository
import com.alunando.morando.domain.usecase.AddTaskUseCase
import com.alunando.morando.domain.usecase.DeleteTaskUseCase
import com.alunando.morando.domain.usecase.GetSubTasksUseCase
import com.alunando.morando.domain.usecase.GetTasksForDateRangeUseCase
import com.alunando.morando.domain.usecase.GetTasksForDateUseCase
import com.alunando.morando.domain.usecase.MarkTaskCompleteUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * ViewModel para tela de tarefas (MVI)
 */
class TasksViewModel(
    private val getTasksForDateRangeUseCase: GetTasksForDateRangeUseCase,
    private val getTasksForDateUseCase: GetTasksForDateUseCase,
    private val getSubTasksUseCase: GetSubTasksUseCase,
    private val markTaskCompleteUseCase: MarkTaskCompleteUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val tasksRepository: TasksRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(TasksState())
    val state: StateFlow<TasksState> = _state.asStateFlow()

    private val _effect = Channel<TasksEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Não carrega automaticamente - espera o usuário autenticar
        // O loadTasks será chamado quando a UI estiver pronta
    }

    /**
     * Processa intenções do usuário
     */
    fun handleIntent(intent: TasksIntent) {
        when (intent) {
            is TasksIntent.LoadTasks -> loadTasks()
            is TasksIntent.ToggleTaskComplete -> toggleTaskComplete(intent.taskId, intent.complete)
            is TasksIntent.SelectDate -> selectDate(intent.date)
            is TasksIntent.NavigateToToday -> navigateToToday()
            is TasksIntent.NavigateToNextDay -> navigateToNextDay()
            is TasksIntent.NavigateToPrevDay -> navigateToPrevDay()
            is TasksIntent.CreateTask -> createTask(intent.task)
            is TasksIntent.CreateCommitment -> createCommitment(intent.commitment, intent.subTasks)
            is TasksIntent.EditTask -> editTask(intent.task)
            is TasksIntent.UpdateTask -> updateTask(intent.task)
            is TasksIntent.UpdateCommitment -> updateCommitment(intent.commitment, intent.subTasks)
            is TasksIntent.ShowDeleteConfirmation -> showDeleteConfirmation(intent.task)
            is TasksIntent.HideDeleteConfirmation -> hideDeleteConfirmation()
            is TasksIntent.ConfirmDelete -> confirmDelete()
            is TasksIntent.DeleteTask -> deleteTask(intent.taskId)
            is TasksIntent.ShowCreateDialog -> showCreateDialog()
            is TasksIntent.HideCreateDialog -> hideCreateDialog()
        }
    }

    private fun loadTasks() {
        _state.value = _state.value.copy(isLoading = true)

        val (startDate, endDate) = _state.value.dateRange

        getTasksForDateRangeUseCase(startDate, endDate)
            .onEach { tasks ->
                // Agrupa tarefas por data
                val tasksByDate = groupTasksByDate(tasks, startDate, endDate)

                val commitments = tasks.filter { it.tipo == TaskType.COMMITMENT }

                if (commitments.isEmpty()) {
                    // Sem compromissos, atualiza estado imediatamente
                    _state.value =
                        _state.value.copy(
                            tasks = tasks,
                            tasksByDate = tasksByDate,
                            subTasksMap = emptyMap(),
                            isLoading = false,
                            error = null,
                        )
                } else {
                    // Combina flows de todas as sub-tarefas
                    combine(
                        commitments.map { commitment ->
                            getSubTasksUseCase(commitment.id)
                        },
                    ) { subTasksArrays ->
                        // Cria mapa commitment.id -> List<Task>
                        commitments.mapIndexed { index, commitment ->
                            commitment.id to subTasksArrays[index]
                        }.toMap()
                    }.onEach { subTasksMap ->
                        _state.value =
                            _state.value.copy(
                                tasks = tasks,
                                tasksByDate = tasksByDate,
                                subTasksMap = subTasksMap,
                                isLoading = false,
                                error = null,
                            )
                    }.launchIn(viewModelScope)
                }
            }.launchIn(viewModelScope)
    }

    /**
     * Agrupa tarefas por data, expandindo tarefas recorrentes
     */
    private fun groupTasksByDate(
        tasks: List<Task>,
        startDate: Date,
        endDate: Date,
    ): Map<Date, List<Task>> {
        val tasksByDate = mutableMapOf<Date, MutableList<Task>>()

        // Itera por cada dia no período
        val calendar = Calendar.getInstance().apply { time = startDate }
        val endCalendar = Calendar.getInstance().apply { time = endDate }

        while (!calendar.after(endCalendar)) {
            val currentDate = calendar.time

            tasks.forEach { task ->
                if (shouldShowTaskOnDate(task, currentDate)) {
                    val normalizedDate = normalizeDate(currentDate)
                    tasksByDate.getOrPut(normalizedDate) { mutableListOf() }.add(task)
                }
            }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return tasksByDate.toSortedMap(compareBy { it })
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
            com.alunando.morando.domain.model.RecurrenceType.NONE -> {
                isSameDay(taskCal, targetCal)
            }
            com.alunando.morando.domain.model.RecurrenceType.DAILY -> {
                !targetCal.before(taskCal)
            }
            com.alunando.morando.domain.model.RecurrenceType.WEEKLY -> {
                !targetCal.before(taskCal) &&
                    taskCal.get(Calendar.DAY_OF_WEEK) == targetCal.get(Calendar.DAY_OF_WEEK)
            }
            com.alunando.morando.domain.model.RecurrenceType.MONTHLY -> {
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
     * Normaliza data para meia-noite
     */
    private fun normalizeDate(date: Date): Date =
        Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

    private fun toggleTaskComplete(
        taskId: String,
        complete: Boolean,
    ) {
        viewModelScope.launch {
            val result = markTaskCompleteUseCase(taskId, complete)
            result
                .onSuccess {
                    // Verificar se precisa auto-completar o compromisso pai
                    checkAndAutoCompleteParent(taskId)
                    sendEffect(TasksEffect.ShowToast("Tarefa atualizada"))
                }.onError {
                    sendEffect(TasksEffect.ShowError("Erro ao atualizar tarefa"))
                }
        }
    }

    private suspend fun checkAndAutoCompleteParent(taskId: String) {
        // Buscar a tarefa que foi alterada
        val taskResult = tasksRepository.getTaskById(taskId)
        taskResult.onSuccess { task ->
            val parentId = task.parentTaskId ?: return@onSuccess

            // Buscar todas as tarefas para encontrar as sub-tarefas do pai
            tasksRepository.getTasks().collect { allTasks ->
                val subTasks = allTasks.filter { it.parentTaskId == parentId }

                // Se todas as sub-tarefas estão completas, completar o pai
                if (subTasks.isNotEmpty() && subTasks.all { it.completa }) {
                    markTaskCompleteUseCase(parentId, true)
                }
            }
        }
    }

    private fun selectDate(date: Date) {
        _state.value = _state.value.copy(selectedDate = date)
        loadTasks()
    }

    private fun navigateToToday() {
        selectDate(Date())
    }

    private fun navigateToNextDay() {
        val calendar =
            Calendar.getInstance().apply {
                time = _state.value.selectedDate
                add(Calendar.DAY_OF_MONTH, 1)
            }
        selectDate(calendar.time)
    }

    private fun navigateToPrevDay() {
        val calendar =
            Calendar.getInstance().apply {
                time = _state.value.selectedDate
                add(Calendar.DAY_OF_MONTH, -1)
            }
        selectDate(calendar.time)
    }

    private fun createTask(task: com.alunando.morando.domain.model.Task) {
        viewModelScope.launch {
            val result = addTaskUseCase(task)
            result
                .onSuccess {
                    sendEffect(TasksEffect.ShowToast("Tarefa criada com sucesso"))
                    _state.value = _state.value.copy(showCreateDialog = false)
                    loadTasks()
                }.onError {
                    sendEffect(TasksEffect.ShowError("Erro ao criar tarefa"))
                }
        }
    }

    private fun createCommitment(
        commitment: com.alunando.morando.domain.model.Task,
        subTasks: List<com.alunando.morando.domain.model.Task>,
    ) {
        viewModelScope.launch {
            // Cria o compromisso primeiro
            val commitmentResult = addTaskUseCase(commitment)
            commitmentResult
                .onSuccess { createdCommitment ->
                    // Cria as sub-tarefas com o parentTaskId do compromisso
                    subTasks.forEach { subTask ->
                        val subTaskWithParent = subTask.copy(parentTaskId = createdCommitment.id)
                        addTaskUseCase(subTaskWithParent)
                    }
                    sendEffect(TasksEffect.ShowToast("Compromisso criado com sucesso"))
                    _state.value = _state.value.copy(showCreateDialog = false)
                    loadTasks()
                }.onError {
                    sendEffect(TasksEffect.ShowError("Erro ao criar compromisso"))
                }
        }
    }

    private fun editTask(task: Task) {
        _state.value =
            _state.value.copy(
                editingTask = task,
                showCreateDialog = true,
            )
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            val result = tasksRepository.updateTask(task)
            result
                .onSuccess {
                    sendEffect(TasksEffect.ShowToast("Tarefa atualizada com sucesso"))
                    _state.value = _state.value.copy(showCreateDialog = false, editingTask = null)
                    loadTasks()
                }.onError {
                    sendEffect(TasksEffect.ShowError("Erro ao atualizar tarefa"))
                }
        }
    }

    private fun updateCommitment(
        commitment: Task,
        subTasks: List<Task>,
    ) {
        viewModelScope.launch {
            // Atualiza o compromisso
            val commitmentResult = tasksRepository.updateTask(commitment)
            commitmentResult
                .onSuccess {
                    // Busca sub-tarefas existentes
                    tasksRepository.getTasks().collect { allTasks ->
                        val existingSubTasks = allTasks.filter { it.parentTaskId == commitment.id }

                        // Deleta sub-tarefas que não existem mais na lista atualizada
                        existingSubTasks.forEach { existing ->
                            if (subTasks.none { it.id == existing.id }) {
                                deleteTaskUseCase(existing.id)
                            }
                        }

                        // Atualiza ou cria sub-tarefas
                        subTasks.forEach { subTask ->
                            val subTaskWithParent = subTask.copy(parentTaskId = commitment.id)
                            if (subTask.id.isNotEmpty() && existingSubTasks.any { it.id == subTask.id }) {
                                // Atualiza existente
                                tasksRepository.updateTask(subTaskWithParent)
                            } else {
                                // Cria nova
                                addTaskUseCase(subTaskWithParent)
                            }
                        }

                        sendEffect(TasksEffect.ShowToast("Compromisso atualizado com sucesso"))
                        _state.value = _state.value.copy(showCreateDialog = false, editingTask = null)
                        loadTasks()
                    }
                }.onError {
                    sendEffect(TasksEffect.ShowError("Erro ao atualizar compromisso"))
                }
        }
    }

    private fun showDeleteConfirmation(task: Task) {
        _state.value =
            _state.value.copy(
                showDeleteDialog = true,
                taskToDelete = task,
            )
    }

    private fun hideDeleteConfirmation() {
        _state.value =
            _state.value.copy(
                showDeleteDialog = false,
                taskToDelete = null,
            )
    }

    private fun confirmDelete() {
        val taskId = _state.value.taskToDelete?.id ?: return
        hideDeleteConfirmation()
        deleteTask(taskId)
    }

    private fun deleteTask(taskId: String) {
        viewModelScope.launch {
            val result = deleteTaskUseCase(taskId)
            result
                .onSuccess {
                    sendEffect(TasksEffect.ShowToast("Tarefa excluída"))
                    loadTasks()
                }.onError {
                    sendEffect(TasksEffect.ShowError("Erro ao excluir tarefa"))
                }
        }
    }

    private fun showCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = true)
    }

    private fun hideCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = false, editingTask = null)
    }

    private fun sendEffect(effect: TasksEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
