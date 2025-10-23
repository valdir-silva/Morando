package com.alunando.morando.data.datasource

import com.alunando.morando.data.firebase.AuthManager
import com.alunando.morando.data.firebase.FirebaseConfig
import com.alunando.morando.domain.model.Product
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
 * Data source remoto para produtos (Firestore + Storage)
 */
@Suppress("TooManyFunctions")
class InventoryRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val authManager: AuthManager,
    private val productApiDataSource: com.alunando.morando.data.api.ProductApiDataSource? = null,
) {
    private fun getUserProductsCollection() =
        authManager.currentUserId.let { userId ->
            check(userId.isNotEmpty()) { "Usuário não autenticado" }
            firestore
                .collection(FirebaseConfig.COLLECTION_USERS)
                .document(userId)
                .collection(FirebaseConfig.COLLECTION_PRODUCTS)
        }

    /**
     * Busca todos os produtos do usuário
     */
    @Suppress("MagicNumber")
    fun getProducts(): Flow<List<Product>> =
        callbackFlow {
            // Aguarda até que o usuário esteja autenticado
            while (authManager.currentUserId.isEmpty()) {
                kotlinx.coroutines.delay(100)
            }

            val listener =
                getUserProductsCollection()
                    .orderBy(FirebaseConfig.FIELD_CREATED_AT, Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val products =
                            snapshot?.documents?.mapNotNull { doc ->
                                doc.toProduct()
                            } ?: emptyList()

                        trySend(products)
                    }

            awaitClose { listener.remove() }
        }

    /**
     * Busca produtos por categoria
     */
    @Suppress("MagicNumber")
    fun getProductsByCategory(category: String): Flow<List<Product>> =
        callbackFlow {
            // Aguarda até que o usuário esteja autenticado
            while (authManager.currentUserId.isEmpty()) {
                kotlinx.coroutines.delay(100)
            }

            val listener =
                getUserProductsCollection()
                    .whereEqualTo(FirebaseConfig.FIELD_CATEGORIA, category)
                    .orderBy(FirebaseConfig.FIELD_CREATED_AT, Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val products =
                            snapshot?.documents?.mapNotNull { doc ->
                                doc.toProduct()
                            } ?: emptyList()

                        trySend(products)
                    }

            awaitClose { listener.remove() }
        }

    /**
     * Busca produtos que estão acabando (vencimento próximo ou vencidos)
     */
    @Suppress("MagicNumber")
    fun getProductsNeedingReplenishment(): Flow<List<Product>> =
        callbackFlow {
            // Aguarda até que o usuário esteja autenticado
            while (authManager.currentUserId.isEmpty()) {
                kotlinx.coroutines.delay(100)
            }

            val listener =
                getUserProductsCollection()
                    .orderBy(FirebaseConfig.FIELD_DATA_VENCIMENTO, Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val products =
                            snapshot?.documents?.mapNotNull { doc ->
                                doc.toProduct()
                            }?.filter { it.isProximoVencimento() || it.isVencido() } ?: emptyList()

                        trySend(products)
                    }

            awaitClose { listener.remove() }
        }

    /**
     * Busca produto por ID
     */
    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    suspend fun getProductById(productId: String): Product? =
        try {
            val doc =
                getUserProductsCollection()
                    .document(productId)
                    .get()
                    .await()
            doc.toProduct()
        } catch (e: Exception) {
            null
        }

    /**
     * Busca produto por código de barras
     */
    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    suspend fun getProductByBarcode(barcode: String): Product? =
        try {
            val snapshot =
                getUserProductsCollection()
                    .whereEqualTo(FirebaseConfig.FIELD_CODIGO_BARRAS, barcode)
                    .limit(1)
                    .get()
                    .await()
            snapshot.documents.firstOrNull()?.toProduct()
        } catch (e: Exception) {
            null
        }

    /**
     * Adiciona novo produto
     */
    suspend fun addProduct(product: Product): Product {
        val docRef = getUserProductsCollection().document()
        val productMap = product.toMap(authManager.currentUserId)
        docRef.set(productMap).await()
        return product.copy(id = docRef.id)
    }

    /**
     * Atualiza produto
     */
    suspend fun updateProduct(product: Product) {
        getUserProductsCollection()
            .document(product.id)
            .set(product.toMap(authManager.currentUserId))
            .await()
    }

    /**
     * Deleta produto
     */
    suspend fun deleteProduct(productId: String) {
        getUserProductsCollection()
            .document(productId)
            .delete()
            .await()
    }

    /**
     * Upload de imagem do produto
     */
    suspend fun uploadProductImage(
        productId: String,
        imageData: ByteArray,
    ): String {
        val userId = authManager.currentUserId
        check(userId.isNotEmpty()) { "Usuário não autenticado" }

        val fileName = "${UUID.randomUUID()}.jpg"
        val path =
            "${FirebaseConfig.STORAGE_PRODUCTS}/$userId/$productId/${FirebaseConfig.STORAGE_IMAGES}/$fileName"
        val storageRef = storage.reference.child(path)

        storageRef.putBytes(imageData).await()
        return storageRef.downloadUrl.await().toString()
    }

    /**
     * Busca informações do produto em API externa
     */
    suspend fun searchProductByBarcode(barcode: String): Product? =
        productApiDataSource?.searchProductByBarcode(barcode)

    // Extension functions
    @Suppress("TooGenericExceptionCaught", "SwallowedException", "CyclomaticComplexMethod")
    private fun com.google.firebase.firestore.DocumentSnapshot.toProduct(): Product? =
        try {
            Product(
                id = id,
                nome = getString(FirebaseConfig.FIELD_NOME) ?: "",
                categoria = getString(FirebaseConfig.FIELD_CATEGORIA) ?: "",
                codigoBarras = getString(FirebaseConfig.FIELD_CODIGO_BARRAS) ?: "",
                fotoUrl = getString(FirebaseConfig.FIELD_FOTO_URL) ?: "",
                dataCompra = getTimestamp(FirebaseConfig.FIELD_DATA_COMPRA)?.toDate(),
                valor = getDouble(FirebaseConfig.FIELD_VALOR) ?: 0.0,
                detalhes = getString(FirebaseConfig.FIELD_DETALHES) ?: "",
                dataVencimento = getTimestamp(FirebaseConfig.FIELD_DATA_VENCIMENTO)?.toDate(),
                userId = getString(FirebaseConfig.FIELD_USER_ID) ?: "",
                createdAt = getTimestamp(FirebaseConfig.FIELD_CREATED_AT)?.toDate() ?: Date(),
            )
        } catch (e: Exception) {
            null
        }

    private fun Product.toMap(userId: String): Map<String, Any?> =
        mapOf(
            FirebaseConfig.FIELD_NOME to nome,
            FirebaseConfig.FIELD_CATEGORIA to categoria,
            FirebaseConfig.FIELD_CODIGO_BARRAS to codigoBarras,
            FirebaseConfig.FIELD_FOTO_URL to fotoUrl,
            FirebaseConfig.FIELD_DATA_COMPRA to dataCompra?.let { com.google.firebase.Timestamp(it) },
            FirebaseConfig.FIELD_VALOR to valor,
            FirebaseConfig.FIELD_DETALHES to detalhes,
            FirebaseConfig.FIELD_DATA_VENCIMENTO to dataVencimento?.let { com.google.firebase.Timestamp(it) },
            FirebaseConfig.FIELD_USER_ID to userId,
            FirebaseConfig.FIELD_CREATED_AT to
                com.google.firebase.Timestamp
                    .now(),
        )
}
