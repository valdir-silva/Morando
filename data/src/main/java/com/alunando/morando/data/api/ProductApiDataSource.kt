package com.alunando.morando.data.api

import com.alunando.morando.data.api.model.CosmosResponse
import com.alunando.morando.data.api.model.OpenFoodFactsProduct
import com.alunando.morando.domain.model.Product
import java.util.Date

/**
 * Data source para buscar informações de produtos em APIs externas
 */
class ProductApiDataSource(
    private val cosmosApiService: CosmosApiService,
    private val openFoodFactsApiService: OpenFoodFactsApiService,
) {
    /**
     * Busca informações do produto por código de barras
     * Tenta primeiro na API Cosmos, depois OpenFoodFacts
     */
    suspend fun searchProductByBarcode(barcode: String): Product? {
        // Tenta buscar na Cosmos primeiro
        val cosmosProduct = searchInCosmos(barcode)
        if (cosmosProduct != null) {
            return cosmosProduct
        }

        // Se não encontrar, tenta OpenFoodFacts
        return searchInOpenFoodFacts(barcode)
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun searchInCosmos(barcode: String): Product? =
        try {
            // Token demo da Cosmos - em produção deve vir de config seguro
            val token = "Z2RGOG9SelptelRqTkxjVmM6"
            val response = cosmosApiService.getProductByGtin(barcode, token)

            if (response.isSuccessful && response.body() != null) {
                response.body()?.toProduct(barcode)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun searchInOpenFoodFacts(barcode: String): Product? =
        try {
            val response = openFoodFactsApiService.getProductByBarcode(barcode)

            if (response.isSuccessful && response.body()?.status == 1) {
                response.body()?.product?.toProduct(barcode)
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }

    private fun CosmosResponse.toProduct(barcode: String): Product {
        val name = description ?: "Produto $barcode"
        val category = ncm?.description ?: brand ?: ""

        return Product(
            nome = name,
            categoria = category,
            codigoBarras = barcode,
            fotoUrl = thumbnail ?: "",
            valor = avgPrice ?: 0.0,
            detalhes = "Marca: ${brand ?: "N/A"}",
            createdAt = Date(),
        )
    }

    private fun OpenFoodFactsProduct.toProduct(barcode: String): Product {
        val name = productName ?: "Produto $barcode"
        val category = categories?.split(",")?.firstOrNull()?.trim() ?: ""

        return Product(
            nome = name,
            categoria = category,
            codigoBarras = barcode,
            fotoUrl = imageUrl ?: "",
            detalhes = "Marca: ${brands ?: "N/A"}\nQuantidade: ${quantity ?: "N/A"}",
            createdAt = Date(),
        )
    }
}
