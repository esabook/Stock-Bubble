package com.stockbubble.dev.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.stockbubble.dev.createTimeString
import com.stockbubble.dev.provider.StocksProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stocksProvider: StocksProvider
) : ViewModel() {

  val fetchState = stocksProvider.fetchState.asLiveData(Dispatchers.Main)
  val lasFetchedMs: Long
    get() = stocksProvider.lastFetchedMs()

    val quotes = stocksProvider.portfolio
  val nextFetchMs = stocksProvider.nextFetchMs.asLiveData(Dispatchers.IO)

  fun fetch() = liveData {
      val fetch = stocksProvider.fetch()
      emit(fetch.wasSuccessful)
  }

  fun lastFetched(): String {
    return stocksProvider.fetchState.value.displayString
  }

  fun nextFetch(): String {
    val nextUpdateMs = stocksProvider.nextFetchMs.value
    val instant = Instant.ofEpochMilli(nextUpdateMs)
    val time = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
    return time.createTimeString()
  }
}