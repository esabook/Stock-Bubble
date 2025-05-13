package com.stockbubble.dev.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.stockbubble.dev.component.AppClock
import com.stockbubble.dev.component.AppPreference
import com.stockbubble.dev.createTimeString
import com.stockbubble.dev.network.api.StocksApi
import com.stockbubble.dev.network.data.FetchException
import com.stockbubble.dev.network.data.FetchResult
import com.stockbubble.dev.network.data.Quote
import com.stockbubble.dev.repo.StocksStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 */
@Singleton
class StocksProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: StocksApi,
    private val preferences: SharedPreferences,
    private val clock: AppClock,
    private val appPreferences: AppPreference,
    private val alarmScheduler: AlarmScheduler,
    private val storage: StocksStorage,
    private val coroutineScope: CoroutineScope
) {

    companion object {
        private const val LAST_FETCHED = "LAST_FETCHED"
        private const val NEXT_FETCH = "NEXT_FETCH"
        private val DEFAULT_STOCKS = DefaultStock.IDX
    }

    private val tickerSet: MutableSet<String> = HashSet()
//    private val quoteMap: MutableMap<String, Quote> = HashMap()

    private val _fetchState = MutableStateFlow<FetchState>(FetchState.NotFetched)
    private val _nextFetch = MutableStateFlow<Long>(0)
    private var lastFetched = 0L
    private val _tickers = MutableStateFlow<List<String>>(emptyList())
    private val _portfolio = MutableStateFlow<List<Quote>>(emptyList())

    init {
        val tickers = storage.readTickers()
        this.tickerSet.addAll(tickers)
        if (this.tickerSet.isEmpty()) {
            runBlocking(Dispatchers.IO.plus(coroutineScope.coroutineContext)) {
                this@StocksProvider.tickerSet.addAll(DEFAULT_STOCKS.split(","))
            }
        }

        _tickers.tryEmit(tickerSet.toList())
        val lastFetchedLoaded = preferences.getLong(LAST_FETCHED, 0L)
        lastFetched = lastFetchedLoaded
        val nextFetch = preferences.getLong(NEXT_FETCH, 0L)
        _nextFetch.tryEmit(nextFetch)
        runBlocking { fetchLocal() }

        if (lastFetched == 0L) {
            coroutineScope.launch {
                fetch()
            }
        } else {
            _fetchState.tryEmit(FetchState.Success(lastFetched))
        }
        schedule()
    }

    private suspend fun fetchLocal() = withContext(Dispatchers.IO) {
        try {
            val quotes = storage.readQuotes()
//            synchronized(quoteMap) {
//                quotes.forEach { quoteMap[it.symbol] = it }
//            }
//            _portfolio.emit(quoteMap.values.filter { tickerSet.contains(it.symbol) }.toList())
            _portfolio.emit(quotes)
        } catch (e: Exception) {
            Timber.w(e)
        }
    }

    private fun saveLastFetched() {
        preferences.edit {
            putLong(LAST_FETCHED, lastFetched)
        }
    }

    private fun saveTickers() {
        storage.saveTickers(tickerSet)
    }

    private fun scheduleUpdate() {
        scheduleUpdateWithMs(alarmScheduler.msToNextAlarm(lastFetched))
    }

    private fun scheduleUpdateWithMs(
        msToNextAlarm: Long
    ) {
        val updateTime = alarmScheduler.scheduleUpdate(msToNextAlarm, context)
        _nextFetch.tryEmit(updateTime.toInstant().toEpochMilli())
        preferences.edit {
            putLong(NEXT_FETCH, updateTime.toInstant().toEpochMilli())
        }
        appPreferences.setRefreshing(false)
//    widgetDataProvider.broadcastUpdateAllWidgets()
    }
//
//    private suspend fun fetchStockInternal(
//        ticker: String,
//        allowCache: Boolean
//    ): FetchResult<Quote> =
//        withContext(Dispatchers.IO) {
//            val quote = if (allowCache) quoteMap[ticker] else null
//            return@withContext quote?.let { FetchResult.success(quote) } ?: run {
//                try {
//                    val res = api.getStock(ticker)
//                    if (res.wasSuccessful) {
//                        val data = res.data
//                        val quoteFromStorage = storage.readQuote(ticker)
//                        val quote = quoteFromStorage?.let {
//                            it.copyValues(data)
//                            quoteFromStorage
//                        } ?: data
//                        quoteMap[ticker] = quote
//                        FetchResult.success(quote)
//                    } else {
//                        FetchResult.failure<Quote>(FetchException("Failed to fetch", res.error))
//                    }
//                } catch (ex: CancellationException) {
//                    // ignore
//                    FetchResult.failure<Quote>(FetchException("Failed to fetch", ex))
//                } catch (ex: Exception) {
//                    Timber.w(ex)
//                    FetchResult.failure<Quote>(FetchException("Failed to fetch", ex))
//                }
//            }
//        }

    /////////////////////
    // public api
    /////////////////////

    fun lastFetchedMs(): Long = preferences.getLong(LAST_FETCHED, 0L)

    suspend fun fetch(allowScheduling: Boolean = true): FetchResult<List<Quote>> =
        withContext(Dispatchers.IO) {
            if (tickerSet.isEmpty()) {
                if (allowScheduling) {
                    _fetchState.emit(FetchState.Failure(FetchException("No symbols in portfolio")))
                }
                FetchResult.failure<List<Quote>>(FetchException("No symbols in portfolio"))
            } else {
                return@withContext try {
                    if (allowScheduling) {
                        appPreferences.setRefreshing(true)
                    }
//                  widgetDataProvider.broadcastUpdateAllWidgets()
                    val fr = api.getStocks(tickerSet.toList())
                    if (fr.hasError) {
                        throw fr.error
                    }
                    val fetchedStocks = fr.data
                    if (fetchedStocks.isEmpty()) {
                        return@withContext FetchResult.failure<List<Quote>>(FetchException("Refresh failed"))
                    } else {
                        synchronized(tickerSet) {
                            tickerSet.addAll(fetchedStocks.map { it.symbol })
                        }
                        _tickers.emit(tickerSet.toList())
                        // clean up existing tickers
//                      tickerSet.toSet().forEach { ticker ->
//                          if (!widgetDataProvider.containsTicker(ticker)) {
//                              removeStock(ticker)
//                          }
//                      }
                        storage.saveQuotes(fetchedStocks)
                        fetchLocal()
                        if (allowScheduling) {
                            lastFetched = clock.currentTimeMillis()
                            _fetchState.emit(FetchState.Success(lastFetched))
                            saveLastFetched()
                            scheduleUpdate()
                        }
                        FetchResult.success(fetchedStocks)
                    }
                } catch (ex: CancellationException) {
                    FetchResult.failure<List<Quote>>(FetchException("Failed to fetch", ex))
                } catch (ex: Throwable) {
                    Timber.w(ex)
                    FetchResult.failure<List<Quote>>(FetchException("Failed to fetch", ex))
                } finally {
                    appPreferences.setRefreshing(false)
                }
            }
        }

    fun schedule() {
        scheduleUpdate()
        alarmScheduler.enqueuePeriodicRefresh()
    }

//    suspend fun fetchStock(ticker: String, allowCache: Boolean = true): FetchResult<Quote> {
//        return fetchStockInternal(ticker, allowCache)
//    }

//    fun getStock(ticker: String): Quote? = quoteMap[ticker]

    val portfolio: StateFlow<List<Quote>>
        get() = _portfolio//quoteMap.filter { widgetDataProvider.containsTicker(it.key) }.map { it.value }

    val fetchState: StateFlow<FetchState>
        get() = _fetchState

    val nextFetchMs: StateFlow<Long>
        get() = _nextFetch

    sealed class FetchState {
        abstract val displayString: String

        object NotFetched : FetchState() {
            override val displayString: String = "--"
        }

        class Success(val fetchTime: Long) : FetchState() {
            override val displayString: String by lazy {
                val instant = Instant.ofEpochMilli(fetchTime)
                val time = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                time.createTimeString()
            }
        }

        class Failure(val exception: Throwable) : FetchState() {
            override val displayString: String by lazy {
                exception.message.orEmpty()
            }
        }
    }
}
