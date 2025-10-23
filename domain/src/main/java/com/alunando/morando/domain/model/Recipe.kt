package com.alunando.morando.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Modelo de domínio para Receita
 */
@Parcelize
data class Recipe(
    val id: String = "",
    val nome: String,
    val descricao: String,
    val categoria: RecipeCategory,
    val tempoPreparo: Int,
    val porcoes: Int,
    val dificuldade: RecipeDifficulty,
    val fotoUrl: String = "",
    val ingredientes: List<Ingredient>,
    val etapasMiseEnPlace: List<MiseEnPlaceStep>,
    val etapasPreparo: List<CookingStep>,
    val tipoFogaoPadrao: StoveType = StoveType.INDUCTION,
    val userId: String = "",
    val createdAt: Date = Date(),
) : Parcelable {
    /**
     * Retorna o tempo total estimado (preparo + soma de tempos das etapas)
     */
    fun tempoTotalEstimado(): Int = tempoPreparo + etapasPreparo.sumOf { it.tempoMinutos }
}
