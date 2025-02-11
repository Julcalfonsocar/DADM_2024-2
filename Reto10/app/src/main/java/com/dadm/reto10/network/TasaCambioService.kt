package com.dadm.reto10.network

import com.dadm.reto10.data.TasaCambio
import retrofit2.http.GET
import retrofit2.http.Query

interface TasaCambioService {
    @GET("resource/mcec-87by.json")
    suspend fun getTasaCambio(
        @Query("\$where") query: String
    ): List<TasaCambio>
} 