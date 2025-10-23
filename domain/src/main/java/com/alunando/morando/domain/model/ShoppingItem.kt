package com.alunando.morando.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Modelo de domínio para Item de Lista de Compras
 */
@Parcelize
data class ShoppingItem(
    val id: String = "",
    val produtoId: String? = null,
    val nome: String,
    val quantidade: Int = 1,
    val comprado: Boolean = false,
    val userId: String = "",
    val createdAt: Date = Date(),
) : Parcelable
