package com.nezspencer.currencyconverter.network

import com.squareup.moshi.Json

data class ConverterResponse(
    @Json(name = "success")
    val success: String,
    @Json(name = "terms")
    val terms: String,
    @Json(name = "privacy")
    val privacy: String,
    @Json(name = "timestamp")
    val timestamp: String,
    @Json(name = "source")
    val source: String,
    @Json(name = "quotes")
    val quotes: List<ConversionRate>
)

