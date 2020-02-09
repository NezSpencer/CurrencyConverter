package com.nezspencer.currencyconverter.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nezspencer.currencyconverter.network.ConversionRate

@Database(entities = [ConversionRate::class], version = 1)
abstract class CurrencyConverterDb : RoomDatabase() {
    abstract fun currencyConverterDao(): CurrencyConverterDao
}