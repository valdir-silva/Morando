package com.alunando.morando.domain.model

/**
 * Nível de dificuldade da receita
 */
enum class RecipeDifficulty(val displayName: String) {
    FACIL("Fácil"),
    MEDIA("Média"),
    DIFICIL("Difícil"),
    ;

    companion object {
        fun fromString(value: String): RecipeDifficulty =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: MEDIA
    }
}
