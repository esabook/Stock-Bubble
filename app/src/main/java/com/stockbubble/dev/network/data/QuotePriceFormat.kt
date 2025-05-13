package com.stockbubble.dev.network.data

import com.stockbubble.dev.component.AppPreference

class QuotePriceFormat(
  val currencyCode: String,
  val symbol: String,
  val prefix: Boolean = true
) {
  fun format(price: Float): String {
    val priceString = AppPreference.SELECTED_DECIMAL_FORMAT.format(price)
    return if (prefix) {
      "$symbol$priceString"
    } else {
      "$priceString$symbol"
    }
  }
}