package com.alunando.morando.domain.model

/**
 * Sessão de cozinha ativa
 */
data class CookingSession(
    val recipe: Recipe,
    val stoveType: StoveType,
    val currentPhase: CookingPhase,
    val currentStepIndex: Int = 0,
    val miseEnPlaceCompleted: List<Boolean>,
    val timerRunning: Boolean = false,
    val timerSecondsRemaining: Int = 0,
) {
    enum class CookingPhase {
        MISE_EN_PLACE, // Preparação dos ingredientes
        COOKING, // Cozimento
        COMPLETED, // Finalizada
    }

    /**
     * Verifica se todas as etapas do mise en place estão completas
     */
    fun isMiseEnPlaceComplete(): Boolean = miseEnPlaceCompleted.all { it }

    /**
     * Retorna a etapa atual do mise en place
     */
    fun getCurrentMiseEnPlaceStep(): MiseEnPlaceStep? = recipe.etapasMiseEnPlace.getOrNull(currentStepIndex)

    /**
     * Retorna a etapa atual de preparo
     */
    fun getCurrentCookingStep(): CookingStep? = recipe.etapasPreparo.getOrNull(currentStepIndex)

    /**
     * Verifica se pode avançar para a próxima etapa
     */
    fun canGoNext(): Boolean =
        when (currentPhase) {
            CookingPhase.MISE_EN_PLACE -> currentStepIndex < recipe.etapasMiseEnPlace.size - 1
            CookingPhase.COOKING -> currentStepIndex < recipe.etapasPreparo.size - 1
            CookingPhase.COMPLETED -> false
        }

    /**
     * Verifica se pode voltar para a etapa anterior
     */
    fun canGoPrevious(): Boolean = currentStepIndex > 0
}
