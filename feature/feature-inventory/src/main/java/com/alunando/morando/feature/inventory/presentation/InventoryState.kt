package com.alunando.morando.feature.inventory.presentation

import com.alunando.morando.domain.model.Product

/**
 * Estado da tela de inventário (MVI)
 */
data class InventoryState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val selectedCategory: String? = null
)

