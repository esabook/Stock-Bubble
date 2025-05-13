package com.stockbubble.dev.network.data

import kotlinx.serialization.Serializable

@Serializable
data class QuoteProperties(
  val symbol: String,
  var notes: String = "",
  var alertAbove: Float = 0.0f,
  var alertBelow: Float = 0.0f,
  var id: Long? = null
) {
  fun isEmpty(): Boolean = !this.notes.isNullOrEmpty() || this.alertAbove > 0.0f || this.alertBelow > 0.0f
}