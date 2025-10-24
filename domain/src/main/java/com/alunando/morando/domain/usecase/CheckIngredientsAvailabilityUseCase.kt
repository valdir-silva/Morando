package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.IngredientAvailability
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.repository.InventoryRepository

/**
 * Use case para verificar disponibilidade de ingredientes no inventário
 */
class CheckIngredientsAvailabilityUseCase(
    private val inventoryRepository: InventoryRepository,
) {
    @Suppress("TooGenericExceptionCaught")
    suspend operator fun invoke(recipe: Recipe): Map<String, IngredientAvailability> {
        return recipe.ingredientes.associate { ingrediente ->
            ingrediente.nome to
                if (ingrediente.productId != null) {
                    // Ingrediente vinculado a produto - verifica disponibilidade real
                    try {
                        when (val result = inventoryRepository.getProductById(ingrediente.productId)) {
                            is Result.Success -> {
                                val product = result.data
                                IngredientAvailability(
                                    ingredienteNome = ingrediente.nome,
                                    disponivel = true,
                                    quantidadeNecessaria = "${ingrediente.quantidade} ${ingrediente.unidade}",
                                    quantidadeDisponivel = "Disponível no estoque",
                                    status = IngredientAvailability.AvailabilityStatus.DISPONIVEL,
                                )
                            }
                            is Result.Error -> {
                                // Produto não encontrado
                                IngredientAvailability(
                                    ingredienteNome = ingrediente.nome,
                                    disponivel = false,
                                    quantidadeNecessaria = "${ingrediente.quantidade} ${ingrediente.unidade}",
                                    quantidadeDisponivel = "Não encontrado",
                                    status = IngredientAvailability.AvailabilityStatus.INDISPONIVEL,
                                )
                            }
                            is Result.Loading -> {
                                // Estado de carregamento
                                IngredientAvailability(
                                    ingredienteNome = ingrediente.nome,
                                    disponivel = false,
                                    quantidadeNecessaria = "${ingrediente.quantidade} ${ingrediente.unidade}",
                                    quantidadeDisponivel = "Verificando...",
                                    status = IngredientAvailability.AvailabilityStatus.PARCIAL,
                                )
                            }
                        }
                    } catch (e: Exception) {
                        // Erro ao buscar produto
                        IngredientAvailability(
                            ingredienteNome = ingrediente.nome,
                            disponivel = false,
                            quantidadeNecessaria = "${ingrediente.quantidade} ${ingrediente.unidade}",
                            quantidadeDisponivel = "Erro ao verificar",
                            status = IngredientAvailability.AvailabilityStatus.INDISPONIVEL,
                        )
                    }
                } else {
                    // Ingrediente sem vínculo - não pode verificar disponibilidade
                    IngredientAvailability(
                        ingredienteNome = ingrediente.nome,
                        disponivel = true,
                        quantidadeNecessaria = "${ingrediente.quantidade} ${ingrediente.unidade}",
                        quantidadeDisponivel = "Não vinculado ao estoque",
                        status = IngredientAvailability.AvailabilityStatus.PARCIAL,
                    )
                }
        }
    }
}
