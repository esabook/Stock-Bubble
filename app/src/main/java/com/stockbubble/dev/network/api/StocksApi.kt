package com.stockbubble.dev.network.api

import com.stockbubble.dev.component.AppPreference
import com.stockbubble.dev.network.data.FetchException
import com.stockbubble.dev.network.data.FetchResult
import com.stockbubble.dev.network.data.Quote
import com.stockbubble.dev.network.yahoo.QuoteSummary
import com.stockbubble.dev.network.yahoo.YahooFinance
import com.stockbubble.dev.network.yahoo.YahooFinanceCrumb
import com.stockbubble.dev.network.yahoo.YahooFinanceInitialLoad
import com.stockbubble.dev.network.yahoo.YahooQuoteDetails
import com.stockbubble.dev.network.yahoo.YahooQuoteNet
import com.stockbubble.dev.network.yahoo.YahooResponse
import com.stockbubble.dev.orZero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 */
@Singleton
class StocksApi @Inject constructor(
    private val yahooFinanceInitialLoad: YahooFinanceInitialLoad,
    private val yahooFinanceCrumb: YahooFinanceCrumb,
    private val yahooFinance: YahooFinance,
    private val appPreferences: AppPreference,
    private val yahooQuoteDetails: YahooQuoteDetails,
//  private val suggestionApi: SuggestionApi
) {

    val csrfTokenMatchPattern by lazy {
        Regex("csrfToken\" value=\"(.+)\">")
    }

    private suspend fun loadCrumb() {
        withContext(Dispatchers.IO) {
            try {
                val initialLoad = yahooFinanceInitialLoad.initialLoad()
                val html = initialLoad.body() ?: ""
                val url = initialLoad.raw().request.url.toString()
                val match = csrfTokenMatchPattern.find(html)
                if (!match?.groupValues.isNullOrEmpty()) {
                    val csrfToken = match?.groupValues?.last().toString()
                    val sessionId = url.split("=").last()

                    val requestBody = FormBody.Builder()
                        .add("csrfToken", csrfToken)
                        .add("sessionId", sessionId)
                        .addEncoded("originalDoneUrl", "https://finance.yahoo.com/?guccounter=1")
                        .add("namespace", "yahoo")
                        .add("agree", "agree")
                        .build()

                    val cookieConsent = yahooFinanceInitialLoad.cookieConsent(url, requestBody)
                    if (!cookieConsent.isSuccessful) {
                        Timber.e("Failed cookie consent with code: ${cookieConsent.code()}")
                        return@withContext
                    }
                }

                val crumbResponse = yahooFinanceCrumb.getCrumb()
                if (crumbResponse.isSuccessful) {
                    val crumb = crumbResponse.body()
                    if (!crumb.isNullOrEmpty()) {
                        appPreferences.setCrumb(crumb)
                    }
                } else {
                    Timber.e("Failed to get crumb with code: ${crumbResponse.code()}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Crumb load failed")
            }
        }
    }

//  suspend fun getSuggestions(query: String): FetchResult<List<SuggestionNet>> =
//      withContext(Dispatchers.IO) {
//          val suggestions = try {
//              suggestionApi.getSuggestions(query).result
//          } catch (e: Exception) {
//              Timber.e(e)
//              return@withContext FetchResult.failure(FetchException("Error fetching", e))
//          }
//          val suggestionList = suggestions?.let { ArrayList(it) } ?: ArrayList()
//          return@withContext FetchResult.success<List<SuggestionNet>>(suggestionList)
//      }

    suspend fun getStocks(tickerList: List<String>): FetchResult<List<Quote>> =
        withContext(Dispatchers.IO) {
            try {
                val quoteNets = getStocksYahoo(tickerList)
                    ?: return@withContext FetchResult.failure(FetchException("Failed to fetch"))
                return@withContext FetchResult.success(
                    quoteNets.toQuoteMap().toOrderedList(tickerList)
                )
            } catch (ex: Exception) {
                Timber.e(ex)
                return@withContext FetchResult.failure(FetchException("Failed to fetch", ex))
            }
        }

    suspend fun getStock(ticker: String): FetchResult<Quote> =
        withContext(Dispatchers.IO) {
            try {
                val quoteNets = getStocksYahoo(listOf(ticker))
                    ?: return@withContext FetchResult.failure(FetchException("Failed to fetch $ticker"))
                return@withContext FetchResult.success(quoteNets.first().toQuote())
            } catch (ex: Exception) {
                Timber.e(ex)
                return@withContext FetchResult.failure(
                    FetchException(
                        "Failed to fetch $ticker",
                        ex
                    )
                )
            }
        }

    suspend fun getQuoteDetails(ticker: String): FetchResult<QuoteSummary> =
        withContext(Dispatchers.IO) {
            try {
                val quoteSummaryResponse = yahooQuoteDetails.getAssetDetails(ticker)
                val data = quoteSummaryResponse.quoteSummary.result.firstOrNull()
                return@withContext data?.let {
                    FetchResult.success(it)
                } ?: FetchResult.failure(
                    FetchException(
                        "Failed to fetch quote details for $ticker with error ${quoteSummaryResponse.quoteSummary.error}"
                    )
                )
            } catch (e: Exception) {
                Timber.e(e)
                return@withContext FetchResult.failure(
                    FetchException("Failed to fetch quote details for $ticker", e)
                )
            }
        }

    private suspend fun getStocksYahoo(
        tickerList: List<String>,
        invocationCount: Int = 1
    ): List<YahooQuoteNet>? =
        withContext(Dispatchers.IO) {
            val query = tickerList.joinToString(",")
            var quotesResponse: Response<YahooResponse>? = null
            try {
                quotesResponse = yahooFinance.getStocks(query)
                if (!quotesResponse.isSuccessful) {
                    Timber.e("Yahoo quote fetch failed with code ${quotesResponse.code()}")
                }
                if (quotesResponse.code() == 401) {
                    appPreferences.setCrumb(null)
                    loadCrumb()
                    if (invocationCount == 1) {
                        return@withContext getStocksYahoo(
                            tickerList,
                            invocationCount = invocationCount + 1
                        )
                    }
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                throw ex
            }
            val quoteNets = quotesResponse.body()?.quoteResponse?.result
            quoteNets
        }

    private fun List<YahooQuoteNet>.toQuoteMap(): MutableMap<String, Quote> {
        val quotesMap = HashMap<String, Quote>()
        for (quoteNet in this) {
            val quote = quoteNet.toQuote()
            quotesMap[quote.symbol] = quote
        }
        return quotesMap
    }

    private fun MutableMap<String, Quote>.toOrderedList(tickerList: List<String>): List<Quote> {
        val quotes = ArrayList<Quote>()
        tickerList.filter { this.containsKey(it) }
            .mapTo(quotes) { this[it]!! }
        return quotes
    }

    private fun YahooQuoteNet.toQuote(): Quote {
        val quote = Quote(
            symbol = this.symbol.orEmpty(),
            name = (this.shortName ?: this.longName).orEmpty(),
            lastTradePrice = this.regularMarketPrice.orZero(),
            changeInPercent = this.regularMarketChangePercent.orZero(),
            change = this.regularMarketChange.orZero()
        )
        quote.stockExchange = this.exchange ?: ""
        quote.currencyCode = this.currency ?: "USD"
        quote.annualDividendRate = this.trailingAnnualDividendRate.orZero()
        quote.annualDividendYield = this.trailingAnnualDividendYield.orZero()
        quote.region = this.region.orEmpty()
        quote.quoteType = this.quoteType.orEmpty()
        quote.longName = this.longName
        quote.gmtOffSetMilliseconds = this.gmtOffSetMilliseconds.orZero()
        quote.dayHigh = this.regularMarketDayHigh
        quote.dayLow = this.regularMarketDayLow
        quote.previousClose = this.regularMarketPreviousClose.orZero()
        quote.open = this.regularMarketOpen
        quote.regularMarketVolume = this.regularMarketVolume.orZero()
        quote.trailingPE = this.trailingPE
        quote.marketState = this.marketState.orEmpty()
        quote.tradeable = this.tradeable ?: false
        quote.triggerable = this.triggerable ?: false
        quote.fiftyTwoWeekLowChange = this.fiftyTwoWeekLowChange
        quote.fiftyTwoWeekLowChangePercent = this.fiftyTwoWeekLowChangePercent
        quote.fiftyTwoWeekHighChange = this.fiftyTwoWeekHighChange
        quote.fiftyTwoWeekHighChangePercent = this.fiftyTwoWeekHighChangePercent
        quote.fiftyTwoWeekLow = this.fiftyTwoWeekLow
        quote.fiftyTwoWeekHigh = this.fiftyTwoWeekHigh
        quote.dividendDate = this.firstTradeDateMilliseconds
        quote.earningsTimestamp = this.earningsTimestamp
        quote.fiftyDayAverage = this.fiftyDayAverage
        quote.fiftyDayAverageChange = this.fiftyDayAverageChange
        quote.fiftyDayAverageChangePercent = this.fiftyDayAverageChangePercent
        quote.twoHundredDayAverage = this.twoHundredDayAverage
        quote.twoHundredDayAverageChange = this.twoHundredDayAverageChange
        quote.twoHundredDayAverageChangePercent = this.twoHundredDayAverageChangePercent
        quote.marketCap = this.marketCap
        quote.averageAnalystRating = this.averageAnalystRating
        return quote
    }
}
