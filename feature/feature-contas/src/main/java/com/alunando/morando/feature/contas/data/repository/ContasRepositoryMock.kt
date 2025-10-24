package com.alunando.morando.feature.contas.data.repository

import com.alunando.morando.feature.contas.domain.model.Conta
import com.alunando.morando.feature.contas.domain.model.ContaCategoria
import com.alunando.morando.feature.contas.domain.model.ContaRecorrencia
import com.alunando.morando.feature.contas.domain.model.ContaStatus
import com.alunando.morando.feature.contas.domain.repository.ContaTotais
import com.alunando.morando.feature.contas.domain.repository.ContasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import java.util.UUID

/**
 * Implementação mock do ContasRepository para desenvolvimento
 */
@Suppress("MagicNumber")
class ContasRepositoryMock : ContasRepository {
    private val contas =
        MutableStateFlow(
            listOf(
                // Contas do mês atual
                Conta(
                    id = "conta-1",
                    nome = "Aluguel",
                    descricao = "Aluguel mensal do apartamento",
                    valor = 1500.0,
                    dataVencimento = getDateForDay(10),
                    categoria = ContaCategoria.MORADIA,
                    recorrencia = ContaRecorrencia.MENSAL,
                    status = ContaStatus.PAGA,
                    dataPagamento = getDateForDay(9),
                    userId = "mock-user",
                ),
                Conta(
                    id = "conta-2",
                    nome = "Conta de Luz",
                    descricao = "Energia elétrica",
                    valor = 180.0,
                    dataVencimento = getDateForDay(15),
                    categoria = ContaCategoria.MORADIA,
                    recorrencia = ContaRecorrencia.MENSAL,
                    status = ContaStatus.PENDENTE,
                    userId = "mock-user",
                ),
                Conta(
                    id = "conta-3",
                    nome = "Internet",
                    descricao = "Internet banda larga 200MB",
                    valor = 120.0,
                    dataVencimento = getDateForDay(20),
                    categoria = ContaCategoria.MORADIA,
                    recorrencia = ContaRecorrencia.MENSAL,
                    status = ContaStatus.PENDENTE,
                    userId = "mock-user",
                ),
                Conta(
                    id = "conta-4",
                    nome = "Supermercado",
                    descricao = "Compras do mês",
                    valor = 800.0,
                    dataVencimento = getDateForDay(25),
                    categoria = ContaCategoria.ALIMENTACAO,
                    recorrencia = ContaRecorrencia.NENHUMA,
                    status = ContaStatus.PENDENTE,
                    userId = "mock-user",
                ),
                Conta(
                    id = "conta-5",
                    nome = "Plano de Saúde",
                    descricao = "Unimed Plus",
                    valor = 450.0,
                    dataVencimento = getDateForDay(5),
                    categoria = ContaCategoria.SAUDE,
                    recorrencia = ContaRecorrencia.MENSAL,
                    status = ContaStatus.ATRASADA,
                    userId = "mock-user",
                ),
                Conta(
                    id = "conta-6",
                    nome = "Gasolina",
                    descricao = "Combustível do carro",
                    valor = 300.0,
                    dataVencimento = getDateForDay(30),
                    categoria = ContaCategoria.TRANSPORTE,
                    recorrencia = ContaRecorrencia.NENHUMA,
                    status = ContaStatus.PENDENTE,
                    userId = "mock-user",
                ),
                Conta(
                    id = "conta-7",
                    nome = "Academia",
                    descricao = "Mensalidade Smart Fit",
                    valor = 89.90,
                    dataVencimento = getDateForDay(12),
                    categoria = ContaCategoria.SAUDE,
                    recorrencia = ContaRecorrencia.MENSAL,
                    status = ContaStatus.PAGA,
                    dataPagamento = getDateForDay(10),
                    userId = "mock-user",
                ),
                Conta(
                    id = "conta-8",
                    nome = "Netflix",
                    descricao = "Assinatura streaming",
                    valor = 55.90,
                    dataVencimento = getDateForDay(18),
                    categoria = ContaCategoria.LAZER,
                    recorrencia = ContaRecorrencia.MENSAL,
                    status = ContaStatus.PENDENTE,
                    userId = "mock-user",
                ),
                Conta(
                    id = "conta-9",
                    nome = "IPVA",
                    descricao = "IPVA do carro",
                    valor = 1200.0,
                    dataVencimento = getDateForMonth(3, 31),
                    categoria = ContaCategoria.TRANSPORTE,
                    recorrencia = ContaRecorrencia.ANUAL,
                    status = ContaStatus.PENDENTE,
                    userId = "mock-user",
                ),
                Conta(
                    id = "conta-10",
                    nome = "Curso Online",
                    descricao = "Udemy - Curso de Kotlin",
                    valor = 150.0,
                    dataVencimento = getDateForDay(28),
                    categoria = ContaCategoria.EDUCACAO,
                    recorrencia = ContaRecorrencia.NENHUMA,
                    status = ContaStatus.PAGA,
                    dataPagamento = getDateForDay(25),
                    userId = "mock-user",
                ),
            ),
        )

