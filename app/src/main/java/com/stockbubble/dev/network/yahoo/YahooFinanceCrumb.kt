package com.stockbubble.dev.network.yahoo

import androidx.annotation.Keep
import retrofit2.Response
import retrofit2.http.GET

@Keep
interface YahooFinanceCrumb {

    @GET("v1/test/getcrumb")
  suspend fun getCrumb(): Response<String>
}