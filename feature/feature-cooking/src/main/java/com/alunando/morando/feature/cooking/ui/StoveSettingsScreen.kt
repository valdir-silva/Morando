package com.alunando.morando.feature.cooking.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alunando.morando.domain.model.StoveType
import com.alunando.morando.feature.cooking.presentation.CookingIntent
import com.alunando.morando.feature.cooking.presentation.CookingViewModel

/**
 * Tela de configuração do tipo de fogão
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoveSettingsScreen(
    viewModel: CookingViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(CookingIntent.LoadStovePreference)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tipo de Fogão") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "Selecione o tipo de fogão que você usa",
                style = MaterialTheme.typography.titleMedium,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "As instruções das receitas serão adaptadas para o seu tipo de fogão",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.selectableGroup(),
            ) {
                StoveType.entries.forEach { stoveType ->
                    StoveTypeOption(
                        stoveType = stoveType,
                        selected = state.currentStoveType == stoveType,
                        onClick = {
                            viewModel.handleIntent(CookingIntent.SelectStoveType(stoveType))
                        },
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun StoveTypeOption(
    stoveType: StoveType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    role = Role.RadioButton,
                ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
            )

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
            ) {
                Text(
                    text = getStoveEmoji(stoveType) + " " + stoveType.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = getStoveDescription(stoveType),
                    style = MaterialTheme.typography.bodyMedium,
                    color =
                        if (selected) {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }
    }
}

private fun getStoveEmoji(stoveType: StoveType): String =
    when (stoveType) {
        StoveType.INDUCTION -> "⚡"
        StoveType.GAS -> "🔥"
        StoveType.ELECTRIC -> "🔌"
        StoveType.WOOD -> "🪵"
    }

private fun getStoveDescription(stoveType: StoveType): String =
    when (stoveType) {
        StoveType.INDUCTION ->
            "Cooktop por indução - Controle preciso de temperatura e potência"
        StoveType.GAS ->
            "Fogão a gás - Controle por intensidade da chama"
        StoveType.ELECTRIC ->
            "Fogão elétrico - Controle por temperatura em graus Celsius"
        StoveType.WOOD ->
            "Fogão a lenha - Controle por intensidade das brasas"
    }
