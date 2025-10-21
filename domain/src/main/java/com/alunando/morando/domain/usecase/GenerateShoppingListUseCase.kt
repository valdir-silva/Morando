package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.ShoppingItem
import com.alunando.morando.domain.repository.InventoryRepository
import com.alunando.morando.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.first

/**
 * Use case para gerar lista de compras automaticamente
 * baseado em produtos que estão acabando ou vencidos
 */
class GenerateShoppingListUseCase(
    private val inventoryRepository: InventoryRepository,
    private val shoppingRepository: ShoppingRepository
) {
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(): Result<List<ShoppingItem>> {
        return try {
            // Busca produtos que precisam ser repostos
            val productsNeedingReplenishment = inventoryRepository
                .getProductsNeedingReplenishment()
                .first()

            // Cria itens da lista de compras
            val shoppingItems = productsNeedingReplenishment.map { product ->
                ShoppingItem(
                    produtoId = product.id,
                    nome = product.nome,
                    quantidade = 1
                )
            }

            // Adiciona cada item à lista
            shoppingItems.forEach { item ->
                shoppingRepository.addShoppingItem(item)
            }

            Result.Success(shoppingItems)
        } catch (e: Exception) {
            Result.Error(e, "Erro ao gerar lista de compras")
        }
    }
}
