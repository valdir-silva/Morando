package com.alunando.morando.feature.cooking.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alunando.morando.domain.model.IngredientAvailability

/**
 * Chip que mostra a disponibilidade de um ingrediente
 */
@Suppress("MagicNumber")
@Composable
fun IngredientAvailabilityChip(
    availability: IngredientAvailability,
    modifier: Modifier = Modifier,
) {
    val (icon, color, label) =
        when (availability.status) {
            IngredientAvailability.AvailabilityStatus.DISPONIVEL ->
                Triple(
                    Icons.Default.CheckCircle,
                    Color(0xFF4CAF50),
                    "Disponível",
                )
            IngredientAvailability.AvailabilityStatus.PARCIAL ->
                Triple(
                    Icons.Default.Warning,
                    Color(0xFFFFA726),
                    "Parcial",
                )
            IngredientAvailability.AvailabilityStatus.INDISPONIVEL ->
                Triple(
                    Icons.Default.Warning,
                    Color(0xFFF44336),
                    "Indisponível",
                )
        }

    AssistChip(
        onClick = { },
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = availability.ingredienteNome,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "• $label",
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                )
            }
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(16.dp),
            )
        },
        colors =
            AssistChipDefaults.assistChipColors(
                containerColor = color.copy(alpha = 0.1f),
            ),
        modifier = modifier,
    )
}

/**
 * Badge simples de disponibilidade
 */
@Composable
fun AvailabilityBadge(
    totalIngredients: Int,
    availableCount: Int,
    modifier: Modifier = Modifier,
) {
    val percentage = if (totalIngredients > 0) (availableCount * 100) / totalIngredients else 0

    val color =
        when {
            percentage >= 80 -> Color(0xFF4CAF50)
            percentage >= 50 -> Color(0xFFFFA726)
            else -> Color(0xFFF44336)
        }

    AssistChip(
        onClick = { },
        label = {
            Text(
                text = "$availableCount/$totalIngredients ingredientes",
                style = MaterialTheme.typography.bodySmall,
            )
        },
        colors =
            AssistChipDefaults.assistChipColors(
                containerColor = color.copy(alpha = 0.2f),
                labelColor = color,
            ),
        modifier = modifier,
    )
}
