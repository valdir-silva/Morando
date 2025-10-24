package com.alunando.morando.domain.model

/**
 * Categorias de receitas
 */
enum class RecipeCategory(val displayName: String) {
    ENTRADA("Entrada"),
    PRATO_PRINCIPAL("Prato Principal"),
    ACOMPANHAMENTO("Acompanhamento"),
    SOBREMESA("Sobremesa"),
    BEBIDA("Bebida"),
    LANCHE("Lanche"),
    CAFE_DA_MANHA("Café da Manhã"),
    ;

    companion object {
        fun fromString(value: String): RecipeCategory =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: PRATO_PRINCIPAL
    }
}
