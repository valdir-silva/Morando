package com.alunando.morando.feature.contas.presentation

/**
 * Efeitos colaterais da tela de contas
 */
sealed interface ContasEffect {
    data class ShowToast(
        val message: String,
    ) : ContasEffect

    data class ShowError(
        val message: String,
    ) : ContasEffect
}

