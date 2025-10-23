package com.alunando.morando.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de resposta da API Cosmos
 */
@JsonClass(generateAdapter = true)
data class CosmosResponse(
    @Json(name = "description")
    val description: String?,
    @Json(name = "gtin")
    val gtin: String?,
    @Json(name = "brand")
    val brand: String?,
    @Json(name = "thumbnail")
    val thumbnail: String?,
    @Json(name = "avg_price")
    val avgPrice: Double?,
    @Json(name = "ncm")
    val ncm: CosmosNcm?,
)

@JsonClass(generateAdapter = true)
data class CosmosNcm(
    @Json(name = "code")
    val code: String?,
    @Json(name = "description")
    val description: String?,
)
