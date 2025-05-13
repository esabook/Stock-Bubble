package com.stockbubble.dev.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.stockbubble.dev.component.AppClock
import com.stockbubble.dev.component.AppPreference
import com.stockbubble.dev.component.Injector
import timber.log.Timber
import javax.inject.Inject

class DailySummaryNotificationReceiver: BroadcastReceiver() {

  @Inject lateinit var notificationsHandler: NotificationsHandler
  @Inject lateinit var appPreferences: AppPreference
  @Inject lateinit var clock: AppClock

  override fun onReceive(context: Context, intent: Intent?) {
    Timber.d("DailySummaryNotificationReceiver onReceive")
    Injector.appComponent().inject(this)
    val today = clock.todayLocal().toLocalDate()
    if (appPreferences.updateDays().contains(today.dayOfWeek)) {
      notificationsHandler.notifyDailySummary()
    }
  }
}