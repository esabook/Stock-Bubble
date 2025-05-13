package com.stockbubble.dev.network.yahoo

import com.google.gson.annotations.SerializedName

data class QuoteSummary(
  @SerializedName("assetProfile")
  val assetProfile: AssetProfile?,
  @SerializedName("financialData")
  val financialData: FinancialData?
)