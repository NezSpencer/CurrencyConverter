package com.nezspencer.currencyconverter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nezspencer.currencyconverter.network.ConversionRate
import com.nezspencer.currencyconverter.network.ConverterRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val converterRepository: ConverterRepository) :
    ViewModel() {
    private val _conversionRatesLiveData = MutableLiveData<Resource<List<ConversionRate>>>()
    val conversionRatesLiveData: LiveData<Resource<List<ConversionRate>>>
        get() = _conversionRatesLiveData

    fun getConversionRates() {
        _conversionRatesLiveData.value = Resource.loading()
        viewModelScope.launch {
            converterRepository.getConversionData(BuildConfig.SERVER_KEY)
                .addOneShotResourceObserver {
                    it.onSuccess {
                        _conversionRatesLiveData.value = Resource.success(it)
                    }

                    it.onFailure {
                        _conversionRatesLiveData.value = Resource.error(it.message)
                    }
                }
        }
    }
}