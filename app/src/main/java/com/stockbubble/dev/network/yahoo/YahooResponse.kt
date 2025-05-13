package com.stockbubble.dev.network.yahoo

import com.google.gson.annotations.SerializedName

data class YahooResponse(
  @SerializedName("quoteResponse") val quoteResponse: QuoteResponse
)

