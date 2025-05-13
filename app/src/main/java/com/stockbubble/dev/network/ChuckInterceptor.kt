package com.stockbubble.dev.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChuckInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {
    private val chuck: ChuckerInterceptor = ChuckerInterceptor(context)

    @Throws(IOException::class)
    override fun intercept(chain: Chain): Response {
        return chuck.intercept(chain)
    }

}