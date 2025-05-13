package com.stockbubble.dev.network.yahoo

import retrofit2.Response
import retrofit2.http.GET

interface YahooFinanceCrumb {

  @GET(
    "v1/test/getcrumb"
  )
  suspend fun getCrumb(): Response<String>
}