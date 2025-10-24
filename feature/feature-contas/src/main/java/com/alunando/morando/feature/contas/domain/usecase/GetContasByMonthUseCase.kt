package com.alunando.morando.feature.contas.domain.usecase

import com.alunando.morando.feature.contas.domain.model.Conta
import com.alunando.morando.feature.contas.domain.repository.ContasRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case para buscar contas de um mês específico
 */
class GetContasByMonthUseCase(
    private val repository: ContasRepository,
) {
    operator fun invoke(
        year: Int,
        month: Int,
    ): Flow<List<Conta>> = repository.getContasByMonth(year, month)
}
