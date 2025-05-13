package com.stockbubble.dev.network.yahoo

import com.google.gson.annotations.SerializedName

data class FinancialData(
  @SerializedName("revenueGrowth")
  val revenueGrowth: GrowthItem?,
  @SerializedName("grossMargins")
  val grossMargins: GrowthItem?,
  @SerializedName("earningsGrowth")
  val earningsGrowth: GrowthItem?,
  @SerializedName("ebitdaMargins")
  val ebitdaMargins: GrowthItem?,
  @SerializedName("operatingMargins")
  val operatingMargins: GrowthItem?,
  @SerializedName("profitMargins")
  val profitMargins: GrowthItem?,
  @SerializedName("financialCurrency")
  val financialCurrency: String?
)