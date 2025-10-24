package com.alunando.morando.feature.contas.presentation

import com.alunando.morando.feature.contas.domain.model.Conta
import java.util.Date

/**
 * Intenções (ações) da tela de contas
 */
sealed interface ContasIntent {
    data object LoadContas : ContasIntent

    data class SelectMonth(
        val year: Int,
        val month: Int,
    ) : ContasIntent

    data class CreateConta(
        val conta: Conta,
    ) : ContasIntent

    data class UpdateConta(
        val conta: Conta,
    ) : ContasIntent

    data class DeleteConta(
        val contaId: String,
    ) : ContasIntent

    data class MarkContaPaga(
        val contaId: String,
        val dataPagamento: Date = Date(),
    ) : ContasIntent

    data object ShowCreateDialog : ContasIntent

    data object HideCreateDialog : ContasIntent

    data class EditConta(
        val conta: Conta,
    ) : ContasIntent
}

