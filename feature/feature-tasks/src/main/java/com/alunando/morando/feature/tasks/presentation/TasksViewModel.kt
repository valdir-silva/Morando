package com.alunando.morando.feature.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alunando.morando.core.onError
import com.alunando.morando.core.onSuccess
import com.alunando.morando.domain.model.TaskType
import com.alunando.morando.domain.repository.TasksRepository
import com.alunando.morando.domain.usecase.AddTaskUseCase
import com.alunando.morando.domain.usecase.DeleteTaskUseCase
import com.alunando.morando.domain.usecase.GetDailyTasksUseCase
import com.alunando.morando.domain.usecase.GetWeeklyTasksUseCase
import com.alunando.morando.domain.usecase.MarkTaskCompleteUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para tela de tarefas (MVI)
 */
class TasksViewModel(
    private val getDailyTasksUseCase: GetDailyTasksUseCase,
    private val getWeeklyTasksUseCase: GetWeeklyTasksUseCase,
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
            is TasksIntent.SelectTaskType -> selectTaskType(intent.type)
            is TasksIntent.CreateTask -> createTask(intent.task)
            is TasksIntent.DeleteTask -> deleteTask(intent.taskId)
            is TasksIntent.ExpandCommitment -> toggleExpanded(intent.taskId)
            is TasksIntent.ShowCreateDialog -> showCreateDialog()
            is TasksIntent.HideCreateDialog -> hideCreateDialog()
        }
    }

    private fun loadTasks() {
        _state.value = _state.value.copy(isLoading = true)

        when (_state.value.selectedType) {
            TaskType.DIARIA -> getDailyTasksUseCase()
            TaskType.SEMANAL -> getWeeklyTasksUseCase()
            TaskType.COMPROMISSO -> {
                // Para compromissos, buscamos todas as tarefas e filtramos
                getDailyTasksUseCase() // Pode precisar de um novo use case específico
            }
        }.onEach { tasks ->
            val filteredTasks =
                if (_state.value.selectedType == TaskType.COMPROMISSO) {
                    tasks.filter { it.tipo == TaskType.COMPROMISSO && it.parentTaskId == null }
                } else {
                    tasks
                }
            _state.value =
                _state.value.copy(
                    tasks = filteredTasks,
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

    private fun selectTaskType(type: TaskType) {
        _state.value = _state.value.copy(selectedType = type)
        loadTasks()
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

    private fun toggleExpanded(taskId: String) {
        val expanded = _state.value.expandedCommitments
        _state.value =
            _state.value.copy(
                expandedCommitments =
                    if (expanded.contains(taskId)) {
                        expanded - taskId
                    } else {
                        expanded + taskId
                    },
            )
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
