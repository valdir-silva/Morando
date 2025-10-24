package com.alunando.morando.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Modelo de domínio para Tarefa
 */
@Parcelize
data class Task(
    val id: String = "",
    val titulo: String,
    val descricao: String = "",
    val tipo: TaskType,
    val recurrence: RecurrenceType = RecurrenceType.NONE,
    val completa: Boolean = false,
    val userId: String = "",
    val createdAt: Date = Date(),
    val parentTaskId: String? = null,
    val scheduledDate: Date? = null,
) : Parcelable

/**
 * Tipo de tarefa
 */
enum class TaskType {
    NORMAL,
    COMMITMENT,
    ;

    companion object {
        fun fromString(value: String): TaskType =
            when (value.lowercase()) {
                "normal" -> NORMAL
                "commitment", "compromisso" -> COMMITMENT
                else -> NORMAL
            }
    }
}

/**
 * Tipo de recorrência da tarefa
 */
enum class RecurrenceType {
    NONE,
    DAILY,
    WEEKLY,
    MONTHLY,
    ;

    companion object {
        fun fromString(value: String): RecurrenceType =
            when (value.lowercase()) {
                "none", "nenhuma" -> NONE
                "daily", "diaria" -> DAILY
                "weekly", "semanal" -> WEEKLY
                "monthly", "mensal" -> MONTHLY
                else -> NONE
            }
    }
}
