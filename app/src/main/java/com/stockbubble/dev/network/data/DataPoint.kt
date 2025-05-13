package com.stockbubble.dev.network.data

import com.github.mikephil.charting.data.CandleEntry
import kotlinx.serialization.Serializable
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

@Serializable
class DataPoint(
  val xVal: Float,
  val shadowH: Float,
  val shadowL: Float,
  val openVal: Float,
  val closeVal: Float
) : CandleEntry(xVal, shadowH, shadowL, openVal, closeVal), Comparable<DataPoint> {

  fun getDate(): LocalDate = LocalDateTime.ofInstant(Instant.ofEpochSecond(x.toLong()), ZoneId.systemDefault()).toLocalDate()

  override fun compareTo(other: DataPoint): Int = x.compareTo(other.x)

  companion object {
    private const val serialVersionUID = 42L
  }
}