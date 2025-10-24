package com.alunando.morando.feature.contas.domain.usecase

import com.alunando.morando.feature.contas.domain.repository.ContaTotais
import com.alunando.morando.feature.contas.domain.repository.ContasRepository

/**
 * Use case para calcular totais de contas por mês
 */
class GetTotaisByMonthUseCase(
    private val repository: ContasRepository,
) {
    suspend operator fun invoke(
        year: Int,
        month: Int,
    ): ContaTotais = repository.getTotaisByMonth(year, month)
}

