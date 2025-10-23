package com.alunando.morando.domain.usecase

import com.alunando.morando.core.Result
import com.alunando.morando.domain.model.StoveType
import com.alunando.morando.domain.repository.CookingRepository

/**
 * Use case para salvar o tipo de fogão preferido do usuário
 */
class SaveUserStovePreferenceUseCase(
    private val repository: CookingRepository,
) {
    suspend operator fun invoke(stoveType: StoveType): Result<Unit> = repository.saveUserStovePreference(stoveType)
}
