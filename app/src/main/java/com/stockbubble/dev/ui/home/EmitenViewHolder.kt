package com.stockbubble.dev.ui.home

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.stockbubble.dev.R
import com.stockbubble.dev.component.AppPreference
import com.stockbubble.dev.databinding.ItemQuoteViewHolderBinding
import com.stockbubble.dev.formatBigNumbers
import com.stockbubble.dev.network.data.Quote
import com.stockbubble.dev.orZero

class EmitenViewHolder(val binding: ItemQuoteViewHolderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun setData(item: Quote) {
        binding.run {
            //            tvNumberRankIndicator.text = item.position.toString()
            //            tvNumberRank.text = item.position.toString()
            tvName.text = item.symbol
            tvRegularPrice.text = item.priceString()
            tvRegularMarketCap.text = item.marketCap?.formatBigNumbers(root.context)
            tvRegularMarketVolume.text = item.regularMarketVolume?.formatBigNumbers(root.context)

            tvRegularMarketChange.text = getPercentWithSign(item.changeInPercent)
            tvRegularMarketChange.changeBackground(item.changeInPercent)

            tv50DayChangePct.text = getPercentWithSign(item.fiftyDayAverageChangePercent.orZero())
            tv50DayChangePct.changeBackground(item.fiftyDayAverageChangePercent.orZero())

            tv200DayAvgChangePct.text =
                getPercentWithSign(item.twoHundredDayAverageChangePercent.orZero())
            tv200DayAvgChangePct.changeBackground(item.twoHundredDayAverageChangePercent.orZero())

            tv52WeekLowChangePct.text =
                getPercentWithSign(item.fiftyTwoWeekLowChangePercent.orZero())
            tv52WeekLowChangePct.changeBackground(item.fiftyTwoWeekLowChangePercent.orZero())

            tv52WeekHighChangePct.text =
                getPercentWithSign(item.fiftyTwoWeekHighChangePercent.orZero())
            tv52WeekHighChangePct.changeBackground(item.fiftyTwoWeekHighChangePercent.orZero())

            tvAvgVolume3Mo.text = item.averageDailyVolume3Month?.formatBigNumbers(root.context)
            tvAvgVolume10Day.text = item.averageDailyVolume10Day?.formatBigNumbers(root.context)

            tvDividendYield.text = getPercentWithSign(item.annualDividendYield)
            tvRating.text = item.averageAnalystRating ?: "-"

            val color = item.getColorRating()
            tvRating.setTextColor(itemView.context.getColor(color))
            tvName.setTextColor(itemView.context.getColor(color))
        }
    }

    companion object {


        fun Quote.getColorRating(): Int {
            return averageAnalystRating?.first()?.let {
                when (it) {
                    '1', '2' -> R.color.green
                    '3' -> R.color.yellow
                    '4', '5' -> R.color.red
                    else -> null
                }
            } ?: R.color.black_60
        }

        fun AppCompatTextView.changeBackground(variable: Float) {
            if (variable > 0f)
                setBackgroundResource(R.color.green)
            else if (variable < 0f)
                setBackgroundResource(R.color.red)
            else
                setBackgroundDrawable(null)
        }

        fun AppCompatTextView.changeTextColor(variable: Float) {
            val color = if (variable > 0f)
                R.color.green
            else if (variable < 0f)
                R.color.red
            else
                R.color.black_80

            setTextColor(ResourcesCompat.getColor(resources, color, null))
        }

        fun getPercentWithSign(variable: Float): String {
            if (variable.isNaN())
                return "-"

            val changeString = "${AppPreference.DECIMAL_FORMAT_2DP.format(variable)}%"
            if (variable > 0) {
                return "+$changeString"
            }
            return changeString
        }

    }

}
