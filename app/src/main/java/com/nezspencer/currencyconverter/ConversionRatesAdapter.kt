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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder.inflate(parent)

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}

class Holder private constructor(private val itemLayout: View) :
    RecyclerView.ViewHolder(itemLayout) {
    fun bind(conversionRate: ConversionRate) {
        with(itemLayout) {
            tv_currency_name.text = getCurrencyCode(conversionRate)
            tv_amount.text = NumberFormat.getNumberInstance().format(conversionRate.rate)
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