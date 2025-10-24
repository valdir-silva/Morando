package com.alunando.morando.feature.contas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.alunando.morando.feature.contas.domain.model.Conta
import com.alunando.morando.feature.contas.domain.model.ContaStatus
import com.alunando.morando.feature.contas.presentation.ContasIntent
import com.alunando.morando.feature.contas.presentation.ContasViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Tela principal de contas
 */
@Composable
fun ContasScreen(
    viewModel: ContasViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(ContasIntent.LoadContas)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(ContasIntent.ShowCreateDialog) },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Conta")
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Card de totais
            TotaisCard(
                totalPendente = state.totais.totalPendente,
                totalPago = state.totais.totalPago,
                totalAtrasado = state.totais.totalAtrasado,
                totalGeral = state.totais.totalGeral,
            )

            // Lista de contas
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.contas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Nenhuma conta para este mês",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.contas) { conta ->
                        ContaCard(
                            conta = conta,
                            onMarkPaga = {
                                viewModel.handleIntent(ContasIntent.MarkContaPaga(conta.id))
                            },
                            onDelete = {
                                viewModel.handleIntent(ContasIntent.DeleteConta(conta.id))
                            },
                        )
                    }

                    // Espaçamento no final para o FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Dialog de criação/edição
        if (state.showCreateDialog) {
            ContaFormDialog(
                onDismiss = { viewModel.handleIntent(ContasIntent.HideCreateDialog) },
                onSave = { conta ->
                    if (state.editingConta != null) {
                        viewModel.handleIntent(ContasIntent.UpdateConta(conta))
                    } else {
                        viewModel.handleIntent(ContasIntent.CreateConta(conta))
                    }
                },
                existingConta = state.editingConta,
            )
        }
    }
}

@Composable
fun TotaisCard(
    totalPendente: Double,
    totalPago: Double,
    totalAtrasado: Double,
    totalGeral: Double,
    modifier: Modifier = Modifier,
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Resumo do Mês",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TotalItem("Pendente", currencyFormat.format(totalPendente), MaterialTheme.colorScheme.primary)
                TotalItem("Pago", currencyFormat.format(totalPago), MaterialTheme.colorScheme.tertiary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TotalItem("Atrasado", currencyFormat.format(totalAtrasado), MaterialTheme.colorScheme.error)
                TotalItem("Total", currencyFormat.format(totalGeral), MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
fun TotalItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
        )
    }
}

@Composable
fun ContaCard(
    conta: Conta,
    onMarkPaga: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    val cardColor =
        when (conta.status) {
            ContaStatus.PAGA -> MaterialTheme.colorScheme.tertiaryContainer
            ContaStatus.ATRASADA -> MaterialTheme.colorScheme.errorContainer
            ContaStatus.PENDENTE ->
                if (conta.isAtrasada()) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
        }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = conta.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (conta.status == ContaStatus.PAGA) TextDecoration.LineThrough else null,
                )
                if (conta.descricao.isNotEmpty()) {
                    Text(
                        text = conta.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currencyFormat.format(conta.valor),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Venc: ${dateFormatter.format(conta.dataVencimento)}",
                    style = MaterialTheme.typography.bodySmall,
                )
                if (conta.status == ContaStatus.PAGA && conta.dataPagamento != null) {
                    Text(
                        text = "Pago em: ${dateFormatter.format(conta.dataPagamento)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (conta.status != ContaStatus.PAGA) {
                    IconButton(onClick = onMarkPaga) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Marcar como paga",
                            tint = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

