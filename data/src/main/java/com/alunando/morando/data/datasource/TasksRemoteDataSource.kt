package com.alunando.morando.data.datasource

import com.alunando.morando.data.firebase.AuthManager
import com.alunando.morando.data.firebase.FirebaseConfig
import com.alunando.morando.domain.model.RecurrenceType
import com.alunando.morando.domain.model.Task
import com.alunando.morando.domain.model.TaskType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

/**
 * Data source remoto para tarefas (Firestore)
 */
class TasksRemoteDataSource(
    private val firestore: FirebaseFirestore,
    private val authManager: AuthManager,
) {
    private fun getUserTasksCollection() =
        authManager.currentUserId.let { userId ->
            check(userId.isNotEmpty()) { "Usuário não autenticado" }
            firestore
                .collection(FirebaseConfig.COLLECTION_USERS)
                .document(userId)
                .collection(FirebaseConfig.COLLECTION_TASKS)
        }

    /**
     * Busca todas as tarefas do usuário
     */
    fun getTasks(): Flow<List<Task>> =
        callbackFlow {
            // Verifica se está autenticado, senão retorna lista vazia
            if (authManager.currentUserId.isEmpty()) {
                trySend(emptyList())
                awaitClose { }
                return@callbackFlow
            }

            val listener =
                getUserTasksCollection()
                    .orderBy(FirebaseConfig.FIELD_CREATED_AT, Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val tasks =
                            snapshot?.documents?.mapNotNull { doc ->
                                doc.toTask()
                            } ?: emptyList()

                        trySend(tasks)
                    }

            awaitClose { listener.remove() }
        }

    /**
     * Busca tarefas por tipo
     */
    fun getTasksByType(type: TaskType): Flow<List<Task>> =
        callbackFlow {
            // Verifica se está autenticado, senão retorna lista vazia
            if (authManager.currentUserId.isEmpty()) {
                trySend(emptyList())
                awaitClose { }
                return@callbackFlow
            }

            val listener =
                getUserTasksCollection()
                    .whereEqualTo(FirebaseConfig.FIELD_TIPO, type.name.lowercase())
                    .orderBy(FirebaseConfig.FIELD_CREATED_AT, Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val tasks =
                            snapshot?.documents?.mapNotNull { doc ->
                                doc.toTask()
                            } ?: emptyList()

                        trySend(tasks)
                    }

            awaitClose { listener.remove() }
        }

    /**
     * Busca tarefas para uma data específica, incluindo recorrentes
     */
    fun getTasksForDate(date: Date): Flow<List<Task>> =
        callbackFlow {
            // Verifica se está autenticado, senão retorna lista vazia
            if (authManager.currentUserId.isEmpty()) {
                trySend(emptyList())
                awaitClose { }
                return@callbackFlow
            }

            val listener =
                getUserTasksCollection()
                    .whereEqualTo("parentTaskId", null)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val allTasks =
                            snapshot?.documents?.mapNotNull { doc ->
                                doc.toTask()
                            } ?: emptyList()

                        // Filtra tarefas para a data
                        val tasksForDate =
                            allTasks.filter { task ->
                                shouldShowTaskOnDate(task, date)
                            }

                        trySend(tasksForDate)
                    }

            awaitClose { listener.remove() }
        }

    /**
     * Busca sub-tarefas de um compromisso
     */
    fun getSubTasks(parentTaskId: String): Flow<List<Task>> =
        callbackFlow {
            // Verifica se está autenticado, senão retorna lista vazia
            if (authManager.currentUserId.isEmpty()) {
                trySend(emptyList())
                awaitClose { }
                return@callbackFlow
            }

            val listener =
                getUserTasksCollection()
                    .whereEqualTo("parentTaskId", parentTaskId)
                    .orderBy(FirebaseConfig.FIELD_CREATED_AT, Query.Direction.ASCENDING)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            close(error)
                            return@addSnapshotListener
                        }

                        val subTasks =
                            snapshot?.documents?.mapNotNull { doc ->
                                doc.toTask()
                            } ?: emptyList()

                        trySend(subTasks)
                    }

            awaitClose { listener.remove() }
        }

    /**
     * Busca tarefa por ID
     */
    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    suspend fun getTaskById(taskId: String): Task? =
        try {
            val doc =
                getUserTasksCollection()
                    .document(taskId)
                    .get()
                    .await()
            doc.toTask()
        } catch (e: Exception) {
            null
        }

    /**
     * Adiciona nova tarefa
     */
    suspend fun addTask(task: Task): Task {
        // Aguarda autenticação antes de tentar adicionar
        authManager.waitForAuthentication()

        val docRef = getUserTasksCollection().document()
        val taskMap = task.toMap(authManager.currentUserId)
        docRef.set(taskMap).await()
        return task.copy(id = docRef.id)
    }

    /**
     * Atualiza tarefa
     */
    suspend fun updateTask(task: Task) {
        // Aguarda autenticação antes de tentar atualizar
        authManager.waitForAuthentication()

        getUserTasksCollection()
            .document(task.id)
            .set(task.toMap(authManager.currentUserId))
            .await()
    }

    /**
     * Deleta tarefa
     */
    suspend fun deleteTask(taskId: String) {
        // Aguarda autenticação antes de tentar deletar
        authManager.waitForAuthentication()

        getUserTasksCollection()
            .document(taskId)
            .delete()
            .await()
    }

    /**
     * Marca tarefa como completa/incompleta
     */
    suspend fun markTaskComplete(
        taskId: String,
        complete: Boolean,
    ) {
        // Aguarda autenticação antes de tentar atualizar
        authManager.waitForAuthentication()

        getUserTasksCollection()
            .document(taskId)
            .update(FirebaseConfig.FIELD_COMPLETA, complete)
            .await()
    }

    /**
     * Verifica se uma tarefa deve ser exibida em uma data específica
     */
    private fun shouldShowTaskOnDate(
        task: Task,
        date: Date,
    ): Boolean {
        val taskDate = task.scheduledDate ?: return false
        val targetCal = Calendar.getInstance().apply { time = date }
        val taskCal = Calendar.getInstance().apply { time = taskDate }

        return when (task.recurrence) {
            RecurrenceType.NONE -> {
                // Tarefa sem recorrência: mostra apenas na data agendada
                isSameDay(taskCal, targetCal)
            }
            RecurrenceType.DAILY -> {
                // Tarefa diária: mostra se a data alvo >= data inicial
                !targetCal.before(taskCal)
            }
            RecurrenceType.WEEKLY -> {
                // Tarefa semanal: mostra se é o mesmo dia da semana e data alvo >= data inicial
                !targetCal.before(taskCal) &&
                    taskCal.get(Calendar.DAY_OF_WEEK) == targetCal.get(Calendar.DAY_OF_WEEK)
            }
            RecurrenceType.MONTHLY -> {
                // Tarefa mensal: mostra se é o mesmo dia do mês e data alvo >= data inicial
                !targetCal.before(taskCal) &&
                    taskCal.get(Calendar.DAY_OF_MONTH) == targetCal.get(Calendar.DAY_OF_MONTH)
            }
        }
    }

    private fun isSameDay(
        cal1: Calendar,
        cal2: Calendar,
    ): Boolean =
        cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)

    // Extension functions
    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun com.google.firebase.firestore.DocumentSnapshot.toTask(): Task? =
        try {
            Task(
                id = id,
                titulo = getString("titulo") ?: "",
                descricao = getString("descricao") ?: "",
                tipo = TaskType.fromString(getString("tipo") ?: "normal"),
                recurrence = RecurrenceType.fromString(getString("recurrence") ?: "none"),
                completa = getBoolean("completa") ?: false,
                userId = getString("userId") ?: "",
                createdAt = getTimestamp("createdAt")?.toDate() ?: Date(),
                parentTaskId = getString("parentTaskId"),
                scheduledDate = getTimestamp("scheduledDate")?.toDate(),
            )
        } catch (e: Exception) {
            null
        }

    private fun Task.toMap(userId: String): Map<String, Any?> =
        mapOf(
            "titulo" to titulo,
            "descricao" to descricao,
            "tipo" to tipo.name.lowercase(),
            "recurrence" to recurrence.name.lowercase(),
            "completa" to completa,
            "userId" to userId,
            "createdAt" to com.google.firebase.Timestamp.now(),
            "parentTaskId" to parentTaskId,
            "scheduledDate" to
                scheduledDate?.let {
                    com.google.firebase.Timestamp(it)
                },
        )
}
