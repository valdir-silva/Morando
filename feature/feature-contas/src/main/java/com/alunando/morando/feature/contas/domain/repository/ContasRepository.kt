package com.alunando.morando.feature.contas.domain.repository

import com.alunando.morando.feature.contas.domain.model.Conta
import com.alunando.morando.feature.contas.domain.model.ContaCategoria
import com.alunando.morando.feature.contas.domain.model.ContaStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * Interface do repositório de contas
 */
interface ContasRepository {
    /**
     * Busca todas as contas do usuário
     */
    fun getContas(): Flow<List<Conta>>

    /**
     * Busca contas de um mês específico
     */
    fun getContasByMonth(
        year: Int,
        month: Int,
    ): Flow<List<Conta>>

    /**
     * Busca contas por status
     */
    fun getContasByStatus(status: ContaStatus): Flow<List<Conta>>

    /**
     * Busca contas por categoria
     */
    fun getContasByCategoria(categoria: ContaCategoria): Flow<List<Conta>>

    /**
     * Busca contas pendentes
     */
    fun getContasPendentes(): Flow<List<Conta>>

    /**
     * Busca conta por ID
     */
    suspend fun getContaById(contaId: String): Conta?

    /**
     * Adiciona nova conta
     */
    suspend fun addConta(conta: Conta): Conta

    /**
     * Atualiza conta existente
     */
    suspend fun updateConta(conta: Conta)

    /**
     * Deleta conta
     */
    suspend fun deleteConta(contaId: String)

    /**
     * Marca conta como paga
     */
    suspend fun markContaPaga(
        contaId: String,
        dataPagamento: Date,
    )

    /**
     * Calcula total de contas por mês e status
     */
    suspend fun getTotaisByMonth(
        year: Int,
        month: Int,
    ): ContaTotais
}

/**
 * Totais de contas
 */
data class ContaTotais(
    val totalPendente: Double = 0.0,
    val totalPago: Double = 0.0,
    val totalAtrasado: Double = 0.0,
    val totalGeral: Double = 0.0,
)

