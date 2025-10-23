package com.alunando.morando.data.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Modelo de resposta da API OpenFoodFacts
 */
@JsonClass(generateAdapter = true)
data class OpenFoodFactsResponse(
    @Json(name = "status")
    val status: Int?,
    @Json(name = "product")
    val product: OpenFoodFactsProduct?,
)

@JsonClass(generateAdapter = true)
data class OpenFoodFactsProduct(
    @Json(name = "product_name")
    val productName: String?,
    @Json(name = "brands")
    val brands: String?,
    @Json(name = "image_url")
    val imageUrl: String?,
    @Json(name = "categories")
    val categories: String?,
    @Json(name = "quantity")
    val quantity: String?,
)
