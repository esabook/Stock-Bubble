package com.stockbubble.dev.network.data

import com.stockbubble.dev.component.AppPreference
import kotlinx.serialization.Serializable

/**
 *
 */
@Serializable
data class Quote(
    var symbol: String = "",
    var name: String = "",
    var regularMarketPrice: Float = 0.toFloat(),
    var changeInPercent: Float = 0.toFloat(),
    var change: Float = 0.toFloat()
) : Comparable<Quote> {

    var isPostMarket: Boolean = false
    var stockExchange: String = ""
    var currencyCode: String = ""
    var annualDividendRate: Float = 0.toFloat()
    var annualDividendYield: Float = 0.toFloat()

    var position: QuotePosition? = null
    var properties: QuoteProperties? = null

    var region: String = ""
    var quoteType: String = ""
    var longName: String? = null
    var gmtOffSetMilliseconds: Long = 0
    var dayHigh: Float? = null
    var dayLow: Float? = null
    var previousClose: Float = 0.0f
    var open: Float? = null
    var regularMarketVolume: Long? = null
    var trailingPE: Float? = 0.0f
    var marketState: String = ""
    var tradeable: Boolean = false
    var triggerable: Boolean = false
    var averageDailyVolume3Month: Long? = null
    var averageDailyVolume10Day: Long? = null
    var fiftyTwoWeekLowChange: Float? = 0.0f
    var fiftyTwoWeekLowChangePercent: Float? = 0.0f
    var fiftyTwoWeekHighChange: Float? = 0.0f
    var fiftyTwoWeekHighChangePercent: Float? = 0.0f
    var fiftyTwoWeekLow: Float? = 0.0f
    var fiftyTwoWeekHigh: Float? = 0.0f
    var dividendDate: Long? = null
    var earningsTimestamp: Long? = null
    var fiftyDayAverage: Float? = 0.0f
    var fiftyDayAverageChange: Float? = 0.0f
    var fiftyDayAverageChangePercent: Float? = 0.0f
    var twoHundredDayAverage: Float? = 0.0f
    var twoHundredDayAverageChange: Float? = 0.0f
    var twoHundredDayAverageChangePercent: Float? = 0.0f
    var marketCap: Long? = null
    var averageAnalystRating: String? = null

    fun hasAlertAbove(): Boolean =
        this.properties != null && this.properties!!.alertAbove > 0.0f && this.properties!!.alertAbove < this.regularMarketPrice

    fun hasAlertBelow(): Boolean =
        this.properties != null && this.properties!!.alertBelow > 0.0f && this.properties!!.alertBelow > this.regularMarketPrice

    fun changePercentStringWithSign(): String {
        val changeString = "${AppPreference.DECIMAL_FORMAT_2DP.format(changeInPercent)}%"
        if (changeInPercent >= 0) {
            return "+$changeString"
        }
        return changeString
    }


    fun priceString(): String = AppPreference.SELECTED_DECIMAL_FORMAT.format(regularMarketPrice)

    override operator fun compareTo(other: Quote): Int =
        other.changeInPercent.compareTo(changeInPercent)

    fun copyValues(data: Quote) {
        this.name = data.name
        this.regularMarketPrice = data.regularMarketPrice
        this.changeInPercent = data.changeInPercent
        this.change = data.change
        this.stockExchange = data.stockExchange
        this.currencyCode = data.currencyCode
        this.annualDividendRate = data.annualDividendRate
        this.annualDividendYield = data.annualDividendYield
        this.region = data.region
        this.quoteType = data.quoteType
        this.longName = data.longName
        this.gmtOffSetMilliseconds = data.gmtOffSetMilliseconds
        this.dayHigh = data.dayHigh
        this.dayLow = data.dayLow
        this.previousClose = data.previousClose
        this.open = data.open
        this.regularMarketVolume = data.regularMarketVolume
        this.trailingPE = data.trailingPE
        this.marketState = data.marketState
        this.tradeable = data.tradeable
        this.fiftyTwoWeekLowChange = data.fiftyTwoWeekLowChange
        this.fiftyTwoWeekLowChangePercent = data.fiftyTwoWeekLowChangePercent
        this.fiftyTwoWeekHighChange = data.fiftyTwoWeekHighChange
        this.fiftyTwoWeekHighChangePercent = data.fiftyTwoWeekHighChangePercent
        this.fiftyTwoWeekLow = data.fiftyTwoWeekLow
        this.fiftyTwoWeekHigh = data.fiftyTwoWeekHigh
        this.dividendDate = data.dividendDate
        this.earningsTimestamp = data.earningsTimestamp
        this.fiftyDayAverage = data.fiftyDayAverage
        this.fiftyDayAverageChange = data.fiftyDayAverageChange
        this.fiftyDayAverageChangePercent = data.fiftyDayAverageChangePercent
        this.twoHundredDayAverage = data.twoHundredDayAverage
        this.twoHundredDayAverageChange = data.twoHundredDayAverageChange
        this.twoHundredDayAverageChangePercent = data.twoHundredDayAverageChangePercent
        this.marketCap = data.marketCap
    }

    companion object {

        private val currencyCodes: Map<String, String> by lazy {
            mapOf(
                "USD" to "$",
                "CAD" to "$",
                "EUR" to "€",
                "AED" to "د.إ.‏",
                "AFN" to "؋",
                "ALL" to "Lek",
                "AMD" to "դր.",
                "ARS" to "$",
                "AUD" to "$",
                "AZN" to "ман.",
                "BAM" to "KM",
                "BDT" to "৳",
                "BGN" to "лв.",
                "BHD" to "د.ب.‏",
                "BIF" to "FBu",
                "BND" to "$",
                "BOB" to "Bs",
                "BRL" to "R$",
                "BWP" to "P",
                "BYN" to "руб.",
                "BZD" to "$",
                "CDF" to "FrCD",
                "CHF" to "CHF",
                "CLP" to "$",
                "CNY" to "CN¥",
                "COP" to "$",
                "CRC" to "₡",
                "CVE" to "CV$",
                "CZK" to "Kč",
                "DJF" to "Fdj",
                "DKK" to "kr",
                "DOP" to "RD$",
                "DZD" to "د.ج.‏",
                "EEK" to "kr",
                "EGP" to "ج.م.‏",
                "ERN" to "Nfk",
                "ETB" to "Br",
                "GBP" to "£",
                "GBp" to "p",
                "GEL" to "GEL",
                "GHS" to "GH₵",
                "GNF" to "FG",
                "GTQ" to "Q",
                "HKD" to "$",
                "HNL" to "L",
                "HRK" to "kn",
                "HUF" to "Ft",
                "IDR" to "Rp",
                "ILS" to "₪",
                "INR" to "₹",
                "IQD" to "د.ع.‏",
                "IRR" to "﷼",
                "ISK" to "kr",
                "JMD" to "$",
                "JOD" to "د.أ.‏",
                "JPY" to "￥",
                "KES" to "Ksh",
                "KHR" to "៛",
                "KMF" to "FC",
                "KRW" to "₩",
                "KWD" to "د.ك.‏",
                "KZT" to "тңг.",
                "LBP" to "ل.ل.‏",
                "LKR" to "SLRe",
                "LTL" to "Lt",
                "LVL" to "Ls",
                "LYD" to "د.ل.‏",
                "MAD" to "د.م.‏",
                "MDL" to "MDL",
                "MGA" to "MGA",
                "MKD" to "MKD",
                "MMK" to "K",
                "MOP" to "MOP$",
                "MUR" to "MURs",
                "MXN" to "$",
                "MYR" to "RM",
                "MZN" to "MTn",
                "NAD" to "N$",
                "NGN" to "₦",
                "NIO" to "C$",
                "NOK" to "kr",
                "NPR" to "नेरू",
                "NZD" to "$",
                "OMR" to "ر.ع.‏",
                "PAB" to "B/.",
                "PEN" to "S/.",
                "PHP" to "₱",
                "PKR" to "₨",
                "PLN" to "zł",
                "PYG" to "₲",
                "QAR" to "ر.ق.‏",
                "RON" to "RON",
                "RSD" to "дин.",
                "RUB" to "₽.",
                "RWF" to "FR",
                "SAR" to "ر.س.‏",
                "SDG" to "SDG",
                "SEK" to "kr",
                "SGD" to "$",
                "SOS" to "Ssh",
                "SYP" to "ل.س.‏",
                "THB" to "฿",
                "TND" to "د.ت.‏",
                "TOP" to "T$",
                "TRY" to "TL",
                "TTD" to "$",
                "TWD" to "NT$",
                "TZS" to "TSh",
                "UAH" to "₴",
                "UGX" to "USh",
                "UYU" to "$",
                "UZS" to "UZS",
                "VEF" to "Bs.F.",
                "VND" to "₫",
                "XAF" to "FCFA",
                "XOF" to "CFA",
                "YER" to "ر.ي.‏",
                "ZAR" to "R",
                "ZMK" to "ZK",
                "ZWL" to "ZWL$"
            )
        }

        private val prefixCurrencies: Map<String, Boolean> by lazy {
            mapOf("GBp" to false)
        }
    }
}