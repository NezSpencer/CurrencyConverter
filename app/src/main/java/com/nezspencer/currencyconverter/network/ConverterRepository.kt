package com.nezspencer.currencyconverter.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.nezspencer.currencyconverter.USD_NAME
import com.nezspencer.currencyconverter.USD_RATE
import org.json.JSONObject
import javax.inject.Inject

interface ConverterRepository {
    suspend fun getConversionData(accessKey: String): LiveData<Result<Pair<List<String>, List<ConversionRate>>>>
}

class ConverterRepoImpl @Inject constructor(private val api: ConverterApi) : ConverterRepository {
    override suspend fun getConversionData(accessKey: String): LiveData<Result<Pair<List<String>, List<ConversionRate>>>> =
        liveData {
            try {
                val result = api.getRates(accessKey)
                val ratesList = parseToConversionRate(result)
                emit(Result.success(ratesList))
            } catch (ex: Throwable) {
                emit(Result.failure(ex))
            }
        }
}

fun parseToConversionRate(apiResponse: String): Pair<List<String>, List<ConversionRate>> {
    val rateObject = JSONObject(apiResponse)
    val quotes = rateObject.getJSONObject("quotes")
    val keys = quotes.keys()
    val rateList = mutableListOf<ConversionRate>()
    val currencies = mutableListOf<String>()

    // locally add USD and rate i.e 1 to the list
    currencies.add(USD_NAME)
    rateList.add(ConversionRate(USD_NAME, USD_RATE))
    while (keys.hasNext()) {
        val key = keys.next()
        currencies.add(key.replace("USD", ""))
        rateList.add(ConversionRate(key.replace("USD", ""), quotes.getDouble(key)))
    }
    return Pair(currencies, rateList)
}