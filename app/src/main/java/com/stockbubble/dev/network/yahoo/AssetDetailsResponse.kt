package com.stockbubble.dev.network.yahoo

import com.google.gson.annotations.SerializedName

data class AssetDetailsResponse(
  @SerializedName("quoteSummary")
  val quoteSummary: QuoteSummaryResult
)