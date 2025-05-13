package com.stockbubble.dev.network.yahoo

import com.google.gson.annotations.SerializedName

data class QuoteSummaryResult(
  @SerializedName("result")
  val result: List<QuoteSummary>,
  @SerializedName("error")
  val error: String?
)