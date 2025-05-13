package com.stockbubble.dev

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.jakewharton.threetenabp.AndroidThreeTen
import com.stockbubble.dev.component.AppPreference
import com.stockbubble.dev.component.Injector
import com.stockbubble.dev.notification.NotificationsHandler
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 *
 */
@HiltAndroidApp
open class App : Application() {

  @Inject lateinit var appPreferences: AppPreference
  @Inject lateinit var notificationsHandler: NotificationsHandler

  override fun onCreate() {
    initLogger()
    initThreeTen()
    Injector.init(this)
    super.onCreate()
    DynamicColors.applyToActivitiesIfAvailable(this)
    AppCompatDelegate.setDefaultNightMode(appPreferences.nightMode)
    initNotificationHandler()
  }

  protected open fun initNotificationHandler() {
    notificationsHandler.initialize()
  }

  protected open fun initThreeTen() {
    AndroidThreeTen.init(this)
  }

  protected open fun initLogger() {
    Timber.plant(object : Timber.Tree() {
      override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
      ) {
        Log.println(priority, tag, message)
        t?.printStackTrace()
      }
    })
  }
}