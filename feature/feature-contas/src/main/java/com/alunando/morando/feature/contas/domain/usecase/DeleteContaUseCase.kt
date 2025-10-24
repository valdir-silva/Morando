package com.alunando.morando.feature.contas.domain.usecase

import com.alunando.morando.feature.contas.domain.repository.ContasRepository

/**
 * Use case para deletar conta
 */
class DeleteContaUseCase(
    private val repository: ContasRepository,
) {
    suspend operator fun invoke(contaId: String) = repository.deleteConta(contaId)
}
