package com.alunando.morando.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Etapa de preparo/cozimento
 */
@Parcelize
data class CookingStep(
    val ordem: Int,
    val titulo: String,
    val tempoMinutos: Int = 0,
    val instrucoesGerais: String,
    val instrucaoInducao: String = "",
    val instrucaoGas: String = "",
    val instrucaoEletrico: String = "",
    val instrucaoLenha: String = "",
) : Parcelable {
    /**
     * Obtém a instrução específica para o tipo de fogão
     */
    fun getInstrucaoParaFogao(stoveType: StoveType): String =
        when (stoveType) {
            StoveType.INDUCTION -> instrucaoInducao.ifEmpty { instrucoesGerais }
            StoveType.GAS -> instrucaoGas.ifEmpty { instrucoesGerais }
            StoveType.ELECTRIC -> instrucaoEletrico.ifEmpty { instrucoesGerais }
            StoveType.WOOD -> instrucaoLenha.ifEmpty { instrucoesGerais }
        }
}
