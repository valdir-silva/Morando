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
import com.alunando.morando.domain.usecase.GetTasksForDateUseCase
import com.alunando.morando.domain.usecase.MarkTaskCompleteUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            is TasksIntent.DeleteTask -> deleteTask(intent.taskId)
            is TasksIntent.ShowCreateDialog -> showCreateDialog()
            is TasksIntent.HideCreateDialog -> hideCreateDialog()
        }
    }

    private fun loadTasks() {
        _state.value = _state.value.copy(isLoading = true)

        getTasksForDateUseCase(_state.value.selectedDate)
            .onEach { tasks ->
                // Carrega sub-tarefas para cada compromisso
                val subTasksMap = mutableMapOf<String, List<Task>>()
                tasks
                    .filter { it.tipo == TaskType.COMMITMENT }
                    .forEach { commitment ->
                        getSubTasksUseCase(commitment.id)
                            .onEach { subTasks ->
                                subTasksMap[commitment.id] = subTasks
                            }.launchIn(viewModelScope)
                    }

                _state.value =
                    _state.value.copy(
                        tasks = tasks,
                        subTasksMap = subTasksMap,
                        isLoading = false,
                        error = null,
                    )
            }.launchIn(viewModelScope)
    }

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
