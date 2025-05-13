package com.stockbubble.dev.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.stockbubble.dev.BuildConfig
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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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
        const val USER_AGENT = "User-Agent"
        const val USER_AGENT_VALUE =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"
    }

    @Provides
    @Singleton
    internal fun provideHttpClient(
        userAgentInterceptor: UserAgentInterceptor,
        chuckInterceptor: ChuckInterceptor
    ): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        val okHttpClient =
            OkHttpClient.Builder()
                .addNetworkInterceptor(chuckInterceptor)
                .addInterceptor(userAgentInterceptor)
                .addInterceptor(logger)
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
        val logger = HttpLoggingInterceptor()
        logger.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
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
                .addInterceptor(logger)
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
        return GsonBuilder().setLenient()
            .create()
    }

    @Provides
    @Singleton
    internal fun provideGsonFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

//  @Provides @Singleton internal fun provideSuggestionsApi(
//    @ApplicationContext context: Context,
//    @Named("yahoo") okHttpClient: OkHttpClient,
//    converterFactory: GsonConverterFactory
//  ): SuggestionApi {
//    val retrofit = Retrofit.Builder()
//        .client(okHttpClient)
//        .baseUrl(context.getString(R.string.suggestions_endpoint))
//        .addConverterFactory(converterFactory)
//        .build()
//    return retrofit.create(SuggestionApi::class.java)
//  }

    @Provides
    @Singleton
    internal fun provideYahooFinance(
        @ApplicationContext context: Context,
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
        @ApplicationContext context: Context,
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
        @ApplicationContext context: Context,
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
        @ApplicationContext context: Context,
        @Named("yahoo") okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): YahooQuoteDetails {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(URL_YAHOO_FINANCE_DETAIL)
            .addConverterFactory(converterFactory)
            .build()
        val yahooFinance = retrofit.create(YahooQuoteDetails::class.java)
        return yahooFinance
    }
//
//  @Provides @Singleton internal fun provideApeWisdom(
//    @ApplicationContext context: Context,
//    okHttpClient: OkHttpClient,
//    converterFactory: GsonConverterFactory
//  ): ApeWisdom {
//    val retrofit = Retrofit.Builder()
//        .client(okHttpClient)
//        .baseUrl(context.getString(R.string.apewisdom_endpoint))
//        .addConverterFactory(converterFactory)
//        .build()
//    val apewisdom = retrofit.create(ApeWisdom::class.java)
//    return apewisdom
//  }
//
//  @Provides @Singleton internal fun provideYahooFinanceMostActive(
//    @ApplicationContext context: Context,
//    @Named("yahoo") okHttpClient: OkHttpClient,
//  ): YahooFinanceMostActive {
//    val retrofit = Retrofit.Builder()
//        .client(okHttpClient)
//        .baseUrl(context.getString(R.string.yahoo_finance_endpoint))
//        .addConverterFactory(JsoupConverterFactory())
//        .build()
//    return retrofit.create(YahooFinanceMostActive::class.java)
//  }

//  @Provides @Singleton internal fun provideGoogleNewsApi(
//    @ApplicationContext context: Context,
//    okHttpClient: OkHttpClient
//  ): GoogleNewsApi {
//    val retrofit =
//      Retrofit.Builder()
//          .client(okHttpClient)
//          .baseUrl(context.getString(R.string.google_news_endpoint))
//          .addConverterFactory(SimpleXmlConverterFactory.create())
//          .build()
//    return retrofit.create(GoogleNewsApi::class.java)
//  }

//  @Provides @Singleton internal fun provideYahooFinanceNewsApi(
//    @ApplicationContext context: Context,
//    @Named("yahoo") okHttpClient: OkHttpClient
//  ): YahooFinanceNewsApi {
//    val retrofit =
//      Retrofit.Builder()
//          .client(okHttpClient)
//          .baseUrl(context.getString(R.string.yahoo_news_endpoint))
//          .addConverterFactory(SimpleXmlConverterFactory.create())
//          .build()
//    return retrofit.create(YahooFinanceNewsApi::class.java)
//  }

    @Provides
    @Singleton
    internal fun provideHistoricalDataApi(
        @ApplicationContext context: Context,
        @Named("yahoo") okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): ChartApi {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://query1.finance.yahoo.com/v8/finance/")
            .addConverterFactory(converterFactory)
            .build()
        val api = retrofit.create(ChartApi::class.java)
        return api
    }
//
//  @Provides @Singleton internal fun provideGithubApi(
//    @ApplicationContext context: Context,
//    okHttpClient: OkHttpClient,
//    converterFactory: GsonConverterFactory
//  ): GithubApi {
//    val retrofit = Retrofit.Builder()
//        .client(okHttpClient)
//        .baseUrl(context.getString(R.string.github_endoint))
//        .addConverterFactory(converterFactory)
//        .build()
//    return retrofit.create(GithubApi::class.java)
//  }
}
