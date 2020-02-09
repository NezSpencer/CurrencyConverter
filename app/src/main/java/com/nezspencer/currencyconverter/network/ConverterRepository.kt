package com.nezspencer.currencyconverter.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.nezspencer.currencyconverter.Config
import com.nezspencer.currencyconverter.REFRESH_TIMEOUT_MS
import com.nezspencer.currencyconverter.USD_NAME
import com.nezspencer.currencyconverter.USD_RATE
import com.nezspencer.currencyconverter.db.CurrencyConverterDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

interface ConverterRepository {
    suspend fun getConversionData(accessKey: String): LiveData<Result<Pair<List<String>, List<ConversionRate>>>>
    suspend fun saveConversionData(rates: List<ConversionRate>)
    suspend fun loadConversionDataFromDb(): Pair<List<String>, List<ConversionRate>>
}

class ConverterRepoImpl @Inject constructor(
    private val api: ConverterApi,
    private val db: CurrencyConverterDb,
    private val config: Config
) : ConverterRepository {
    override suspend fun getConversionData(accessKey: String): LiveData<Result<Pair<List<String>, List<ConversionRate>>>> =
        liveData {
            try {
                val cacheResult = loadConversionDataFromDb()
                if (cacheResult.second.isEmpty() || isCacheExpired(config.lastCached)) {
                    val result = api.getRates(accessKey)
                    val ratesList = parseToConversionRate(result)
                    saveConversionData(ratesList.second)
                    emit(Result.success(ratesList))
                } else {
                    emit(Result.success(cacheResult))
                }
            } catch (ex: Throwable) {
                emit(Result.failure(ex))
            }
        }

    override suspend fun saveConversionData(rates: List<ConversionRate>) {
        withContext(Dispatchers.IO) {
            db.currencyConverterDao().insertAll(rates)
            config.lastCached = System.currentTimeMillis()
        }
    }

    override suspend fun loadConversionDataFromDb(): Pair<List<String>, List<ConversionRate>> =
        withContext(Dispatchers.IO) {
            val rates = db.currencyConverterDao().getAllRates()
            val currencies = rates.map { it.label }
            return@withContext Pair(currencies, rates)
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

fun isCacheExpired(timeCached: Long) = System.currentTimeMillis() - timeCached > REFRESH_TIMEOUT_MS