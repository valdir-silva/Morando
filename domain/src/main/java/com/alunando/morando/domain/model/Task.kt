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
    val completa: Boolean = false,
    val userId: String = "",
    val createdAt: Date = Date()
) : Parcelable

/**
 * Tipo de tarefa
 */
enum class TaskType {
    DIARIA,
    SEMANAL;

    companion object {
        fun fromString(value: String): TaskType {
            return when (value.lowercase()) {
                "diaria" -> DIARIA
                "semanal" -> SEMANAL
                else -> DIARIA
            }
        }
    }
}

