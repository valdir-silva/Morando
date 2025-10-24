package com.alunando.morando.feature.contas.presentation

import com.alunando.morando.feature.contas.domain.model.Conta
import com.alunando.morando.feature.contas.domain.repository.ContaTotais
import java.util.Calendar

/**
 * Estado da tela de contas
 */
data class ContasState(
    val contas: List<Conta> = emptyList(),
    val totais: ContaTotais = ContaTotais(),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val editingConta: Conta? = null,
)
