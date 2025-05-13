package com.stockbubble.dev.network.yahoo

import com.google.gson.annotations.SerializedName

data class GrowthItem(
  @SerializedName("raw")
  val raw: Float,
  @SerializedName("fmt")
  val fmt: String
)