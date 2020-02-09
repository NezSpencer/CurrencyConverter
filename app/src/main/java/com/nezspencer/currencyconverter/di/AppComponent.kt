package com.nezspencer.currencyconverter.di

import com.nezspencer.currencyconverter.ConverterApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityModule::class, ViewModelModule::class])
interface AppComponent : AndroidInjector<ConverterApp> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: ConverterApp): AppComponent
    }
}