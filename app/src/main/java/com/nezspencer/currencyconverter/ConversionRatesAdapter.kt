package com.nezspencer.currencyconverter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nezspencer.currencyconverter.network.ConversionRate
import kotlinx.android.synthetic.main.item_conversion_layout.view.*
import java.text.NumberFormat

class ConversionRatesAdapter : ListAdapter<ConversionRate, Holder>(ConversionRatesDiff()) {
    private var conversionFactor: Double = 1.0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder.inflate(parent)

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), conversionFactor)
    }

    fun updateConversionFactor(factor: Double) {
        conversionFactor = factor
        notifyDataSetChanged()
    }
}

class Holder private constructor(private val itemLayout: View) :
    RecyclerView.ViewHolder(itemLayout) {
    fun bind(conversionRate: ConversionRate, conversionFactor: Double) {
        with(itemLayout) {
            tv_currency_name.text = conversionRate.label
            tv_amount.text =
                NumberFormat.getNumberInstance().format(conversionFactor * conversionRate.rate)
        }
    }

    companion object {
        fun inflate(parent: ViewGroup) = Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_conversion_layout,
                parent,
                false
            )
        )
    }
}

class ConversionRatesDiff : DiffUtil.ItemCallback<ConversionRate>() {
    override fun areItemsTheSame(oldItem: ConversionRate, newItem: ConversionRate) =
        oldItem.label == newItem.label

    override fun areContentsTheSame(oldItem: ConversionRate, newItem: ConversionRate) =
        oldItem == newItem
}