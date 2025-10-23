package com.alunando.morando.domain.usecase

import com.alunando.morando.domain.model.IngredientAvailability
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.repository.InventoryRepository

/**
 * Use case para verificar disponibilidade de ingredientes no inventário
 */
class CheckIngredientsAvailabilityUseCase(
    private val inventoryRepository: InventoryRepository,
) {
    suspend operator fun invoke(recipe: Recipe): Map<String, IngredientAvailability> {
        // TODO: Implementar lógica real quando InventoryRepository estiver completo
        // Por enquanto, retorna mock indicando disponibilidade parcial
        return recipe.ingredientes.associate { ingrediente ->
            ingrediente.nome to
                IngredientAvailability(
                    ingredienteNome = ingrediente.nome,
                    disponivel = true,
                    quantidadeNecessaria = "${ingrediente.quantidade} ${ingrediente.unidade}",
                    quantidadeDisponivel = "${ingrediente.quantidade} ${ingrediente.unidade}",
                    status =
                        when (ingrediente.nome.hashCode() % 3) {
                            0 -> IngredientAvailability.AvailabilityStatus.DISPONIVEL
                            1 -> IngredientAvailability.AvailabilityStatus.PARCIAL
                            else -> IngredientAvailability.AvailabilityStatus.INDISPONIVEL
                        },
                )
        }
    }
}
