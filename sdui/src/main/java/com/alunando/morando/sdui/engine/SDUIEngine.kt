package com.alunando.morando.sdui.engine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alunando.morando.sdui.models.SDUIComponent
import com.alunando.morando.sdui.models.TextStyle

/**
 * Motor de renderização SDUI
 */
object SDUIEngine {
    /**
     * Renderiza um componente SDUI
     */
    @Composable
    fun RenderComponent(
        component: SDUIComponent,
        modifier: Modifier = Modifier,
        onAction: (com.alunando.morando.sdui.models.SDUIAction) -> Unit = {},
    ) {
        when (component) {
            is SDUIComponent.Text -> RenderText(component)
            is SDUIComponent.Button -> RenderButton(component, onAction)
            is SDUIComponent.List -> RenderList(component)
            is SDUIComponent.Column -> RenderColumn(component, modifier, onAction)
            is SDUIComponent.Row -> RenderRow(component, modifier, onAction)
        }
    }

    @Composable
    private fun RenderText(component: SDUIComponent.Text) {
        val textStyle =
            when (component.style) {
                TextStyle.TITLE -> MaterialTheme.typography.titleLarge
                TextStyle.SUBTITLE -> MaterialTheme.typography.titleMedium
                TextStyle.BODY -> MaterialTheme.typography.bodyMedium
                TextStyle.CAPTION -> MaterialTheme.typography.bodySmall
            }

        Text(
            text = component.value,
            style = textStyle,
        )
    }

    @Composable
    private fun RenderButton(
        component: SDUIComponent.Button,
        onAction: (com.alunando.morando.sdui.models.SDUIAction) -> Unit,
    ) {
        Button(onClick = { onAction(component.action) }) {
            Text(component.label)
        }
    }

    @Composable
    private fun RenderList(component: SDUIComponent.List) {
        // Implementar renderização de lista dinâmica
        Text("Lista dinâmica: ${component.dataSource}")
    }

    @Composable
    private fun RenderColumn(
        component: SDUIComponent.Column,
        modifier: Modifier,
        onAction: (com.alunando.morando.sdui.models.SDUIAction) -> Unit,
    ) {
        Column(modifier = modifier) {
            component.children.forEach { child ->
                RenderComponent(child, onAction = onAction)
            }
        }
    }

    @Composable
    private fun RenderRow(
        component: SDUIComponent.Row,
        modifier: Modifier,
        onAction: (com.alunando.morando.sdui.models.SDUIAction) -> Unit,
    ) {
        Row(modifier = modifier) {
            component.children.forEach { child ->
                RenderComponent(child, onAction = onAction)
            }
        }
    }
}
