package com.stockbubble.dev.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.stockbubble.dev.component.Injector
import com.stockbubble.dev.provider.StocksProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
class UpdateReceiver : BroadcastReceiver() {

  @Inject internal lateinit var stocksProvider: StocksProvider
  @Inject internal lateinit var coroutineScope: CoroutineScope

  override fun onReceive(
      context: Context,
      intent: Intent
  ) {
    Injector.appComponent().inject(this)
    val pendingResult = goAsync()
    coroutineScope.launch(Dispatchers.Main) {
      stocksProvider.fetch()
      pendingResult.finish()
    }
  }
}