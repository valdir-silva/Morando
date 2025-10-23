package com.alunando.morando.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Gerenciador de autenticação Firebase
 */
class AuthManager(
    private val firebaseAuth: FirebaseAuth,
) {
    /**
     * Flow do usuário atual
     */
    val currentUserFlow: Flow<FirebaseUser?> =
        callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    trySend(auth.currentUser)
                }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose { firebaseAuth.removeAuthStateListener(listener) }
        }

    /**
     * Usuário atual (snapshot)
     */
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /**
     * ID do usuário atual
     */
    val currentUserId: String
        get() = currentUser?.uid ?: ""

    /**
     * Verifica se o usuário está autenticado
     */
    val isAuthenticated: Boolean
        get() = currentUser != null

    /**
     * Faz login anônimo
     */
    @Suppress("TooGenericExceptionCaught")
    suspend fun signInAnonymously(): Result<FirebaseUser> =
        try {
            val result = firebaseAuth.signInAnonymously().await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Erro ao fazer login anônimo"))
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Faz login com email e senha
     */
    @Suppress("TooGenericExceptionCaught")
    suspend fun signInWithEmailPassword(
        email: String,
        password: String,
    ): Result<FirebaseUser> =
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Erro ao fazer login"))
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Registra novo usuário
     */
    @Suppress("TooGenericExceptionCaught")
    suspend fun registerWithEmailPassword(
        email: String,
        password: String,
    ): Result<FirebaseUser> =
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Erro ao registrar usuário"))
        } catch (e: Exception) {
            Result.failure(e)
        }

    /**
     * Faz logout
     */
    fun signOut() {
        firebaseAuth.signOut()
    }

    /**
     * Garante que há usuário autenticado (cria anônimo se necessário)
     */
    suspend fun ensureAuthenticated(): Result<FirebaseUser> =
        currentUser?.let { Result.success(it) } ?: signInAnonymously()

    /**
     * Aguarda até que o usuário esteja autenticado (com timeout)
     * Retorna o user ID ou lança exceção se não autenticar
     */
    @Suppress("MagicNumber")
    suspend fun waitForAuthentication(timeoutMillis: Long = 5000): String {
        android.util.Log.d("AuthManager", "Aguardando autenticação... (userId atual: '$currentUserId')")

        var elapsed = 0L
        val interval = 100L

        while (currentUserId.isEmpty() && elapsed < timeoutMillis) {
            kotlinx.coroutines.delay(interval)
            elapsed += interval

            if (elapsed % 1000 == 0L) {
                android.util.Log.d("AuthManager", "Aguardando... ${elapsed}ms de ${timeoutMillis}ms")
            }
        }

        if (currentUserId.isEmpty()) {
            android.util.Log.e("AuthManager", "❌ Timeout: Usuário não autenticado após ${timeoutMillis}ms")
            throw IllegalStateException("Usuário não autenticado após ${timeoutMillis}ms")
        }

        android.util.Log.d("AuthManager", "✅ Usuário autenticado: $currentUserId")
        return currentUserId
    }
}
