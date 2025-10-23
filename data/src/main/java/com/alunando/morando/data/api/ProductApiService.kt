package com.alunando.morando.data.api

import com.alunando.morando.data.api.model.CosmosResponse
import com.alunando.morando.data.api.model.OpenFoodFactsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

/**
 * Interface da API Cosmos para buscar produtos por código de barras
 */
interface CosmosApiService {
    @GET("gtins/{gtin}.json")
    suspend fun getProductByGtin(
        @Path("gtin") gtin: String,
        @Header("X-Cosmos-Token") token: String,
    ): Response<CosmosResponse>
}

/**
 * Interface da API OpenFoodFacts (fallback)
 */
interface OpenFoodFactsApiService {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String,
    ): Response<OpenFoodFactsResponse>
}
