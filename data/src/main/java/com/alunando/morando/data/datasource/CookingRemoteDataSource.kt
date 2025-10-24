package com.alunando.morando.data.datasource

import com.alunando.morando.data.firebase.AuthManager
import com.alunando.morando.data.firebase.FirebaseConfig
import com.alunando.morando.domain.model.CookingStep
import com.alunando.morando.domain.model.Ingredient
import com.alunando.morando.domain.model.MiseEnPlaceStep
import com.alunando.morando.domain.model.Recipe
import com.alunando.morando.domain.model.RecipeCategory
import com.alunando.morando.domain.model.RecipeDifficulty
import com.alunando.morando.domain.model.StoveType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

/**
 * Data source remoto para receitas (Firestore + Storage)
 */
@Suppress("TooManyFunctions")
class CookingRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val authManager: AuthManager,
) {
    private fun getUserRecipesCollection() =
        authManager.currentUserId.let { userId ->
            check(userId.isNotEmpty()) { "Usuário não autenticado" }
            firestore
                .collection(FirebaseConfig.COLLECTION_USERS)
                .document(userId)
                .collection(FirebaseConfig.COLLECTION_RECIPES)
        }

    private fun getUserPreferencesCollection() =
        authManager.currentUserId.let { userId ->
            check(userId.isNotEmpty()) { "Usuário não autenticado" }
            firestore
                .collection(FirebaseConfig.COLLECTION_USERS)
                .document(userId)
                .collection(FirebaseConfig.COLLECTION_PREFERENCES)
        }

    /**
     * Busca todas as receitas do usuário
     */
    fun getRecipes(): Flow<List<Recipe>> =
        callbackFlow {
            // Se não estiver autenticado, retorna lista vazia
            if (authManager.currentUserId.isEmpty()) {
                trySend(emptyList())
                awaitClose { }
                return@callbackFlow
            }

            // Usuário autenticado, configura listener
            val listener =
                getUserRecipesCollection()
                    .orderBy(FirebaseConfig.FIELD_CREATED_AT, Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val recipes =
                            snapshot?.documents?.mapNotNull { it.toRecipe() } ?: emptyList()
                        trySend(recipes)
                    }

            awaitClose { listener.remove() }
        }

    /**
     * Busca receitas por categoria
     */
    fun getRecipesByCategory(category: RecipeCategory): Flow<List<Recipe>> =
        callbackFlow {
            if (authManager.currentUserId.isEmpty()) {
                trySend(emptyList())
                awaitClose { }
                return@callbackFlow
            }

            val listener =
                getUserRecipesCollection()
                    .whereEqualTo(FirebaseConfig.FIELD_CATEGORIA, category.name)
                    .orderBy(FirebaseConfig.FIELD_CREATED_AT, Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val recipes =
                            snapshot?.documents?.mapNotNull { it.toRecipe() } ?: emptyList()
                        trySend(recipes)
                    }

            awaitClose { listener.remove() }
        }

    /**
     * Busca receita por ID
     */
    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    suspend fun getRecipeById(id: String): Recipe? =
        try {
            val snapshot =
                getUserRecipesCollection()
                    .document(id)
                    .get()
                    .await()
            snapshot.toRecipe()
        } catch (e: Exception) {
            null
        }

    /**
     * Adiciona nova receita
     */
    suspend fun addRecipe(recipe: Recipe): Recipe {
        val userId = authManager.waitForAuthentication()

        val docRef = getUserRecipesCollection().document()
        val recipeMap = recipe.toMap(authManager.currentUserId)
        docRef.set(recipeMap).await()
        return recipe.copy(id = docRef.id, userId = userId)
    }

    /**
     * Atualiza receita
     */
    suspend fun updateRecipe(recipe: Recipe) {
        authManager.waitForAuthentication()

        getUserRecipesCollection()
            .document(recipe.id)
            .set(recipe.toMap(authManager.currentUserId))
            .await()
    }

    /**
     * Deleta receita
     */
    suspend fun deleteRecipe(id: String) {
        authManager.waitForAuthentication()

        getUserRecipesCollection()
            .document(id)
            .delete()
            .await()
    }

    /**
     * Upload de imagem da receita
     */
    suspend fun uploadRecipeImage(
        recipeId: String,
        imageData: ByteArray,
    ): String {
        val userId = authManager.waitForAuthentication()

        val fileName = "${UUID.randomUUID()}.jpg"
        val path =
            "${FirebaseConfig.STORAGE_RECIPES}/$userId/$recipeId/${FirebaseConfig.STORAGE_IMAGES}/$fileName"
        val storageRef = storage.reference.child(path)

        storageRef.putBytes(imageData).await()
        return storageRef.downloadUrl.await().toString()
    }

    /**
     * Obtém preferência de tipo de fogão do usuário
     */
    suspend fun getUserStovePreference(): StoveType =
        try {
            val snapshot =
                getUserPreferencesCollection()
                    .document("cooking")
                    .get()
                    .await()

            val stoveTypeStr = snapshot.getString(FirebaseConfig.FIELD_STOVE_TYPE)
            StoveType.fromString(stoveTypeStr ?: "induction")
        } catch (e: Exception) {
            StoveType.INDUCTION // default
        }

    /**
     * Salva preferência de tipo de fogão do usuário
     */
    suspend fun saveUserStovePreference(stoveType: StoveType) {
        authManager.waitForAuthentication()

        getUserPreferencesCollection()
            .document("cooking")
            .set(mapOf(FirebaseConfig.FIELD_STOVE_TYPE to stoveType.name.lowercase()))
            .await()
    }

    // Extension functions
    @Suppress("TooGenericExceptionCaught", "SwallowedException", "CyclomaticComplexMethod", "LongMethod")
    private fun com.google.firebase.firestore.DocumentSnapshot.toRecipe(): Recipe? =
        try {
            Recipe(
                id = id,
                nome = getString(FirebaseConfig.FIELD_NOME) ?: "",
                descricao = getString(FirebaseConfig.FIELD_DESCRICAO) ?: "",
                categoria =
                    RecipeCategory.fromString(
                        getString(FirebaseConfig.FIELD_CATEGORIA) ?: "prato_principal",
                    ),
                tempoPreparo = getLong(FirebaseConfig.FIELD_TEMPO_PREPARO)?.toInt() ?: 0,
                porcoes = getLong(FirebaseConfig.FIELD_PORCOES)?.toInt() ?: 1,
                dificuldade =
                    RecipeDifficulty.fromString(
                        getString(FirebaseConfig.FIELD_DIFICULDADE) ?: "media",
                    ),
                fotoUrl = getString(FirebaseConfig.FIELD_FOTO_URL) ?: "",
                ingredientes = getIngredientes(),
                etapasMiseEnPlace = getEtapasMiseEnPlace(),
                etapasPreparo = getEtapasPreparo(),
                tipoFogaoPadrao =
                    StoveType.fromString(
                        getString(FirebaseConfig.FIELD_TIPO_FOGAO_PADRAO) ?: "induction",
                    ),
                userId = getString(FirebaseConfig.FIELD_USER_ID) ?: "",
                createdAt = getTimestamp(FirebaseConfig.FIELD_CREATED_AT)?.toDate() ?: Date(),
            )
        } catch (e: Exception) {
            null
        }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun com.google.firebase.firestore.DocumentSnapshot.getIngredientes(): List<Ingredient> =
        try {
            @Suppress("UNCHECKED_CAST")
            val list = get(FirebaseConfig.FIELD_INGREDIENTES) as? List<Map<String, Any>> ?: emptyList()
            list.map { map ->
                Ingredient(
                    nome = map["nome"] as? String ?: "",
                    quantidade = (map["quantidade"] as? Number)?.toDouble() ?: 0.0,
                    unidade = map["unidade"] as? String ?: "",
                    observacoes = map["observacoes"] as? String ?: "",
                    productId = map["productId"] as? String,
                )
            }
        } catch (e: Exception) {
            emptyList()
        }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun com.google.firebase.firestore.DocumentSnapshot.getEtapasMiseEnPlace(): List<MiseEnPlaceStep> =
        try {
            @Suppress("UNCHECKED_CAST")
            val list =
                get(FirebaseConfig.FIELD_ETAPAS_MISE_EN_PLACE) as? List<Map<String, Any>> ?: emptyList()
            list.map { map ->
                MiseEnPlaceStep(
                    ordem = (map["ordem"] as? Number)?.toInt() ?: 0,
                    ingrediente = map["ingrediente"] as? String ?: "",
                    quantidade = map["quantidade"] as? String ?: "",
                    instrucao = map["instrucao"] as? String ?: "",
                    tipoDePreparo = map["tipoDePreparo"] as? String ?: "",
                )
            }
        } catch (e: Exception) {
            emptyList()
        }

    @Suppress("TooGenericExceptionCaught", "SwallowedException", "LongMethod")
    private fun com.google.firebase.firestore.DocumentSnapshot.getEtapasPreparo(): List<CookingStep> =
        try {
            @Suppress("UNCHECKED_CAST")
            val list = get(FirebaseConfig.FIELD_ETAPAS_PREPARO) as? List<Map<String, Any>> ?: emptyList()
            list.map { map ->
                CookingStep(
                    ordem = (map["ordem"] as? Number)?.toInt() ?: 0,
                    titulo = map["titulo"] as? String ?: "",
                    tempoMinutos = (map["tempoMinutos"] as? Number)?.toInt() ?: 0,
                    instrucoesGerais = map["instrucoesGerais"] as? String ?: "",
                    instrucaoInducao = map["instrucaoInducao"] as? String ?: "",
                    instrucaoGas = map["instrucaoGas"] as? String ?: "",
                    instrucaoEletrico = map["instrucaoEletrico"] as? String ?: "",
                    instrucaoLenha = map["instrucaoLenha"] as? String ?: "",
                )
            }
        } catch (e: Exception) {
            emptyList()
        }

    @Suppress("LongMethod")
    private fun Recipe.toMap(userId: String): Map<String, Any?> =
        mapOf(
            FirebaseConfig.FIELD_NOME to nome,
            FirebaseConfig.FIELD_DESCRICAO to descricao,
            FirebaseConfig.FIELD_CATEGORIA to categoria.name,
            FirebaseConfig.FIELD_TEMPO_PREPARO to tempoPreparo,
            FirebaseConfig.FIELD_PORCOES to porcoes,
            FirebaseConfig.FIELD_DIFICULDADE to dificuldade.name,
            FirebaseConfig.FIELD_FOTO_URL to fotoUrl,
            FirebaseConfig.FIELD_INGREDIENTES to
                ingredientes.map { ingredient ->
                    mapOf(
                        "nome" to ingredient.nome,
                        "quantidade" to ingredient.quantidade,
                        "unidade" to ingredient.unidade,
                        "observacoes" to ingredient.observacoes,
                        "productId" to ingredient.productId,
                    )
                },
            FirebaseConfig.FIELD_ETAPAS_MISE_EN_PLACE to
                etapasMiseEnPlace.map { step ->
                    mapOf(
                        "ordem" to step.ordem,
                        "ingrediente" to step.ingrediente,
                        "quantidade" to step.quantidade,
                        "instrucao" to step.instrucao,
                        "tipoDePreparo" to step.tipoDePreparo,
                    )
                },
            FirebaseConfig.FIELD_ETAPAS_PREPARO to
                etapasPreparo.map { step ->
                    mapOf(
                        "ordem" to step.ordem,
                        "titulo" to step.titulo,
                        "tempoMinutos" to step.tempoMinutos,
                        "instrucoesGerais" to step.instrucoesGerais,
                        "instrucaoInducao" to step.instrucaoInducao,
                        "instrucaoGas" to step.instrucaoGas,
                        "instrucaoEletrico" to step.instrucaoEletrico,
                        "instrucaoLenha" to step.instrucaoLenha,
                    )
                },
            FirebaseConfig.FIELD_TIPO_FOGAO_PADRAO to tipoFogaoPadrao.name,
            FirebaseConfig.FIELD_USER_ID to userId,
            FirebaseConfig.FIELD_CREATED_AT to
                com.google.firebase.Timestamp
                    .now(),
        )
}
