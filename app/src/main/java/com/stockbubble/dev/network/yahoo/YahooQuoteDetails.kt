package com.stockbubble.dev.network.yahoo

import retrofit2.http.GET
import retrofit2.http.Path

interface YahooQuoteDetails {
  @GET("quoteSummary/{symbol}?modules=financialData,assetProfile")
  suspend fun getAssetDetails(@Path(value = "symbol") symbol: String): AssetDetailsResponse
}