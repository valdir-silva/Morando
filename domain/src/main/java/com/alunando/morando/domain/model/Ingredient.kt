package com.alunando.morando.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Ingrediente de uma receita
 */
@Parcelize
data class Ingredient(
    val nome: String,
    val quantidade: Double,
    val unidade: String,
    val observacoes: String = "",
    val productId: String? = null,
) : Parcelable
