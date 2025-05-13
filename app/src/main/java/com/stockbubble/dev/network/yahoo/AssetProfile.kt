package com.stockbubble.dev.network.yahoo

import com.google.gson.annotations.SerializedName

data class AssetProfile(
  @SerializedName("longBusinessSummary")
  val longBusinessSummary: String?,
  @SerializedName("website")
  val website: String?
)