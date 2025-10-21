package com.alunando.morando.feature.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alunando.morando.core.onError
import com.alunando.morando.core.onSuccess
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.usecase.AddProductUseCase
import com.alunando.morando.domain.usecase.DeleteProductUseCase
import com.alunando.morando.domain.usecase.GetProductsUseCase
import com.alunando.morando.domain.usecase.UpdateProductUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para tela de inventário (MVI)
 */
class InventoryViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(InventoryState())
    val state: StateFlow<InventoryState> = _state.asStateFlow()

    private val _effect = Channel<InventoryEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Carrega produtos automaticamente
        handleIntent(InventoryIntent.LoadProducts)
    }

    /**
     * Processa intenções do usuário
     */
    fun handleIntent(intent: InventoryIntent) {
        when (intent) {
            is InventoryIntent.LoadProducts -> loadProducts()
            is InventoryIntent.AddProduct -> addProduct(intent.product)
            is InventoryIntent.UpdateProduct -> updateProduct(intent.product)
            is InventoryIntent.DeleteProduct -> deleteProduct(intent.productId)
            is InventoryIntent.OpenAddDialog -> openAddDialog()
            is InventoryIntent.CloseAddDialog -> closeAddDialog()
            is InventoryIntent.OpenBarcodeScanner -> openBarcodeScanner()
            is InventoryIntent.FilterByCategory -> filterByCategory(intent.category)
        }
    }

    private fun loadProducts() {
        _state.value = _state.value.copy(isLoading = true)

        getProductsUseCase()
            .onEach { products ->
                _state.value =
                    _state.value.copy(
                        products = products,
                        isLoading = false,
                        error = null
                    )
            }.launchIn(viewModelScope)
    }

    private fun addProduct(product: Product) {
        viewModelScope.launch {
            val result = addProductUseCase(product)
            result
                .onSuccess {
                    sendEffect(InventoryEffect.ShowToast("Produto adicionado com sucesso"))
                    closeAddDialog()
                }.onError { error ->
                    sendEffect(InventoryEffect.ShowError(error.message ?: "Erro ao adicionar produto"))
                }
        }
    }

    private fun updateProduct(product: Product) {
        viewModelScope.launch {
            val result = updateProductUseCase(product)
            result
                .onSuccess {
                    sendEffect(InventoryEffect.ShowToast("Produto atualizado com sucesso"))
                }.onError { error ->
                    sendEffect(InventoryEffect.ShowError(error.message ?: "Erro ao atualizar produto"))
                }
        }
    }

    private fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val result = deleteProductUseCase(productId)
            result
                .onSuccess {
                    sendEffect(InventoryEffect.ShowToast("Produto removido com sucesso"))
                }.onError { error ->
                    sendEffect(InventoryEffect.ShowError(error.message ?: "Erro ao remover produto"))
                }
        }
    }

    private fun openAddDialog() {
        _state.value = _state.value.copy(showAddDialog = true)
    }

    private fun closeAddDialog() {
        _state.value = _state.value.copy(showAddDialog = false)
    }

    private fun openBarcodeScanner() {
        viewModelScope.launch {
            _effect.send(InventoryEffect.NavigateToBarcodeScanner)
        }
    }

    private fun filterByCategory(category: String?) {
        _state.value = _state.value.copy(selectedCategory = category)
        // TODO: Implementar filtro quando necessário
    }

    private fun sendEffect(effect: InventoryEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}

