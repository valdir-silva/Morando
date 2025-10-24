package com.alunando.morando.feature.contas.domain.usecase

import com.alunando.morando.feature.contas.domain.model.Conta
import com.alunando.morando.feature.contas.domain.repository.ContasRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case para buscar contas pendentes
 */
class GetContasPendentesUseCase(
    private val repository: ContasRepository,
) {
    operator fun invoke(): Flow<List<Conta>> = repository.getContasPendentes()
}

