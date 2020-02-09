package com.nezspencer.currencyconverter.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nezspencer.currencyconverter.network.ConversionRate

@Dao
interface CurrencyConverterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversionRate: ConversionRate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rates: List<ConversionRate>)

    @Query("SELECT * FROM ConversionRate")
    suspend fun getAllRates(): List<ConversionRate>
}