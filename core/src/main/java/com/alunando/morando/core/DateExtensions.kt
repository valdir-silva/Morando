package com.alunando.morando.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Formata data para string no padrão dd/MM/yyyy
 */
fun Date.toFormattedString(): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return format.format(this)
}

/**
 * Formata data e hora para string no padrão dd/MM/yyyy HH:mm
 */
fun Date.toFormattedDateTimeString(): String {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
    return format.format(this)
}

/**
 * Calcula diferença em dias entre duas datas
 */
fun Date.daysBetween(other: Date): Long {
    val diff = other.time - this.time
    return TimeUnit.MILLISECONDS.toDays(diff)
}

/**
 * Adiciona dias à data
 */
fun Date.plusDays(days: Int): Date {
    return Date(this.time + TimeUnit.DAYS.toMillis(days.toLong()))
}

/**
 * Verifica se a data já passou
 */
fun Date.isPast(): Boolean {
    return this.before(Date())
}

/**
 * Verifica se a data está próxima (dentro de X dias)
 */
fun Date.isWithinDays(days: Int): Boolean {
    val now = Date()
    val future = now.plusDays(days)
    return this.after(now) && this.before(future)
}

