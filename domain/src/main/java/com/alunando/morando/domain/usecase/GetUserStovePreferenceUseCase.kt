package com.alunando.morando.domain.usecase

import com.alunando.morando.domain.model.StoveType
import com.alunando.morando.domain.repository.CookingRepository

/**
 * Use case para obter o tipo de fogão preferido do usuário
 */
class GetUserStovePreferenceUseCase(
    private val repository: CookingRepository,
) {
    suspend operator fun invoke(): StoveType = repository.getUserStovePreference()
}
