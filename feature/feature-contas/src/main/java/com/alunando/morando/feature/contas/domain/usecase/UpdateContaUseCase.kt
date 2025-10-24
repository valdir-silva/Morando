package com.alunando.morando.feature.contas.domain.usecase

import com.alunando.morando.feature.contas.domain.model.Conta
import com.alunando.morando.feature.contas.domain.repository.ContasRepository

/**
 * Use case para atualizar conta existente
 */
class UpdateContaUseCase(
    private val repository: ContasRepository,
) {
    suspend operator fun invoke(conta: Conta) = repository.updateConta(conta)
}
