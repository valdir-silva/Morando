package com.alunando.morando.domain.model

/**
 * Disponibilidade de um ingrediente no inventário
 */
data class IngredientAvailability(
    val ingredienteNome: String,
    val disponivel: Boolean,
    val quantidadeNecessaria: String,
    val quantidadeDisponivel: String = "",
    val status: AvailabilityStatus,
) {
    enum class AvailabilityStatus {
        DISPONIVEL, // Tem tudo que precisa
        PARCIAL, // Tem mas não o suficiente
        INDISPONIVEL, // Não tem
    }
}
