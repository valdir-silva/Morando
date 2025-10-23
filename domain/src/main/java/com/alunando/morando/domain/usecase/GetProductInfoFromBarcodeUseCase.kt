package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.repository.InventoryRepository

/**
 * Use case para buscar informações de produto a partir do código de barras
 */
class GetProductInfoFromBarcodeUseCase(
    private val repository: InventoryRepository,
) {
    /**
     * Busca informações do produto em APIs externas
     * Se não encontrar, retorna null para o usuário criar manualmente
     */
    suspend operator fun invoke(barcode: String): Result<Product?> = repository.getProductInfoFromBarcode(barcode)
}
