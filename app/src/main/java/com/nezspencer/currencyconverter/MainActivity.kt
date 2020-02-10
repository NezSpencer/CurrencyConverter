package com.nezspencer.currencyconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.nezspencer.currencyconverter.network.ConversionRate
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val ratesList = mutableListOf<ConversionRate>()
    private val currencies = mutableListOf<String>()
    private var enteredAmount: Double = 1.0
    private var selectedItem = ConversionRate(USD_NAME, USD_RATE)
    private val currencyAdapter by lazy { ConversionRatesAdapter() }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv_conversions.adapter = currencyAdapter
        rv_conversions.addItemDecoration(DividerItemDecoration(this, GridLayoutManager.VERTICAL))
        rv_conversions.addItemDecoration(DividerItemDecoration(this, GridLayoutManager.HORIZONTAL))
        spinner_currency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedItem = ratesList.find { it.label == currencies[position] } ?: return
                currencyAdapter.updateConversionFactor(
                    viewModel.getConversionFactor(
                        selectedItem,
                        enteredAmount
                    )
                )
            }
        }
        et_amount_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val input = et_amount_input.text.toString().toDoubleOrNull()
                input ?: return
                enteredAmount = input
                currencyAdapter.updateConversionFactor(
                    viewModel.getConversionFactor(
                        selectedItem,
                        enteredAmount
                    )
                )
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
        viewModel.getConversionRates()
        viewModel.conversionRatesLiveData.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    //show loading prompt
                    Toast.makeText(this, "app loading", Toast.LENGTH_SHORT).show()
                }
                Status.SUCCESS -> {
                    it.data?.let { resultPair ->
                        spinner_currency.adapter = ArrayAdapter<String>(
                            this,
                            R.layout.support_simple_spinner_dropdown_item,
                            android.R.id.text1,
                            resultPair.first
                        )
                        currencies.clear()
                        currencies.addAll(resultPair.first)
                        ratesList.clear()
                        ratesList.addAll(resultPair.second)
                        currencyAdapter.submitList(resultPair.second)
                    }

                }
                Status.ERROR -> {

                }
            }
        })
    }
}
