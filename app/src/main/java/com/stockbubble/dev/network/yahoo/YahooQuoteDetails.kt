package com.stockbubble.dev.network.yahoo

import androidx.annotation.Keep
import retrofit2.http.GET
import retrofit2.http.Path

@Keep
interface YahooQuoteDetails {
  @GET("quoteSummary/{symbol}?modules=financialData,assetProfile")
  suspend fun getAssetDetails(@Path(value = "symbol") symbol: String): AssetDetailsResponse
}