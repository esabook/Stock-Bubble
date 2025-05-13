package com.stockbubble.dev.network.data

import kotlinx.serialization.Serializable

@Serializable
data class QuotePosition(
  var symbol: String = "",
  var holdings: MutableList<Holding> = ArrayList()
)

@Serializable
data class Holding(
    val symbol: String,
    val shares: Float = 0.0f,
    val price: Float = 0.0f,
    var id: Long? = null
) {

  fun totalValue(): Float = shares * price
}