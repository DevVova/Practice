package learnokhttp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.math.min

fun main() {
    val newList = getMinuteVKlines("SOLUSDT", limit = 700)
    println(newList)
    println(newList.size)
    println(newList[0].open)
}

/**
 * Получаем данные определённого количества свечей, разбивая запрос на части, если необходимо.
 */
fun getMinuteVKlines(
    symbol: String = "BTCUSDT",
    limit: Int = 1000
): List<MinuteVKlines> {
    val client = OkHttpClient()
    val outputVKlines = mutableListOf<MinuteVKlines>()
    var remaining = limit
    var lastCloseTime: Long? = null
    var hasMoreData = true

    while (remaining > 0 && hasMoreData) {
        val requestLimit = min(1000, remaining)  // Запрашиваем максимум 1000 записей за один раз
        val urlBuilder =
            StringBuilder("https://api.binance.com/api/v3/klines?symbol=$symbol&interval=1m&limit=$requestLimit")

        // Если есть lastCloseTime, добавляем параметр startTime, чтобы запрос начинался с последнего времени закрытия
        if (lastCloseTime != null) {
            urlBuilder.append("&endTime=${lastCloseTime + 60000}")
        }

        val request = Request.Builder().url(urlBuilder.toString()).build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                val json = Json { ignoreUnknownKeys = true }

                try {
                    val rawCandlesticks = json.decodeFromString<JsonElement>(response.body?.string() ?: "").jsonArray
                    hasMoreData = rawCandlesticks.isNotEmpty() // Проверяем, есть ли данные

                    if (rawCandlesticks.isNotEmpty()) {
                        val candlesticks = rawCandlesticks.map {
                            val data = it.jsonArray
                            BinanceMinuteCandlestick(
                                openTime = data[0].jsonPrimitive.long,
                                open = data[1].jsonPrimitive.content,
                                high = data[2].jsonPrimitive.content,
                                low = data[3].jsonPrimitive.content,
                                close = data[4].jsonPrimitive.content,
                                volume = data[5].jsonPrimitive.content,
                                closeTime = data[6].jsonPrimitive.long,
                                quoteAssetVolume = data[7].jsonPrimitive.content,
                                numberOfTrades = data[8].jsonPrimitive.int,
                                takerBuyBaseAssetVolume = data[9].jsonPrimitive.content,
                                takerBuyQuoteAssetVolume = data[10].jsonPrimitive.content,
                                ignore = data[11].jsonPrimitive.content
                            )
                        }

                        // Конвертируем BinanceCandlestick в VKlines и добавляем в общий список
                        candlesticks.forEach { candlestick ->
                            val vkline = MinuteVKlines(
                                openTime = candlestick.openTime,
                                open = candlestick.open,
                                high = candlestick.high,
                                low = candlestick.low,
                                close = candlestick.close,
                                volume = candlestick.volume,
                                closeTime = candlestick.closeTime
                            )
                            outputVKlines.add(vkline)
                        }

                        // Обновляем lastCloseTime для следующего запроса
                        lastCloseTime = candlesticks.lastOrNull()?.closeTime
                        remaining -= candlesticks.size  // Уменьшаем оставшееся количество
                    }
                } catch (e: Exception) {
                    println("Ошибка при обработке JSON: ${e.message}")
                }
            }
        } catch (e: IOException) {
            println("Ошибка выполнения запроса: ${e.message}")
        }

        // Добавьте паузу, чтобы не превысить лимит запросов Binance API (примерно 10 запросов в секунду)
        Thread.sleep(100)  // Пауза в 100 миллисекунд
    }

    return outputVKlines.reversed()
}

data class MinuteVKlines(
    val openTime: Long,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val volume: String,
    val closeTime: Long
)

@Serializable
data class BinanceMinuteCandlestick(
    val openTime: Long,
    val open: String,
    val high: String,
    val low: String,
    val close: String,
    val volume: String,
    val closeTime: Long,
    val quoteAssetVolume: String,
    val numberOfTrades: Int,
    val takerBuyBaseAssetVolume: String,
    val takerBuyQuoteAssetVolume: String,
    val ignore: String
)