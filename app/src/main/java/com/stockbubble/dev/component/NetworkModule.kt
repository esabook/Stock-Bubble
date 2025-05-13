package com.stockbubble.dev.component

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.stockbubble.dev.BuildConfig
import com.stockbubble.dev.network.ChuckInterceptor
import com.stockbubble.dev.network.UserAgentInterceptor
import com.stockbubble.dev.network.api.ChartApi
import com.stockbubble.dev.network.yahoo.YahooCrumbInterceptor
import com.stockbubble.dev.network.yahoo.YahooFinance
import com.stockbubble.dev.network.yahoo.YahooFinanceCookies
import com.stockbubble.dev.network.yahoo.YahooFinanceCrumb
import com.stockbubble.dev.network.yahoo.YahooFinanceInitialLoad
import com.stockbubble.dev.network.yahoo.YahooQuoteDetails
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 *
 */
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        internal const val CONNECTION_TIMEOUT: Long = 5000
        internal const val READ_TIMEOUT: Long = 5000

        const val URL_YAHOO_FINANCE_INIT = "https://finance.yahoo.com/"
        const val URL_YAHOO_FINANCE_DETAIL = "https://query1.finance.yahoo.com/v11/finance/"
        const val URL_YAHOO_FINANCE = "https://query1.finance.yahoo.com/"
        const val URL_YAHOO_FINANCE_HISTORY = "https://query1.finance.yahoo.com/v8/finance/"

        const val USER_AGENT = "User-Agent"
        const val USER_AGENT_VALUE =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"
    }

    private val httpLogger by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) BODY else NONE
        }
    }

    @Provides
    @Singleton
    internal fun provideHttpClient(
        userAgentInterceptor: UserAgentInterceptor,
        chuckInterceptor: ChuckInterceptor
    ): OkHttpClient {
        val okHttpClient =
            OkHttpClient.Builder()
                .addNetworkInterceptor(chuckInterceptor)
                .addInterceptor(userAgentInterceptor)
                .addInterceptor(httpLogger)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .build()
        return okHttpClient
    }

    @Named("yahoo")
    @Provides
    @Singleton
    internal fun provideHttpClientForYahoo(
        crumbInterceptor: YahooCrumbInterceptor,
        chuckInterceptor: ChuckInterceptor,
        cookieJar: YahooFinanceCookies
    ): OkHttpClient {
        val okHttpClient =
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val newRequest = chain.request()
                        .newBuilder()
                        .removeHeader(USER_AGENT)
                        .addHeader(
                           USER_AGENT,
                           USER_AGENT_VALUE
                        )
                        .build()
                    chain.proceed(newRequest)
                }
                .addNetworkInterceptor(chuckInterceptor)
                .addInterceptor(httpLogger)
                .addInterceptor(crumbInterceptor)
                .cookieJar(cookieJar)
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
                .build()
        return okHttpClient
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        return GsonBuilder().setStrictness(Strictness.LENIENT)
            .create()
    }

    @Provides
    @Singleton
    internal fun provideGsonFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    internal fun provideYahooFinance(
        @Named("yahoo") okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): YahooFinance {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(URL_YAHOO_FINANCE)
            .addConverterFactory(converterFactory)
            .build()
        val yahooFinance = retrofit.create(YahooFinance::class.java)
        return yahooFinance
    }

    @Provides
    @Singleton
    internal fun provideYahooFinanceInitialLoad(
        @Named("yahoo") okHttpClient: OkHttpClient
    ): YahooFinanceInitialLoad {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(URL_YAHOO_FINANCE_INIT)
            .build()
        val yahooFinance = retrofit.create(YahooFinanceInitialLoad::class.java)
        return yahooFinance
    }

    @Provides
    @Singleton
    internal fun provideYahooFinanceCrumb(
        @Named("yahoo") okHttpClient: OkHttpClient
    ): YahooFinanceCrumb {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(URL_YAHOO_FINANCE)
            .build()
        val yahooFinance = retrofit.create(YahooFinanceCrumb::class.java)
        return yahooFinance
    }

    @Provides
    @Singleton
    internal fun provideYahooQuoteDetailsApi(
        @Named("yahoo") okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): YahooQuoteDetails {
        try {
            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(URL_YAHOO_FINANCE_DETAIL)
                .addConverterFactory(converterFactory)
                .build()
            val yahooFinance = retrofit.create(YahooQuoteDetails::class.java)
            return yahooFinance
        } catch (e: Exception) {
            throw e
        }
    }

    @Provides
    @Singleton
    internal fun provideHistoricalDataApi(
        @Named("yahoo") okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): ChartApi {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(URL_YAHOO_FINANCE_HISTORY)
            .addConverterFactory(converterFactory)
            .build()
        val api = retrofit.create(ChartApi::class.java)
        return api
    }
}