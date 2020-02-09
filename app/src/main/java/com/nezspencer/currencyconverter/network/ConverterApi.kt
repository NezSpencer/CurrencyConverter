package com.nezspencer.currencyconverter.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ConverterApi {
    @GET("live")
    suspend fun getRates(@Query("access_key") key: String): String
}