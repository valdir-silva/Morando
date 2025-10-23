package com.alunando.morando.data.api

import com.alunando.morando.domain.model.Product
import kotlinx.coroutines.delay
import java.util.Date

/**
 * Mock do data source de API para testes
 */
class ProductApiDataSourceMock {
    /**
     * Simula busca de produto por código de barras
     */
    @Suppress("MagicNumber")
    suspend fun searchProductByBarcode(barcode: String): Product? {
        // Simula latência de rede
        delay(500)

        // Retorna produtos fictícios baseados no código
        return when {
            barcode.startsWith("789") ->
                Product(
                    nome = "Arroz Tio João 5kg",
                    categoria = "Alimentos",
                    codigoBarras = barcode,
                    fotoUrl = "https://via.placeholder.com/300x300.png?text=Arroz",
                    valor = 25.90,
                    detalhes = "Marca: Tio João\nPeso: 5kg",
                    createdAt = Date(),
                )

            barcode.startsWith("890") ->
                Product(
                    nome = "Feijão Camil 1kg",
                    categoria = "Alimentos",
                    codigoBarras = barcode,
                    fotoUrl = "https://via.placeholder.com/300x300.png?text=Feijao",
                    valor = 8.50,
                    detalhes = "Marca: Camil\nPeso: 1kg",
                    createdAt = Date(),
                )

            barcode.startsWith("750") ->
                Product(
                    nome = "Café Pilão 500g",
                    categoria = "Bebidas",
                    codigoBarras = barcode,
                    fotoUrl = "https://via.placeholder.com/300x300.png?text=Cafe",
                    valor = 15.90,
                    detalhes = "Marca: Pilão\nPeso: 500g",
                    createdAt = Date(),
                )

            else ->
                Product(
                    nome = "Produto Genérico",
                    categoria = "Diversos",
                    codigoBarras = barcode,
                    fotoUrl = "",
                    valor = 10.00,
                    detalhes = "Produto de exemplo para teste",
                    createdAt = Date(),
                )
        }
    }
}
