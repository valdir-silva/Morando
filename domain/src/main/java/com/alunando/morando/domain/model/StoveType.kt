package com.alunando.morando.domain.model

/**
 * Tipos de fogão suportados pelo app
 */
enum class StoveType(val displayName: String) {
    INDUCTION("Indução"),
    GAS("Gás"),
    ELECTRIC("Elétrico"),
    WOOD("Lenha"),
    ;

    companion object {
        fun fromString(value: String): StoveType =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: INDUCTION
    }
}
