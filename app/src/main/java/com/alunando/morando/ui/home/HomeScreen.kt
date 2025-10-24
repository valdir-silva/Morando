package com.alunando.morando.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alunando.morando.R

/**
 * Tela inicial com navegação para as features do app
 */
@Composable
fun HomeScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToShopping: () -> Unit,
    onNavigateToCooking: () -> Unit,
    onNavigateToContas: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val features =
        listOf(
            FeatureCard(
                title = "Tarefas",
                description = "Gerencie suas tarefas do dia a dia",
                iconRes = R.drawable.ic_tasks,
                onClick = onNavigateToTasks,
            ),
            FeatureCard(
                title = "Estoques",
                description = "Controle produtos e vencimentos",
                iconRes = R.drawable.ic_inventory,
                onClick = onNavigateToInventory,
            ),
            FeatureCard(
                title = "Lista de Compras",
                description = "Organize suas compras",
                iconRes = R.drawable.ic_shopping,
                onClick = onNavigateToShopping,
            ),
            FeatureCard(
                title = "Cozinhando",
                description = "Receitas e parceiro de cozinha",
                iconRes = R.drawable.ic_cooking,
                onClick = onNavigateToCooking,
            ),
            FeatureCard(
                title = "Contas",
                description = "Gerencie suas contas e despesas",
                iconRes = R.drawable.ic_tasks, // TODO: criar ícone específico para contas
                onClick = onNavigateToContas,
            ),
        )

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Morando",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(features) { feature ->
                FeatureCardItem(feature = feature)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeatureCardItem(feature: FeatureCard) {
    Card(
        onClick = feature.onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                painter = painterResource(id = feature.iconRes),
                contentDescription = feature.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp),
            )
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = feature.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private data class FeatureCard(
    val title: String,
    val description: String,
    @DrawableRes val iconRes: Int,
    val onClick: () -> Unit,
)

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        Surface {
            HomeScreen(
                onNavigateToTasks = {},
                onNavigateToInventory = {},
                onNavigateToShopping = {},
                onNavigateToCooking = {},
                onNavigateToContas = {},
            )
        }
    }
}
