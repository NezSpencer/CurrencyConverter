package com.nezspencer.currencyconverter

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.nezspencer.currencyconverter.network.ConversionRate

fun <T> LiveData<T>.addOneShotResourceObserver(
    viewLifecycleOwner: LifecycleOwner? = null,
    function: (T) -> Unit
) {
    var oneShotObserver: OneShotObserver<T>? = null
    oneShotObserver = OneShotObserver(function) {
        oneShotObserver?.let { observer ->
            removeObserver(observer)
        }
    }

    viewLifecycleOwner?.let {
        observe(it, oneShotObserver)
    } ?: observeForever(oneShotObserver)
}

private class OneShotObserver<T>(
    private val function: (T) -> Unit,
    private val done: () -> Unit
) :
    Observer<T> {
    override fun onChanged(t: T?) {
        t?.let(function)
        done.invoke()
    }
}

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message: String?,
    val state: Boolean = true
) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String? = null, data: T? = null, state: Boolean = true): Resource<T> {
            return Resource(Status.ERROR, data, msg, state)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }

    val isLoading
        get() = status == Status.LOADING
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}

fun internal(block: () -> Unit) {
    if (BuildConfig.DEBUG) block()
}

fun getCurrencyCode(conversionRate: ConversionRate) = conversionRate.label.replace("USD", "")