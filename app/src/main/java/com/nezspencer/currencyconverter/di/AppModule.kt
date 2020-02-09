package com.nezspencer.currencyconverter.di

import com.nezspencer.currencyconverter.BuildConfig
import com.nezspencer.currencyconverter.internal
import com.nezspencer.currencyconverter.network.ConverterApi
import com.nezspencer.currencyconverter.network.ConverterRepoImpl
import com.nezspencer.currencyconverter.network.ConverterRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule {
    private val logger by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            internal {
                addInterceptor(logger)
            }
        }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideConverterApi(retrofit: Retrofit): ConverterApi =
        retrofit.create(ConverterApi::class.java)

    @Provides
    @Singleton
    fun provideConverterRepository(converterApi: ConverterApi): ConverterRepository {
        return ConverterRepoImpl(converterApi)
    }
}