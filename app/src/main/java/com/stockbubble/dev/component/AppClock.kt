package com.stockbubble.dev.component

import android.os.SystemClock
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime

interface AppClock {
  fun todayZoned(): ZonedDateTime
  fun todayLocal(): LocalDateTime
  fun currentTimeMillis(): Long
  fun elapsedRealtime(): Long

  object AppClockImpl : AppClock {

    override fun todayZoned(): ZonedDateTime = ZonedDateTime.now()

    override fun todayLocal(): LocalDateTime = LocalDateTime.now()

    override fun currentTimeMillis(): Long = System.currentTimeMillis()

    override fun elapsedRealtime(): Long = SystemClock.elapsedRealtime()
  }
}