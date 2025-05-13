package com.stockbubble.dev.component

import com.stockbubble.dev.App
import dagger.hilt.EntryPoints

/**
 *
 */
object Injector {

  private lateinit var app: App

  fun init(app: App) {
    this.app = app
  }

  fun appComponent(): AppEntryPoint {
    return EntryPoints.get(app, AppEntryPoint::class.java)
  }
}