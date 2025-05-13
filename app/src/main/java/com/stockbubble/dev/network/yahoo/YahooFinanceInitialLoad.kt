package com.stockbubble.dev.network.yahoo

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface YahooFinanceInitialLoad {
  @GET("/")
  suspend fun initialLoad(): Response<String?>

  @POST
  suspend fun cookieConsent(@Url url: String?, @Body body: RequestBody): Response<String?>
}