    override fun getContas(): Flow<List<Conta>> = contas

    override fun getContasByMonth(
        year: Int,
        month: Int,
    ): Flow<List<Conta>> =
        contas.map { contasList ->
            contasList.filter { conta ->
                val calendar = Calendar.getInstance().apply { time = conta.dataVencimento }
                calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month - 1
            }
        }

    override fun getContasByStatus(status: ContaStatus): Flow<List<Conta>> =
        contas.map { contasList ->
            contasList.filter { it.status == status }
        }

    override fun getContasByCategoria(categoria: ContaCategoria): Flow<List<Conta>> =
        contas.map { contasList ->
            contasList.filter { it.categoria == categoria }
        }

    override fun getContasPendentes(): Flow<List<Conta>> =
        contas.map { contasList ->
            contasList.filter { it.status == ContaStatus.PENDENTE || it.isAtrasada() }
        }

    override suspend fun getContaById(contaId: String): Conta? = contas.value.find { it.id == contaId }

    override suspend fun addConta(conta: Conta): Conta {
        val newConta = conta.copy(id = if (conta.id.isEmpty()) UUID.randomUUID().toString() else conta.id)
        contas.value = contas.value + newConta
        return newConta
    }

    override suspend fun updateConta(conta: Conta) {
        contas.value = contas.value.map { if (it.id == conta.id) conta else it }
    }

    override suspend fun deleteConta(contaId: String) {
        contas.value = contas.value.filter { it.id != contaId }
    }

    override suspend fun markContaPaga(
        contaId: String,
        dataPagamento: Date,
    ) {
        contas.value =
            contas.value.map {
                if (it.id == contaId) {
                    it.copy(status = ContaStatus.PAGA, dataPagamento = dataPagamento)
                } else {
                    it
                }
            }
    }

    override suspend fun getTotaisByMonth(
        year: Int,
        month: Int,
    ): ContaTotais {
        val contasDoMes =
            contas.value.filter { conta ->
                val calendar = Calendar.getInstance().apply { time = conta.dataVencimento }
                calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month - 1
            }

        val totalPendente = contasDoMes.filter { it.status == ContaStatus.PENDENTE }.sumOf { it.valor }
        val totalPago = contasDoMes.filter { it.status == ContaStatus.PAGA }.sumOf { it.valor }
        val totalAtrasado = contasDoMes.filter { it.isAtrasada() }.sumOf { it.valor }
        val totalGeral = contasDoMes.sumOf { it.valor }

        return ContaTotais(
            totalPendente = totalPendente,
            totalPago = totalPago,
            totalAtrasado = totalAtrasado,
            totalGeral = totalGeral,
        )
    }

    // Helper functions
    private fun getDateForDay(day: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    private fun getDateForMonth(
        month: Int,
        day: Int,
    ): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }
}
