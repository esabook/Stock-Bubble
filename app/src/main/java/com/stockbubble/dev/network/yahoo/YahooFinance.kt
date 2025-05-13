package com.stockbubble.dev.network.yahoo

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface YahooFinance {

  /**
   * Retrieves a list of stock quotes.
   *
   * @param query comma separated list of symbols.
   *
   * @return A List of quotes.
   */
  @GET(
    "v7/finance/quote?format=json"
  )
  suspend fun getStocks(@Query(value = "symbols") query: String): Response<YahooResponse>
}

