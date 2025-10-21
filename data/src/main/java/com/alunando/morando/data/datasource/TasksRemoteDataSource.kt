package com.alunando.morando.data.datasource

import com.alunando.morando.data.firebase.AuthManager
import com.alunando.morando.data.firebase.FirebaseConfig
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Data source remoto para tarefas (Firestore)
 */
class TasksRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val authManager: AuthManager
) {
    
    private fun getUserTasksCollection() = authManager.currentUserId.let { userId ->
        if (userId.isEmpty()) {
            throw IllegalStateException("Usuário não autenticado")
        }
        firestore
            .collection(FirebaseConfig.COLLECTION_USERS)
            .document(userId)
            .collection(FirebaseConfig.COLLECTION_TASKS)
    }

    /**
     * Busca todas as tarefas do usuário
     */
    fun getTasks(): Flow<List<Task>> = callbackFlow {
        // Aguarda até que o usuário esteja autenticado
        while (authManager.currentUserId.isEmpty()) {
            kotlinx.coroutines.delay(100)
        }
        
        val listener = getUserTasksCollection()
            .orderBy(FirebaseConfig.FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    doc.toTask()
                } ?: emptyList()

                trySend(tasks)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Busca tarefas por tipo
     */
    fun getTasksByType(type: TaskType): Flow<List<Task>> = callbackFlow {
        // Aguarda até que o usuário esteja autenticado
        while (authManager.currentUserId.isEmpty()) {
            kotlinx.coroutines.delay(100)
        }
        
        val listener = getUserTasksCollection()
            .whereEqualTo(FirebaseConfig.FIELD_TIPO, type.name.lowercase())
            .orderBy(FirebaseConfig.FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    doc.toTask()
                } ?: emptyList()

                trySend(tasks)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Busca tarefa por ID
     */
    suspend fun getTaskById(taskId: String): Task? {
        return try {
            val doc = getUserTasksCollection()
                .document(taskId)
                .get()
                .await()
            doc.toTask()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Adiciona nova tarefa
     */
    suspend fun addTask(task: Task): Task {
        val docRef = getUserTasksCollection().document()
        val taskMap = task.toMap(authManager.currentUserId)
        docRef.set(taskMap).await()
        return task.copy(id = docRef.id)
    }

    /**
     * Atualiza tarefa
     */
    suspend fun updateTask(task: Task) {
        getUserTasksCollection()
            .document(task.id)
            .set(task.toMap(authManager.currentUserId))
            .await()
    }

    /**
     * Deleta tarefa
     */
    suspend fun deleteTask(taskId: String) {
        getUserTasksCollection()
            .document(taskId)
            .delete()
            .await()
    }

    /**
     * Marca tarefa como completa/incompleta
     */
    suspend fun markTaskComplete(taskId: String, complete: Boolean) {
        getUserTasksCollection()
            .document(taskId)
            .update(FirebaseConfig.FIELD_COMPLETA, complete)
            .await()
    }

    // Extension functions
    private fun com.google.firebase.firestore.DocumentSnapshot.toTask(): Task? {
        return try {
            Task(
                id = id,
                titulo = getString("titulo") ?: "",
                descricao = getString("descricao") ?: "",
                tipo = TaskType.fromString(getString("tipo") ?: "diaria"),
                completa = getBoolean("completa") ?: false,
                userId = getString("userId") ?: "",
                createdAt = getTimestamp("createdAt")?.toDate() ?: Date()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun Task.toMap(userId: String): Map<String, Any?> {
        return mapOf(
            "titulo" to titulo,
            "descricao" to descricao,
            "tipo" to tipo.name.lowercase(),
            "completa" to completa,
            "userId" to userId,
            "createdAt" to com.google.firebase.Timestamp.now()
        )
    }
}

