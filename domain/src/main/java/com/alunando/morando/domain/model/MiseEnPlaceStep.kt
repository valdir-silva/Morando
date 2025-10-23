package com.alunando.morando.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Etapa de mise en place (preparação prévia)
 */
@Parcelize
data class MiseEnPlaceStep(
    val ordem: Int,
    val ingrediente: String,
    val quantidade: String,
    val instrucao: String,
    val tipoDePreparo: String = "",
) : Parcelable
