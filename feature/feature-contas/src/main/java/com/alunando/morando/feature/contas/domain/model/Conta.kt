package com.alunando.morando.feature.contas.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Modelo de domínio para Conta/Despesa
 */
@Parcelize
data class Conta(
    val id: String = "",
    val nome: String,
    val descricao: String = "",
    val valor: Double,
    val dataVencimento: Date,
    val dataPagamento: Date? = null,
    val categoria: ContaCategoria,
    val recorrencia: ContaRecorrencia = ContaRecorrencia.NENHUMA,
    val status: ContaStatus = ContaStatus.PENDENTE,
    val userId: String = "",
    val createdAt: Date = Date(),
) : Parcelable {
    /**
     * Verifica se a conta está atrasada
     */
    fun isAtrasada(): Boolean =
        status == ContaStatus.PENDENTE && dataVencimento.before(Date())

    /**
     * Calcula dias até o vencimento (negativo se atrasada)
     */
    @Suppress("MagicNumber")
    fun diasAteVencimento(): Long {
        val diff = dataVencimento.time - Date().time
        return diff / (1000 * 60 * 60 * 24)
    }
}

/**
 * Categoria da conta
 */
enum class ContaCategoria {
    MORADIA,
    ALIMENTACAO,
    TRANSPORTE,
    SAUDE,
    EDUCACAO,
    LAZER,
    OUTROS,
    ;

    companion object {
        fun fromString(value: String): ContaCategoria =
            when (value.lowercase()) {
                "moradia" -> MORADIA
                "alimentacao" -> ALIMENTACAO
                "transporte" -> TRANSPORTE
                "saude" -> SAUDE
                "educacao" -> EDUCACAO
                "lazer" -> LAZER
                else -> OUTROS
            }
    }
}

/**
 * Status da conta
 */
enum class ContaStatus {
    PENDENTE,
    PAGA,
    ATRASADA,
    ;

    companion object {
        fun fromString(value: String): ContaStatus =
            when (value.lowercase()) {
                "pendente" -> PENDENTE
                "paga" -> PAGA
                "atrasada" -> ATRASADA
                else -> PENDENTE
            }
    }
}

/**
 * Recorrência da conta
 */
enum class ContaRecorrencia {
    NENHUMA,
    MENSAL,
    ANUAL,
    ;

    companion object {
        fun fromString(value: String): ContaRecorrencia =
            when (value.lowercase()) {
                "nenhuma" -> NENHUMA
                "mensal" -> MENSAL
                "anual" -> ANUAL
                else -> NENHUMA
            }
    }
}

