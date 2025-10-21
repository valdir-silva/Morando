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
    private val firebaseAuth: FirebaseAuth
) {

    /**
     * Flow do usuário atual
     */
    val currentUserFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
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
    suspend fun signInAnonymously(): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInAnonymously().await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Erro ao fazer login anônimo"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Faz login com email e senha
     */
    @Suppress("TooGenericExceptionCaught")
    suspend fun signInWithEmailPassword(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Erro ao fazer login"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra novo usuário
     */
    @Suppress("TooGenericExceptionCaught")
    suspend fun registerWithEmailPassword(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let {
                Result.success(it)
            } ?: Result.failure(Exception("Erro ao registrar usuário"))
        } catch (e: Exception) {
            Result.failure(e)
        }
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
    suspend fun ensureAuthenticated(): Result<FirebaseUser> {
        return currentUser?.let { Result.success(it) } ?: signInAnonymously()
    }
}
