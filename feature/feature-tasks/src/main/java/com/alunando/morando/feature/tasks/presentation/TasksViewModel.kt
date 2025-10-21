package com.alunando.morando.feature.tasks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alunando.morando.core.onError
import com.alunando.morando.core.onSuccess
import com.alunando.morando.domain.model.TaskType
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
    private val markTaskCompleteUseCase: MarkTaskCompleteUseCase
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
        }
    }

    private fun loadTasks() {
        _state.value = _state.value.copy(isLoading = true)

        when (_state.value.selectedType) {
            TaskType.DIARIA -> getDailyTasksUseCase()
            TaskType.SEMANAL -> getWeeklyTasksUseCase()
        }.onEach { tasks ->
            _state.value = _state.value.copy(
                tasks = tasks,
                isLoading = false,
                error = null
            )
        }.launchIn(viewModelScope)
    }

    private fun toggleTaskComplete(taskId: String, complete: Boolean) {
        viewModelScope.launch {
            val result = markTaskCompleteUseCase(taskId, complete)
            result.onSuccess {
                sendEffect(TasksEffect.ShowToast("Tarefa atualizada"))
            }.onError {
                sendEffect(TasksEffect.ShowError("Erro ao atualizar tarefa"))
            }
        }
    }

    private fun selectTaskType(type: TaskType) {
        _state.value = _state.value.copy(selectedType = type)
        loadTasks()
    }

    private fun sendEffect(effect: TasksEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}

