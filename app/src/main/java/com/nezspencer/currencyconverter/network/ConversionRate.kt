package com.nezspencer.currencyconverter.network

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConversionRate(
    @PrimaryKey
    val label: String,
    val rate: Double
)