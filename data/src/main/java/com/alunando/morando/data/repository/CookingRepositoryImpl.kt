package com.alunando.morando.data.repository

import com.alunando.morando.core.Result
import com.alunando.morando.data.datasource.CookingRemoteDataSource
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.model.RecipeCategory
import com.alunando.morando.domain.model.StoveType
import com.alunando.morando.domain.repository.CookingRepository
import kotlinx.coroutines.flow.Flow

/**
 * Implementação do repositório de receitas com Firebase
 */
class CookingRepositoryImpl(
    private val remoteDataSource: CookingRemoteDataSource,
) : CookingRepository {
    override fun getRecipes(): Flow<List<Recipe>> = remoteDataSource.getRecipes()

    override fun getRecipesByCategory(category: RecipeCategory): Flow<List<Recipe>> =
        remoteDataSource.getRecipesByCategory(category)

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getRecipeById(id: String): Result<Recipe> =
        try {
            val recipe = remoteDataSource.getRecipeById(id)
            if (recipe != null) {
                Result.Success(recipe)
            } else {
                Result.Error(Exception("Receita não encontrada"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun addRecipe(recipe: Recipe): Result<Unit> =
        try {
            remoteDataSource.addRecipe(recipe)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun updateRecipe(recipe: Recipe): Result<Unit> =
        try {
            remoteDataSource.updateRecipe(recipe)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun deleteRecipe(id: String): Result<Unit> =
        try {
            remoteDataSource.deleteRecipe(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getUserStovePreference(): StoveType =
        try {
            remoteDataSource.getUserStovePreference()
        } catch (e: Exception) {
            StoveType.INDUCTION
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun saveUserStovePreference(stoveType: StoveType): Result<Unit> =
        try {
            remoteDataSource.saveUserStovePreference(stoveType)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun uploadRecipeImage(
        recipeId: String,
        imageData: ByteArray,
    ): Result<String> =
        try {
            val imageUrl = remoteDataSource.uploadRecipeImage(recipeId, imageData)
            Result.Success(imageUrl)
        } catch (e: Exception) {
            Result.Error(e)
        }
}
