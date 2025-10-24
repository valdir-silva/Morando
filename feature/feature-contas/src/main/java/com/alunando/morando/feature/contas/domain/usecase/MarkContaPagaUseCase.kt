package com.alunando.morando.feature.contas.domain.usecase

import com.alunando.morando.feature.contas.domain.repository.ContasRepository
import java.util.Date

/**
 * Use case para marcar conta como paga
 */
class MarkContaPagaUseCase(
    private val repository: ContasRepository,
) {
    suspend operator fun invoke(
        contaId: String,
        dataPagamento: Date = Date(),
    ) = repository.markContaPaga(contaId, dataPagamento)
}

