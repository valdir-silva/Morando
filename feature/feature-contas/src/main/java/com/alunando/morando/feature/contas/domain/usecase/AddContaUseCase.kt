package com.alunando.morando.feature.contas.domain.usecase

import com.alunando.morando.feature.contas.domain.model.Conta
import com.alunando.morando.feature.contas.domain.repository.ContasRepository

/**
 * Use case para adicionar nova conta
 */
class AddContaUseCase(
    private val repository: ContasRepository,
) {
    suspend operator fun invoke(conta: Conta): Conta = repository.addConta(conta)
}
