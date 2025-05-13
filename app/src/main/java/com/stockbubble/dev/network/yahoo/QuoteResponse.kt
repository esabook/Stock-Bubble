package com.stockbubble.dev.network.yahoo

import com.google.gson.annotations.SerializedName

data class QuoteResponse(
  @SerializedName("result") val result: List<YahooQuoteNet>?
)