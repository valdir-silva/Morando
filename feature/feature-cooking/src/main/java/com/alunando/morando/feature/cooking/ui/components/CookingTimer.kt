package com.alunando.morando.feature.cooking.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Componente de cronômetro para cooking
 */
@Suppress("LongParameterList", "MagicNumber")
@Composable
fun CookingTimer(
    secondsRemaining: Int,
    isRunning: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier,
    showStartButton: Boolean = true,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Display do tempo
            Text(
                text = formatTime(secondsRemaining),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )

            Spacer(modifier = Modifier.padding(8.dp))

            // Controles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showStartButton && secondsRemaining == 0) {
                    Button(onClick = onStart) {
                        Text("Iniciar Timer")
                    }
                } else {
                    // Botão Play/Pause
                    Button(onClick = if (isRunning) onPause else onResume) {
                        Text(if (isRunning) "⏸ Pausar" else "▶ Continuar")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Botão Stop
                    Button(onClick = onStop) {
                        Text("⏹ Parar")
                    }
                }
            }
        }
    }
}

/**
 * Formata segundos em MM:SS
 */
private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return "%02d:%02d".format(minutes, secs)
}
