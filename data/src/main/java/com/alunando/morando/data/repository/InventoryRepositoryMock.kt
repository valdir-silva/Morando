package com.alunando.morando.data.repository

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.Product
import com.alunando.morando.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar
import java.util.Date
import java.util.UUID

/**
 * Implementação mock do InventoryRepository para desenvolvimento sem Firebase
 */
class InventoryRepositoryMock : InventoryRepository {
    private val mockProducts =
        listOf(
            Product(
                id = UUID.randomUUID().toString(),
                nome = "Leite Integral",
                categoria = "Laticínios",
                codigoBarras = "7891234567890",
                fotoUrl = "https://via.placeholder.com/200",
                dataCompra = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -5) }.time,
                valor = 4.50,
                detalhes = "Leite integral 1L",
                dataVencimento = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 3) }.time,
                diasParaAcabar = 5,
                userId = "mock-user",
                createdAt = Date(),
            ),
            Product(
                id = UUID.randomUUID().toString(),
                nome = "Arroz Branco",
                categoria = "Grãos",
                codigoBarras = "7891234567891",
                fotoUrl = "https://via.placeholder.com/200",
                dataCompra = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -10) }.time,
                valor = 25.90,
                detalhes = "Arroz branco tipo 1, 5kg",
                dataVencimento = Calendar.getInstance().apply { add(Calendar.MONTH, 6) }.time,
                diasParaAcabar = 15,
                userId = "mock-user",
                createdAt = Date(),
            ),
            Product(
                id = UUID.randomUUID().toString(),
                nome = "Feijão Preto",
                categoria = "Grãos",
                codigoBarras = "7891234567892",
                fotoUrl = "https://via.placeholder.com/200",
                dataCompra = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -8) }.time,
                valor = 8.90,
                detalhes = "Feijão preto 1kg",
                dataVencimento = Calendar.getInstance().apply { add(Calendar.MONTH, 12) }.time,
                diasParaAcabar = 20,
                userId = "mock-user",
                createdAt = Date(),
            ),
            Product(
                id = UUID.randomUUID().toString(),
                nome = "Tomate",
                categoria = "Hortifruti",
                codigoBarras = "",
                fotoUrl = "https://via.placeholder.com/200",
                dataCompra = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -2) }.time,
                valor = 6.50,
                detalhes = "Tomate fresco 1kg",
                dataVencimento = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 5) }.time,
                diasParaAcabar = 3,
                userId = "mock-user",
                createdAt = Date(),
            ),
            Product(
                id = UUID.randomUUID().toString(),
                nome = "Óleo de Soja",
                categoria = "Óleos",
                codigoBarras = "7891234567893",
                fotoUrl = "https://via.placeholder.com/200",
                dataCompra = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -15) }.time,
                valor = 7.20,
                detalhes = "Óleo de soja 900ml",
                dataVencimento = Calendar.getInstance().apply { add(Calendar.MONTH, 8) }.time,
                diasParaAcabar = 10,
                userId = "mock-user",
                createdAt = Date(),
            ),
            Product(
                id = UUID.randomUUID().toString(),
                nome = "Pão de Forma",
                categoria = "Padaria",
                codigoBarras = "7891234567894",
                fotoUrl = "https://via.placeholder.com/200",
                dataCompra = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
                valor = 5.80,
                detalhes = "Pão de forma integral 500g",
                dataVencimento = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 2) }.time,
                diasParaAcabar = 2,
                userId = "mock-user",
                createdAt = Date(),
            ),
        )

    override fun getProducts(): Flow<List<Product>> = flowOf(mockProducts)

    override suspend fun getProductById(productId: String): Result<Product> {
        val product = mockProducts.find { it.id == productId }
        return if (product != null) {
            Result.Success(product)
        } else {
            Result.Error(Exception("Produto não encontrado"))
        }
    }

    override fun getProductsByCategory(category: String): Flow<List<Product>> =
        flowOf(mockProducts.filter { it.categoria == category })

    override suspend fun getProductByBarcode(barcode: String): Result<Product?> {
        val product = mockProducts.find { it.codigoBarras == barcode }
        return Result.Success(product)
    }

    override suspend fun addProduct(product: Product): Result<Product> =
        Result.Success(product.copy(id = UUID.randomUUID().toString()))

    override suspend fun updateProduct(product: Product): Result<Unit> = Result.Success(Unit)

    override suspend fun deleteProduct(productId: String): Result<Unit> = Result.Success(Unit)

    override suspend fun uploadProductImage(
        productId: String,
        imageData: ByteArray,
    ): Result<String> = Result.Success("https://via.placeholder.com/200?text=Mock+Image")

    override fun getProductsNeedingReplenishment(): Flow<List<Product>> =
        flowOf(mockProducts.filter { it.diasParaAcabar <= 7 })

    @Suppress("MagicNumber")
    override suspend fun getProductInfoFromBarcode(barcode: String): Result<Product?> {
        // Simula busca em API externa
        kotlinx.coroutines.delay(500)

        return Result.Success(
            when {
                barcode.startsWith("789") ->
                    Product(
                        nome = "Arroz Tio João 5kg",
                        categoria = "Alimentos",
                        codigoBarras = barcode,
                        fotoUrl = "https://via.placeholder.com/300x300.png?text=Arroz",
                        valor = 25.90,
                        detalhes = "Marca: Tio João\nPeso: 5kg",
                        diasParaAcabar = 30,
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
                        diasParaAcabar = 45,
                        createdAt = Date(),
                    )
                else -> null
            },
        )
    }
}
