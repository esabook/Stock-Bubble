package com.stockbubble.dev.network.yahoo

import com.google.gson.annotations.SerializedName

data class YahooQuoteNet(
    @SerializedName("language")
    var language: String? = null,
    @SerializedName("region")
    var region: String? = null,
    @SerializedName("quoteType")
    var quoteType: String? = null,
    @SerializedName("typeDisp")
    var typeDisp: String? = null,
    @SerializedName("quoteSourceName")
    var quoteSourceName: String? = null,
    @SerializedName("triggerable")
    var triggerable: Boolean? = null,
    @SerializedName("customPriceAlertConfidence")
    var customPriceAlertConfidence: String? = null,
    @SerializedName("priceHint")
    var priceHint: Int? = null,
    @SerializedName("regularMarketChange")
    var regularMarketChange: Float? = null,
    @SerializedName("regularMarketDayHigh")
    var regularMarketDayHigh: Float? = null,
    @SerializedName("regularMarketDayRange")
    var regularMarketDayRange: String? = null,
    @SerializedName("regularMarketDayLow")
    var regularMarketDayLow: Float? = null,
    @SerializedName("regularMarketVolume")
    var regularMarketVolume: Long? = null,
    @SerializedName("regularMarketPreviousClose")
    var regularMarketPreviousClose: Float? = null,
    @SerializedName("bid")
    var bid: Long? = null,
    @SerializedName("ask")
    var ask: Long? = null,
    @SerializedName("fullExchangeName")
    var fullExchangeName: String? = null,
    @SerializedName("financialCurrency")
    var financialCurrency: String? = null,
    @SerializedName("regularMarketOpen")
    var regularMarketOpen: Float? = null,
    @SerializedName("averageDailyVolume3Month")
    var averageDailyVolume3Month: Long? = null,
    @SerializedName("averageDailyVolume10Day")
    var averageDailyVolume10Day: Long? = null,
    @SerializedName("fiftyTwoWeekLowChange")
    var fiftyTwoWeekLowChange: Float? = null,
    @SerializedName("fiftyTwoWeekLowChangePercent")
    var fiftyTwoWeekLowChangePercent: Float? = null,
    @SerializedName("fiftyTwoWeekRange")
    var fiftyTwoWeekRange: String? = null,
    @SerializedName("fiftyTwoWeekHighChange")
    var fiftyTwoWeekHighChange: Float? = null,
    @SerializedName("fiftyTwoWeekHighChangePercent")
    var fiftyTwoWeekHighChangePercent: Float? = null,
    @SerializedName("fiftyTwoWeekLow")
    var fiftyTwoWeekLow: Float? = null,
    @SerializedName("fiftyTwoWeekHigh")
    var fiftyTwoWeekHigh: Float? = null,
    @SerializedName("fiftyTwoWeekChangePercent")
    var fiftyTwoWeekChangePercent: Double? = null,
    @SerializedName("earningsTimestamp")
    var earningsTimestamp: Long? = null,
    @SerializedName("earningsTimestampStart")
    var earningsTimestampStart: Long? = null,
    @SerializedName("earningsTimestampEnd")
    var earningsTimestampEnd: Long? = null,
    @SerializedName("isEarningsDateEstimate")
    var isEarningsDateEstimate: Boolean? = null,
    @SerializedName("trailingAnnualDividendRate")
    var trailingAnnualDividendRate: Float? = null,
    @SerializedName("trailingPE")
    var trailingPE: Float? = null,
    @SerializedName("dividendRate")
    var dividendRate: Double? = null,
    @SerializedName("trailingAnnualDividendYield")
    var trailingAnnualDividendYield: Float? = null,
    @SerializedName("dividendYield")
    var dividendYield: Double? = null,
    @SerializedName("epsTrailingTwelveMonths")
    var epsTrailingTwelveMonths: Double? = null,
    @SerializedName("epsForward")
    var epsForward: Double? = null,
    @SerializedName("epsCurrentYear")
    var epsCurrentYear: Double? = null,
    @SerializedName("priceEpsCurrentYear")
    var priceEpsCurrentYear: Double? = null,
//    @SerializedName("sharesOutstanding")
//    var sharesOutstanding: Long? = null,
    @SerializedName("bookValue")
    var bookValue: Double? = null,
    @SerializedName("fiftyDayAverage")
    var fiftyDayAverage: Float? = null,
    @SerializedName("fiftyDayAverageChange")
    var fiftyDayAverageChange: Float? = null,
    @SerializedName("fiftyDayAverageChangePercent")
    var fiftyDayAverageChangePercent: Float? = null,
    @SerializedName("twoHundredDayAverage")
    var twoHundredDayAverage: Float? = null,
    @SerializedName("twoHundredDayAverageChange")
    var twoHundredDayAverageChange: Float? = null,
    @SerializedName("twoHundredDayAverageChangePercent")
    var twoHundredDayAverageChangePercent: Float? = null,
    @SerializedName("marketCap")
    var marketCap: Long? = null,
    @SerializedName("forwardPE")
    var forwardPE: Double? = null,
    @SerializedName("priceToBook")
    var priceToBook: Double? = null,
    @SerializedName("sourceInterval")
    var sourceInterval: Long? = null,
    @SerializedName("exchangeDataDelayedBy")
    var exchangeDataDelayedBy: Long? = null,
    @SerializedName("averageAnalystRating")
    var averageAnalystRating: String? = null,
    @SerializedName("currency")
    var currency: String? = null,
    @SerializedName("hasPrePostMarketData")
    var hasPrePostMarketData: Boolean? = null,
    @SerializedName("firstTradeDateMilliseconds")
    var firstTradeDateMilliseconds: Long? = null,
    @SerializedName("shortName")
    var shortName: String? = null,
    @SerializedName("marketState")
    var marketState: String? = null,
    @SerializedName("regularMarketChangePercent")
    var regularMarketChangePercent: Float? = null,
    @SerializedName("regularMarketPrice")
    var regularMarketPrice: Float? = null,
    @SerializedName("longName")
    var longName: String? = null,
    @SerializedName("tradeable")
    var tradeable: Boolean? = null,
    @SerializedName("cryptoTradeable")
    var cryptoTradeable: Boolean? = null,
//    @SerializedName("corporateActions")
//    var corporateActions: ArrayList<String> = arrayListOf(),
    @SerializedName("regularMarketTime")
    var regularMarketTime: Long? = null,
    @SerializedName("exchange")
    var exchange: String? = null,
    @SerializedName("messageBoardId")
    var messageBoardId: String? = null,
    @SerializedName("exchangeTimezoneName")
    var exchangeTimezoneName: String? = null,
    @SerializedName("exchangeTimezoneShortName")
    var exchangeTimezoneShortName: String? = null,
    @SerializedName("gmtOffSetMilliseconds")
    var gmtOffSetMilliseconds: Long? = null,
    @SerializedName("market")
    var market: String? = null,
    @SerializedName("esgPopulated")
    var esgPopulated: Boolean? = null,
    @SerializedName("symbol")
    var symbol: String? = null
)