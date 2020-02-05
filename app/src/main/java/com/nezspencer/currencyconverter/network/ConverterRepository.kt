package com.nezspencer.currencyconverter.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData

interface ConverterRepository {
    suspend fun getConversionData(accessKey: String): LiveData<Result<List<ConversionRate>>>
}

class ConverterRepoImpl(private val api: ConverterApi) : ConverterRepository {
    override suspend fun getConversionData(accessKey: String): LiveData<Result<List<ConversionRate>>> =
        liveData {
            try {
                val result = api.getRates(mapOf("access_key" to accessKey, "format" to "1"))
                emit(Result.success(result.quotes))
            } catch (ex: Throwable) {
                emit(Result.failure(ex))
            }
        }
}