package com.alunando.morando.sdui.models

import com.squareup.moshi.JsonClass

/**
 * Componente SDUI base
 */
@JsonClass(generateAdapter = true)
data class SDUIScreen(
    val screenId: String,
    val version: String,
    val layout: SDUILayout
)

@JsonClass(generateAdapter = true)
data class SDUILayout(
    val type: String,
    val components: List<SDUIComponentData>
)

@JsonClass(generateAdapter = true)
data class SDUIComponentData(
    val id: String,
    val type: String,
    val properties: Map<String, Any?>? = null
)

/**
 * Sealed class para componentes renderizáveis
 */
sealed class SDUIComponent {
    data class Text(
        val value: String,
        val style: TextStyle = TextStyle.BODY
    ) : SDUIComponent()

    data class Button(
        val label: String,
        val action: SDUIAction
    ) : SDUIComponent()

    data class List(
        val dataSource: String,
        val itemType: String
    ) : SDUIComponent()

    data class Column(
        val children: kotlin.collections.List<SDUIComponent>
    ) : SDUIComponent()

    data class Row(
        val children: kotlin.collections.List<SDUIComponent>
    ) : SDUIComponent()
}

enum class TextStyle {
    TITLE,
    SUBTITLE,
    BODY,
    CAPTION
}

/**
 * Ações do SDUI
 */
sealed class SDUIAction {
    data class Navigate(val route: String) : SDUIAction()
    data class ShowToast(val message: String) : SDUIAction()
    data object Back : SDUIAction()
}

