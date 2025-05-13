package com.stockbubble.dev.component

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import androidx.work.WorkManager
import com.stockbubble.dev.component.AppClock.AppClockImpl
import com.stockbubble.dev.repo.QuoteDao
import com.stockbubble.dev.repo.QuotesDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

  @Singleton @Provides fun provideApplicationScope(): CoroutineScope =
      CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

  @Provides @Singleton fun provideClock(): AppClock = AppClockImpl

  @Provides @Singleton fun provideDefaultSharedPreferences(
    @ApplicationContext context: Context
  ): SharedPreferences =
    context.getSharedPreferences(AppPreference.PREFS_NAME, MODE_PRIVATE)

  @Provides @Singleton fun provideAppWidgetManager(@ApplicationContext context: Context): AppWidgetManager =
    AppWidgetManager.getInstance(context)

  @Provides @Singleton fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
    WorkManager.getInstance(context)
//
//  @Provides @Singleton fun provideAnalytics(
//    @ApplicationContext context: Context,
//    properties: Lazy<GeneralProperties>
//  ): Analytics = AnalyticsImpl(context, properties)

  @Provides @Singleton fun provideQuotesDB(@ApplicationContext context: Context): QuotesDB {
    return Room.databaseBuilder(
        context.applicationContext,
        QuotesDB::class.java, "quotes-db"
    ).fallbackToDestructiveMigration(false)
//        .addMigrations(MIGRATION_1_2)
//        .addMigrations(MIGRATION_2_3)
//        .addMigrations(MIGRATION_3_4)
//        .addMigrations(MIGRATION_4_5)
//        .addMigrations(MIGRATION_5_6)
        .build()
  }

  @Provides @Singleton fun provideQuoteDao(db: QuotesDB): QuoteDao = db.quoteDao()
//
//  @Provides @Singleton fun providesAppReviewManager(@ApplicationContext context: Context): IAppReviewManager {
//    return AppReviewManager(context)
//  }
}