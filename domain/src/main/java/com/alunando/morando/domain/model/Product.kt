package com.alunando.morando.domain.model

import android.os.Parcelable
import java.util.Date
import kotlinx.parcelize.Parcelize

/**
 * Modelo de domínio para Produto
 */
@Parcelize
data class Product(
    val id: String = "",
    val nome: String,
    val categoria: String = "",
    val codigoBarras: String = "",
    val fotoUrl: String = "",
    val dataCompra: Date? = null,
    val valor: Double = 0.0,
    val detalhes: String = "",
    val dataVencimento: Date? = null,
    val diasParaAcabar: Int = 0,
    val userId: String = "",
    val createdAt: Date = Date()
) : Parcelable {

    /**
     * Verifica se o produto está vencido
     */
    fun isVencido(): Boolean {
        return dataVencimento?.let { it.before(Date()) } ?: false
    }

    /**
     * Verifica se o produto está próximo do vencimento (dentro de 7 dias)
     */
    @Suppress("MagicNumber")
    fun isProximoVencimento(): Boolean {
        return dataVencimento?.let {
            val diasRestantes = Date().time.let { now ->
                (it.time - now) / (1000 * 60 * 60 * 24)
            }
            diasRestantes in 0..7
        } ?: false
    }

    /**
     * Verifica se o produto está acabando
     */
    @Suppress("MagicNumber")
    fun isAcabando(): Boolean {
        return diasParaAcabar <= 7 && diasParaAcabar > 0
    }
}
