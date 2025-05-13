package com.stockbubble.dev.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stockbubble.dev.R
import com.stockbubble.dev.databinding.DialogOpenItemQuoteBinding
import com.stockbubble.dev.network.data.Quote
import com.stockbubble.dev.ui.home.EmitenViewHolder.Companion.changeTextColor
import com.stockbubble.dev.ui.home.EmitenViewHolder.Companion.getChangePercentWithSign
import com.stockbubble.dev.ui.home.EmitenViewHolder.Companion.getColorRating
import kotlinx.coroutines.launch
import timber.log.Timber

class DialogOpenItemQuote : BottomSheetDialogFragment() {
    val binding by lazy { DialogOpenItemQuoteBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root.let {
            if (it.parent != null) {
                (it.parent as ViewGroup).removeView(it)
            }
            it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (view.parent as View).updateLayoutParams<ViewGroup.MarginLayoutParams> {
            marginStart = resources.getDimensionPixelSize(R.dimen.dp_16)
            marginEnd = resources.getDimensionPixelSize(R.dimen.dp_16)
        }

        binding.run {
            iclIdx.tvName.text = "Indonesian Stock Exchange"
            iclAjaib.tvName.text = "Ajaib"
            iclGoogleFin.tvName.text = "Google Finance"
            iclStockbit.tvName.text = "Stockbit"
            iclYahooFin.tvName.text = "Yahoo Finance"
            iclTradingview.tvName.text = "TradingView"

            iclIdx.ivLogo.setImageResource(R.drawable.logo_idx)
            iclAjaib.ivLogo.setImageResource(R.drawable.logo_ajaib)
            iclGoogleFin.ivLogo.setImageResource(R.drawable.logo_google_finance)
            iclStockbit.ivLogo.setImageResource(R.drawable.logo_stockbit)
            iclYahooFin.ivLogo.setImageResource(R.drawable.logo_yahoo_fin)
            iclTradingview.ivLogo.setImageResource(R.drawable.logo_tradingview)

            iclIdx.root.setTag(R.integer.tag_emiten_url, URL_IDX)
            iclAjaib.root.setTag(R.integer.tag_emiten_url, URL_AJAIB)
            iclGoogleFin.root.setTag(R.integer.tag_emiten_url, URL_GOOGLE_FIN)
            iclStockbit.root.setTag(R.integer.tag_emiten_url, URL_STOCKBIT)
            iclYahooFin.root.setTag(R.integer.tag_emiten_url, URL_YAHOO_FIN)
            iclTradingview.root.setTag(R.integer.tag_emiten_url, URL_TRADINGVIEW)

            iclIdx.root.setOnClickListener(::onClick)
            iclAjaib.root.setOnClickListener(::onClick)
            iclGoogleFin.root.setOnClickListener(::onClick)
            iclStockbit.root.setOnClickListener(::onClick)
            iclYahooFin.root.setOnClickListener(::onClick)
            iclTradingview.root.setOnClickListener(::onClick)
        }
    }

    fun showWithSymbol(fragmentManager: FragmentManager, quote: Quote?) {
        this.quote = quote
        super.show(fragmentManager, "DialogOpenItemQuote")
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            quote?.let { quote ->
                binding.run {
                    tvName.text = quote.symbol
                    tvLongName.text = quote.longName

                    tvRegularPrice.text = quote.priceString()
                    tvRegularMarketChange.text = getChangePercentWithSign(quote.changeInPercent)
                    tvRating.text = quote.averageAnalystRating

                    tvRegularPrice.changeTextColor(quote.changeInPercent)
                    tvRegularMarketChange.changeTextColor(quote.changeInPercent)
                    tvRating.setTextColor(ResourcesCompat.getColor(resources, quote.getColorRating(), null))
                }
            }
        }
    }

    private var quote: Quote? = null
    private fun onClick(root: View) {
        val url = root.getTag(R.integer.tag_emiten_url) as? String

        if (url != null) {
            try {
                val sym = quote?.symbol?.split(".")?.getOrNull(0) ?: return
                root.context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        url.replace("{sym}", sym).toUri()
                    )
                )
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }


    companion object {
        const val URL_STOCKBIT = "https://stockbit.com/symbol/{sym}"
        const val URL_AJAIB = "https://ajaib.onelink.me/DXmL/{sym}"
        const val URL_GOOGLE_FIN = "https://g.co/finance/{sym}:IDX"
        const val URL_YAHOO_FIN = "https://finance.yahoo.com/quote/{sym}.JK/"
        const val URL_IDX =
            "https://www.idx.co.id/id/perusahaan-tercatat/profil-perusahaan-tercatat/{sym}"
        const val URL_TRADINGVIEW = "https://www.tradingview.com/symbols/IDX-{sym}/"
    }
}