package com.alunando.morando.feature.inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alunando.morando.core.onError
import com.alunando.morando.core.onSuccess
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.usecase.AddProductUseCase
import com.alunando.morando.domain.usecase.DeleteProductUseCase
import com.alunando.morando.domain.usecase.GetProductInfoFromBarcodeUseCase
import com.alunando.morando.domain.usecase.GetProductsUseCase
import com.alunando.morando.domain.usecase.UpdateProductUseCase
import com.alunando.morando.domain.usecase.UploadProductImageUseCase
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
    private val deleteProductUseCase: DeleteProductUseCase,
    private val uploadProductImageUseCase: UploadProductImageUseCase,
    private val getProductInfoFromBarcodeUseCase: GetProductInfoFromBarcodeUseCase
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
            is InventoryIntent.AddProduct -> addProduct(intent.product, intent.imageData)
            is InventoryIntent.UpdateProduct -> updateProduct(intent.product, intent.imageData)
            is InventoryIntent.DeleteProduct -> deleteProduct(intent.productId)
            is InventoryIntent.OpenAddDialog -> openAddDialog()
            is InventoryIntent.OpenEditDialog -> openEditDialog(intent.product)
            is InventoryIntent.CloseAddDialog -> closeAddDialog()
            is InventoryIntent.OpenBarcodeScanner -> openBarcodeScanner()
            is InventoryIntent.OnBarcodeScanned -> onBarcodeScanned(intent.barcode)
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

    private fun addProduct(
        product: Product,
        imageData: ByteArray?
    ) {
        viewModelScope.launch {
            // Primeiro adiciona o produto
            val result = addProductUseCase(product)
            result
                .onSuccess { addedProduct ->
                    // Se tem imagem, faz upload
                    if (imageData != null) {
                        uploadProductImageUseCase(addedProduct.id, imageData)
                            .onSuccess { imageUrl ->
                                // Atualiza produto com URL da imagem
                                updateProductUseCase(addedProduct.copy(fotoUrl = imageUrl))
                            }
                    }
                    sendEffect(InventoryEffect.ShowToast("Produto adicionado com sucesso"))
                    closeAddDialog()
                }.onError { error ->
                    sendEffect(InventoryEffect.ShowError(error.message ?: "Erro ao adicionar produto"))
                }
        }
    }

    private fun updateProduct(
        product: Product,
        imageData: ByteArray?
    ) {
        viewModelScope.launch {
            // Se tem nova imagem, faz upload primeiro
            var updatedProduct = product
            if (imageData != null) {
                uploadProductImageUseCase(product.id, imageData)
                    .onSuccess { imageUrl ->
                        updatedProduct = product.copy(fotoUrl = imageUrl)
                    }
            }

            val result = updateProductUseCase(updatedProduct)
            result
                .onSuccess {
                    sendEffect(InventoryEffect.ShowToast("Produto atualizado com sucesso"))
                    closeAddDialog()
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
        _state.value = _state.value.copy(
            showAddDialog = true,
            showEditDialog = false,
            scannedProduct = null,
            editingProduct = null
        )
    }

    private fun openEditDialog(product: Product) {
        _state.value = _state.value.copy(
            showEditDialog = true,
            showAddDialog = false,
            editingProduct = product,
            scannedProduct = null
        )
    }

    private fun closeAddDialog() {
        _state.value = _state.value.copy(
            showAddDialog = false,
            showEditDialog = false,
            scannedProduct = null,
            editingProduct = null
        )
    }

    private fun openBarcodeScanner() {
        viewModelScope.launch {
            _effect.send(InventoryEffect.NavigateToBarcodeScanner)
        }
    }

    private fun onBarcodeScanned(barcode: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingBarcodeInfo = true)

            // Busca informações do produto na API externa
            val result = getProductInfoFromBarcodeUseCase(barcode)
            result
                .onSuccess { productInfo ->
                    _state.value =
                        _state.value.copy(
                            scannedProduct = productInfo ?: Product(
                                codigoBarras = barcode,
                                nome = ""
                            ),
                            isLoadingBarcodeInfo = false,
                            showAddDialog = true
                        )
                    if (productInfo != null) {
                        sendEffect(InventoryEffect.ShowToast("Produto encontrado!"))
                    } else {
                        sendEffect(InventoryEffect.ShowToast("Produto não encontrado. Preencha os dados manualmente."))
                    }
                }.onError { error ->
                    _state.value = _state.value.copy(
                        scannedProduct = Product(codigoBarras = barcode, nome = ""),
                        isLoadingBarcodeInfo = false,
                        showAddDialog = true
                    )
                    sendEffect(InventoryEffect.ShowToast("Erro ao buscar produto. Preencha manualmente."))
                }
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
