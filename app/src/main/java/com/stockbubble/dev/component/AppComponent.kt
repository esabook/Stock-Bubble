package com.stockbubble.dev.component

import com.google.gson.Gson
import com.stockbubble.dev.notification.DailySummaryNotificationReceiver
import com.stockbubble.dev.provider.RefreshWorker
import com.stockbubble.dev.receiver.RefreshReceiver
import com.stockbubble.dev.receiver.UpdateReceiver
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 *
 */

interface LegacyComponent {
    fun gson(): Gson

    //  fun inject(widget: StockWidget)
//
//  fun inject(data: WidgetData)
//
//  fun inject(adapter: RemoteStockViewAdapter)
//
//  fun inject(receiver: WidgetClickReceiver)
    fun inject(receiver: DailySummaryNotificationReceiver)
    fun inject(refreshWorker: RefreshWorker)
    fun inject(receiver: RefreshReceiver)
    fun inject(receiver: UpdateReceiver)
}

@InstallIn(SingletonComponent::class)
@EntryPoint
interface AppEntryPoint : LegacyComponent
