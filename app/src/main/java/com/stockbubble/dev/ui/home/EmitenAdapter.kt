package com.stockbubble.dev.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.stockbubble.dev.R
import com.stockbubble.dev.databinding.ItemQuoteViewHolderBinding
import com.stockbubble.dev.network.data.Quote

class EmitenAdapter(val onItemClick: (Quote?) -> Unit) :
    ListAdapter<Quote, EmitenViewHolder>(DIFF) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmitenViewHolder {
        return EmitenViewHolder(
            ItemQuoteViewHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: EmitenViewHolder,
        position: Int
    ) {
        holder.setData(getItem(position))
        holder.itemView.setTag(R.integer.tag_unspecified, position)
        holder.itemView.setOnClickListener(::onItemClick)
    }

    private fun onItemClick(view: View) {
        val quotePos = view.getTag(R.integer.tag_unspecified) as Int
        onItemClick.invoke(getItem(quotePos))
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Quote>() {
            override fun areItemsTheSame(oldItem: Quote, newItem: Quote): Boolean {
                return oldItem.symbol == newItem.symbol
            }

            override fun areContentsTheSame(
                oldItem: Quote,
                newItem: Quote
            ): Boolean {
                return oldItem.regularMarketPrice == newItem.regularMarketPrice
            }

        }
    }
}