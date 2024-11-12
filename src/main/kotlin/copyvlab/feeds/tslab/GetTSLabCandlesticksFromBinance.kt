package copyvlab.feeds.tslab

import copyvlab.feeds.binance.BinanceCandlestick
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun getTSLabCandlesticksFromBinance(symbol: String, inputListQuotes: List<BinanceCandlestick>): List<TSLabCandlesticks> {
    val outputVKlines = mutableListOf<TSLabCandlesticks>()//Это список для вывода окончательный.

    // Конвертируем BinanceCandlestick в VKlines и добавляем в общий список
    inputListQuotes.forEach { inputKline ->
        val vkline = TSLabCandlesticks(
            ticker = symbol,
            period = "1",
            date = getTSLabDate(inputKline),
            time = getTSLabTime(inputKline),
            open = inputKline.open,
            high = inputKline.high,
            low = inputKline.low,
            close = inputKline.close,
            volume = inputKline.volume
        )
        outputVKlines.add(vkline)
    }

    return outputVKlines
}

fun getTSLabDate(inputQuotes: BinanceCandlestick): String {
    // Преобразуем Unix-время в Instant
    val instant = Instant.ofEpochMilli(inputQuotes.openTime)
    // Преобразуем Instant в LocalDate в зоне UTC
    val dateTime = instant.atZone(ZoneOffset.UTC).toLocalDate()
    // Форматируем дату в нужный формат "yyMMdd"
    val formatter = DateTimeFormatter.ofPattern("yyMMdd")

    return dateTime.format(formatter)
}

fun getTSLabTime(inputQuotes: BinanceCandlestick): String {
    // Преобразуем Unix-время в Instant
    val instant = Instant.ofEpochMilli(inputQuotes.openTime)
    // Преобразуем Instant в LocalTime в зоне UTC
    val time = instant.atZone(ZoneOffset.UTC).toLocalTime()
    // Форматируем время в нужный формат "HHmmss"
    val formatter = DateTimeFormatter.ofPattern("HHmmss")
    return time.format(formatter)
}