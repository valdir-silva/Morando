package com.alunando.morando.feature.contas.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alunando.morando.feature.contas.domain.usecase.AddContaUseCase
import com.alunando.morando.feature.contas.domain.usecase.DeleteContaUseCase
import com.alunando.morando.feature.contas.domain.usecase.GetContasByMonthUseCase
import com.alunando.morando.feature.contas.domain.usecase.GetTotaisByMonthUseCase
import com.alunando.morando.feature.contas.domain.usecase.MarkContaPagaUseCase
import com.alunando.morando.feature.contas.domain.usecase.UpdateContaUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para tela de contas (MVI)
 */
@Suppress("TooManyFunctions")
class ContasViewModel(
    private val getContasByMonthUseCase: GetContasByMonthUseCase,
    private val getTotaisByMonthUseCase: GetTotaisByMonthUseCase,
    private val addContaUseCase: AddContaUseCase,
    private val updateContaUseCase: UpdateContaUseCase,
    private val deleteContaUseCase: DeleteContaUseCase,
    private val markContaPagaUseCase: MarkContaPagaUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ContasState())
    val state: StateFlow<ContasState> = _state.asStateFlow()

    private val _effect = Channel<ContasEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Carrega contas do mês atual
        loadContas()
    }

    /**
     * Processa intenções do usuário
     */
    fun handleIntent(intent: ContasIntent) {
        when (intent) {
            is ContasIntent.LoadContas -> loadContas()
            is ContasIntent.SelectMonth -> selectMonth(intent.year, intent.month)
            is ContasIntent.CreateConta -> createConta(intent.conta)
            is ContasIntent.UpdateConta -> updateConta(intent.conta)
            is ContasIntent.DeleteConta -> deleteConta(intent.contaId)
            is ContasIntent.MarkContaPaga -> markContaPaga(intent.contaId, intent.dataPagamento)
            is ContasIntent.ShowCreateDialog -> showCreateDialog()
            is ContasIntent.HideCreateDialog -> hideCreateDialog()
            is ContasIntent.EditConta -> editConta(intent.conta)
        }
    }

    private fun loadContas() {
        _state.value = _state.value.copy(isLoading = true)

        getContasByMonthUseCase(_state.value.selectedYear, _state.value.selectedMonth)
            .onEach { contas ->
                _state.value =
                    _state.value.copy(
                        contas = contas,
                        isLoading = false,
                        error = null,
                    )
                loadTotais()
            }.launchIn(viewModelScope)
    }

    private fun loadTotais() {
        viewModelScope.launch {
            try {
                val totais =
                    getTotaisByMonthUseCase(
                        _state.value.selectedYear,
                        _state.value.selectedMonth,
                    )
                _state.value = _state.value.copy(totais = totais)
            } catch (_: Exception) {
                // Ignorar erro de totais
            }
        }
    }

    private fun selectMonth(
        year: Int,
        month: Int,
    ) {
        _state.value =
            _state.value.copy(
                selectedYear = year,
                selectedMonth = month,
            )
        loadContas()
    }

    private fun createConta(conta: com.alunando.morando.feature.contas.domain.model.Conta) {
        viewModelScope.launch {
            try {
                addContaUseCase(conta)
                sendEffect(ContasEffect.ShowToast("Conta criada com sucesso"))
                _state.value = _state.value.copy(showCreateDialog = false)
            } catch (e: Exception) {
                sendEffect(ContasEffect.ShowError("Erro ao criar conta: ${e.message}"))
            }
        }
    }

    private fun updateConta(conta: com.alunando.morando.feature.contas.domain.model.Conta) {
        viewModelScope.launch {
            try {
                updateContaUseCase(conta)
                sendEffect(ContasEffect.ShowToast("Conta atualizada com sucesso"))
                _state.value = _state.value.copy(showCreateDialog = false, editingConta = null)
            } catch (e: Exception) {
                sendEffect(ContasEffect.ShowError("Erro ao atualizar conta: ${e.message}"))
            }
        }
    }

    private fun deleteConta(contaId: String) {
        viewModelScope.launch {
            try {
                deleteContaUseCase(contaId)
                sendEffect(ContasEffect.ShowToast("Conta excluída"))
            } catch (e: Exception) {
                sendEffect(ContasEffect.ShowError("Erro ao excluir conta: ${e.message}"))
            }
        }
    }

    private fun markContaPaga(
        contaId: String,
        dataPagamento: java.util.Date,
    ) {
        viewModelScope.launch {
            try {
                markContaPagaUseCase(contaId, dataPagamento)
                sendEffect(ContasEffect.ShowToast("Conta marcada como paga"))
            } catch (e: Exception) {
                sendEffect(ContasEffect.ShowError("Erro ao marcar conta como paga: ${e.message}"))
            }
        }
    }

    private fun showCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = true, editingConta = null)
    }

    private fun hideCreateDialog() {
        _state.value = _state.value.copy(showCreateDialog = false, editingConta = null)
    }

    private fun editConta(conta: com.alunando.morando.feature.contas.domain.model.Conta) {
        _state.value = _state.value.copy(showCreateDialog = true, editingConta = conta)
    }

    private fun sendEffect(effect: ContasEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}
