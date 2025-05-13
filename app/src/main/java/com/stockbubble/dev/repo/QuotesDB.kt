package com.stockbubble.dev.repo

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stockbubble.dev.repo.data.HoldingRow
import com.stockbubble.dev.repo.data.PropertiesRow
import com.stockbubble.dev.repo.data.QuoteRow

@Database(
    entities = [QuoteRow::class, HoldingRow::class, PropertiesRow::class],
    version = 7,
    exportSchema = true
)
abstract class QuotesDB : RoomDatabase() {
  abstract fun quoteDao(): QuoteDao
}