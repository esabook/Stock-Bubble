package com.stockbubble.dev.network.yahoo

import com.stockbubble.dev.component.AppPreference
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YahooCrumbInterceptor @Inject constructor(
  private val appPreferences: AppPreference
) : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val crumb = appPreferences.getCrumb()
    val builder = chain.request().newBuilder()
    builder
      .removeHeader("Accept")
      .addHeader(
        "Accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"
      )
    if (!crumb.isNullOrEmpty()) {
      builder
        .url(chain.request().url.newBuilder().addQueryParameter("crumb", crumb).build())
    }
    return chain.proceed(
      builder.build()
    )
  }
}