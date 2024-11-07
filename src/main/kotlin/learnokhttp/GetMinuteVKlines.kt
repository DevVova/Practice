package learnokhttp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.time.Instant
import kotlin.math.min

fun main() {
    val newList = getMinuteVKlines("SOLUSDT", limit = 5000)
    newList.forEach { println(it) }
    println(newList.size)
    println(newList[0].open)
    val diff = newList[0].openTime - newList[newList.size - 1].openTime
    println(diff)
    println()

    println(newList.size - 1)
    println(newList.lastOrNull()?.let { Instant.ofEpochMilli(it.openTime) })
}

fun getMinuteVKlines(
    symbol: String = "BTCUSDT",
    limit: Int = 1000
): List<MinuteVKlines> {
    val client = OkHttpClient()

    val outputVKlines = mutableListOf<MinuteVKlines>()//Это список для вывода окончательный.
    val intermediateVKlines = mutableListOf<BinanceMinuteCandlestick>()//Это промежуточный список свечей.

    var remaining = limit//Оставшееся количество свечей.
    var lastOpenTime: Long? = null//Это время открытия последней свечи.
    var requestLimit: Int

    while (remaining > 0) {
        requestLimit = min(1000, remaining)  // Запрашиваем максимум 1000 записей за один раз.
        //Ниже это построитель строк, который нужен для запроса.
        val url = "https://api.binance.com/api/v3/klines?symbol=$symbol&interval=1m&limit=$requestLimit"
        var request = Request.Builder().url(url).build()

        // Если есть lastOpenTime, добавляем параметр endTime, чтобы запрос начинался с последнего времени открытия.
        if (lastOpenTime != null) {
            val addUrl = "&endTime=${lastOpenTime - 60000}"
            val newUrl = url + addUrl
            request = Request.Builder().url(newUrl).build()
        }

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")

                // Обработка успешного ответа.
                val json = Json { ignoreUnknownKeys = true } // Игнорируем неизвестные ключи в ответе

                // Добавлена обработка ошибок при десериализации.
                try {
                    val rawCandlesticks = json.decodeFromString<JsonElement>(response.body?.string() ?: "").jsonArray
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
                    intermediateVKlines.addAll(0, candlesticks)
                } catch (e: Exception) {
                    // Обработка ошибки при десериализации JSON
                    println("Ошибка при обработке JSON: ${e.message}")
                }
            }
        } catch (e: IOException) {
            // Обработка ошибки запроса
            println("Ошибка выполнения запроса: ${e.message}")
        }

        // Добавьте паузу, чтобы не превысить лимит запросов Binance API (примерно 10 запросов в секунду)
        Thread.sleep(100)  // Пауза в 100 миллисекунд

        lastOpenTime = intermediateVKlines.firstOrNull()?.openTime
        remaining -= requestLimit  // Уменьшаем оставшееся количество
    }

    // Конвертируем BinanceCandlestick в VKlines и добавляем в общий список
    intermediateVKlines.forEach { intermediateVKline ->
        val vkline = MinuteVKlines(
            openTime = intermediateVKline.openTime,
            open = intermediateVKline.open,
            high = intermediateVKline.high,
            low = intermediateVKline.low,
            close = intermediateVKline.close,
            volume = intermediateVKline.volume,
            closeTime = intermediateVKline.closeTime
        )
        outputVKlines.add(vkline)
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