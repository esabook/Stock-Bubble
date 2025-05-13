package com.stockbubble.dev.network.yahoo

import androidx.annotation.Keep
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

@Keep
interface YahooFinance {

  /**
   * Retrieves a list of stock quotes.
   *
   * @param query comma separated list of symbols.
   *
   * @return A List of quotes.
   */
  @GET("v7/finance/quote?format=json")
  suspend fun getStocks(@Query(value = "symbols") query: String): Response<YahooResponse>
}

