package com.nezspencer.currencyconverter

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            viewModelFactory
        )[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ratesAdapter = ConversionRatesAdapter()
        rv_conversions.adapter = ratesAdapter
        rv_conversions.addItemDecoration(DividerItemDecoration(this, GridLayoutManager.VERTICAL))
        rv_conversions.addItemDecoration(DividerItemDecoration(this, GridLayoutManager.HORIZONTAL))
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
                        ratesAdapter.submitList(resultPair.second)
                    }

                }
                Status.ERROR -> {

                }
            }
        })
    }
}
