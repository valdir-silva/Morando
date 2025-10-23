package com.alunando.morando.feature.cooking.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alunando.morando.domain.model.CookingSession
import com.alunando.morando.domain.model.CookingStep
import com.alunando.morando.domain.model.MiseEnPlaceStep
import com.alunando.morando.feature.cooking.presentation.CookingIntent
import com.alunando.morando.feature.cooking.presentation.CookingViewModel
import com.alunando.morando.feature.cooking.ui.components.CookingTimer
import com.alunando.morando.feature.cooking.ui.components.StepProgressIndicator

/**
 * Tela de sessão de cozinha - Parceiro de cozinha interativo
 */
@Suppress("LongMethod", "MagicNumber")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingSessionScreen(
    recipeId: String,
    viewModel: CookingViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }

    LaunchedEffect(recipeId) {
        viewModel.handleIntent(CookingIntent.StartCooking(recipeId))
    }

    val session = state.cookingSession

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(session?.recipe?.nome ?: "Cozinhando") },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(Icons.Default.Close, "Sair")
                    }
                },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        if (session == null) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
            ) {
                // Indicador de progresso
                when (session.currentPhase) {
                    CookingSession.CookingPhase.MISE_EN_PLACE -> {
                        StepProgressIndicator(
                            currentStep = session.currentStepIndex,
                            totalSteps = session.recipe.etapasMiseEnPlace.size,
                            phaseName = "Mise en Place",
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Conteúdo de mise en place
                        MiseEnPlaceContent(
                            session = session,
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    CookingSession.CookingPhase.COOKING -> {
                        StepProgressIndicator(
                            currentStep = session.currentStepIndex,
                            totalSteps = session.recipe.etapasPreparo.size,
                            phaseName = "Preparo",
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Conteúdo de preparo
                        CookingContent(
                            session = session,
                            viewModel = viewModel,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    CookingSession.CookingPhase.COMPLETED -> {
                        // Finalizado
                        CompletedContent(
                            onFinish = onNavigateBack,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }

    // Dialog de confirmação de saída
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Sair do modo de cozinha?") },
            text = { Text("Seu progresso será perdido.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.handleIntent(CookingIntent.StopCooking)
                        onNavigateBack()
                    },
                ) {
                    Text("Sair")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

@Suppress("LongMethod")
@Composable
private fun MiseEnPlaceContent(
    session: CookingSession,
    viewModel: CookingViewModel,
    modifier: Modifier = Modifier,
) {
    val currentStep = session.getCurrentMiseEnPlaceStep()

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
    ) {
        if (currentStep != null) {
            // Card da etapa atual
            MiseEnPlaceStepCard(
                step = currentStep,
                isCompleted = session.miseEnPlaceCompleted.getOrNull(session.currentStepIndex) ?: false,
                onToggleComplete = {
                    viewModel.handleIntent(CookingIntent.ToggleMiseEnPlaceStep(session.currentStepIndex))
                },
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Resumo de todas as etapas
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                ) {
                    Text(
                        text = "Checklist Completo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    session.recipe.etapasMiseEnPlace.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = session.miseEnPlaceCompleted.getOrNull(index) ?: false,
                                onCheckedChange = {
                                    viewModel.handleIntent(CookingIntent.ToggleMiseEnPlaceStep(index))
                                },
                            )
                            Text(
                                text = "${step.ingrediente} - ${step.instrucao}",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navegação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedButton(
                onClick = { viewModel.handleIntent(CookingIntent.PreviousStep) },
                enabled = session.canGoPrevious(),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                Text("Anterior")
            }

            if (session.canGoNext()) {
                Button(onClick = { viewModel.handleIntent(CookingIntent.NextStep) }) {
                    Text("Próximo")
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                }
            } else {
                Button(
                    onClick = { viewModel.handleIntent(CookingIntent.StartCookingPhase) },
                    enabled = session.isMiseEnPlaceComplete(),
                ) {
                    Text("Iniciar Preparo")
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                }
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun MiseEnPlaceStepCard(
    step: MiseEnPlaceStep,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (isCompleted) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.secondaryContainer
                    },
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = step.tipoDePreparo.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = step.ingrediente,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = step.quantidade,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    )
                }

                IconButton(onClick = onToggleComplete) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Marcar como completo",
                        tint =
                            if (isCompleted) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f)
                            },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Instrução:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = step.instrucao,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun CookingContent(
    session: CookingSession,
    viewModel: CookingViewModel,
    modifier: Modifier = Modifier,
) {
    val currentStep = session.getCurrentCookingStep()

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
    ) {
        if (currentStep != null) {
            // Card da etapa de preparo
            CookingStepCard(
                step = currentStep,
                stoveType = session.stoveType,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Cronômetro (se a etapa tiver tempo)
            if (currentStep.tempoMinutos > 0) {
                CookingTimer(
                    secondsRemaining = session.timerSecondsRemaining,
                    isRunning = session.timerRunning,
                    onStart = {
                        viewModel.handleIntent(CookingIntent.StartTimer(currentStep.tempoMinutos * 60))
                    },
                    onPause = { viewModel.handleIntent(CookingIntent.PauseTimer) },
                    onResume = { viewModel.handleIntent(CookingIntent.ResumeTimer) },
                    onStop = { viewModel.handleIntent(CookingIntent.StopTimer) },
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Navegação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedButton(
                onClick = { viewModel.handleIntent(CookingIntent.PreviousStep) },
                enabled = session.canGoPrevious(),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                Text("Anterior")
            }

            if (session.canGoNext()) {
                Button(onClick = { viewModel.handleIntent(CookingIntent.NextStep) }) {
                    Text("Próximo")
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                }
            } else {
                Button(onClick = { viewModel.handleIntent(CookingIntent.NextStep) }) {
                    Text("Concluir")
                    Icon(Icons.Default.CheckCircle, null)
                }
            }
        }
    }
}

@Composable
private fun CookingStepCard(
    step: CookingStep,
    stoveType: com.alunando.morando.domain.model.StoveType,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
        ) {
            Text(
                text = "ETAPA ${step.ordem}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = step.titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            if (step.tempoMinutos > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "⏱️ Tempo estimado: ${step.tempoMinutos} minutos",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            // Tipo de fogão
            Text(
                text = "🔥 ${stoveType.displayName}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Instrução específica para o fogão
            Text(
                text = step.getInstrucaoParaFogao(stoveType),
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
            )
        }
    }
}

@Composable
private fun CompletedContent(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Receita Concluída!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bom apetite!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Finalizar")
        }
    }
}
